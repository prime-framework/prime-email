/*
 * Copyright (c) 2001-2019, JCatapult.org, All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.primeframework.email.service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import com.google.inject.Inject;
import org.primeframework.email.domain.Attachment;
import org.primeframework.email.domain.Email;
import org.primeframework.email.domain.EmailAddress;
import org.primeframework.email.domain.SendResult;
import org.primeframework.email.service.MessagingExceptionHandler.PrimeMessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements the {@link EmailTransportService} interface using the JavaMail API and a JavaMail
 * sessionProvider.
 *
 * @author Brian Pontarelli
 */
public class JavaMailEmailTransportService implements EmailTransportService {
  private final ExecutorService executorService;

  private final MessagingExceptionHandler messagingExceptionHandler;

  private final JavaMailSessionProvider sessionProvider;

  /**
   * Constructs the transport service.
   *
   * @param sessionProvider The Java mail session provider.
   */
  @Inject
  public JavaMailEmailTransportService(MessagingExceptionHandler messagingExceptionHandler,
                                       JavaMailSessionProvider sessionProvider) {
    this.messagingExceptionHandler = messagingExceptionHandler;
    this.sessionProvider = sessionProvider;
    this.executorService = Executors.newCachedThreadPool(
        (r) -> {
          Thread t = new Thread(r, "Prime-Email Executor Thread");
          t.setDaemon(true);
          return t;
        }
    );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void sendEmail(Object contextId, Email email, SendResult sendResult) {
    sendEmail(contextId, email, sendResult, messagingExceptionHandler);
  }

  @Override
  public void sendEmail(Object contextId, Email email, SendResult sendResult,
                        MessagingExceptionHandler messagingExceptionHandler) {
    Session session = sessionProvider.get(contextId);
    EmailRunnable runnable = new EmailRunnable(contextId, message(email, sendResult, session), sendResult, messagingExceptionHandler);
    if (sendResult.wasSuccessful()) {
      runnable.run();
    }
  }

  @Override
  public void sendEmailLater(Object contextId, Email email, SendResult sendResult) {
    sendEmailLater(contextId, email, sendResult, messagingExceptionHandler);
  }

  @Override
  public void sendEmailLater(Object contextId, Email email, SendResult sendResult,
                             MessagingExceptionHandler messagingExceptionHandler) {
    Session session = sessionProvider.get(contextId);
    EmailRunnable runnable = new EmailRunnable(contextId, message(email, sendResult, session), sendResult, messagingExceptionHandler);
    if (sendResult.wasSuccessful()) {
      try {
        sendResult.future = executorService.submit(runnable, sendResult);
      } catch (RejectedExecutionException ree) {
        sendResult.transportError = "Unable to submit the JavaMail message to the asynchronous handler " +
            "so that it can be processed at a later time. The email was therefore not sent.";
      }
    }
  }

  private Message message(Email email, SendResult sendResult, Session session) {
    MimeMessage message = new MimeMessage(session);

    try {
      // Define message
      EmailAddress from = email.from;
      if (from == null) {
        sendResult.transportError = "email message 'from' not set";
        return message;
      }
      message.setFrom(new InternetAddress(from.address, from.display, "UTF-8"));

      // Setup the reply to
      if (email.replyTo != null) {
        message.setReplyTo(new InternetAddress[]{new InternetAddress(email.replyTo.address, email.replyTo.display, "UTF-8")});
      }

      List<EmailAddress> toList = email.to;
      for (EmailAddress to : toList) {
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to.address, to.display, "UTF-8"));
      }

      List<EmailAddress> ccList = email.cc;
      for (EmailAddress cc : ccList) {
        message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc.address, cc.display, "UTF-8"));
      }

      List<EmailAddress> bccList = email.bcc;
      for (EmailAddress bcc : bccList) {
        message.addRecipient(Message.RecipientType.BCC, new InternetAddress(bcc.address, bcc.display, "UTF-8"));
      }

      if (message.getAllRecipients() == null || message.getAllRecipients().length == 0) {
        sendResult.transportError = "email message must contain at least one CC, BCC, or To recipient";
        return message;
      }

      String subject = email.subject;
      if (subject != null) {
        message.setSubject(subject, "UTF-8");
      }

      // Determine the email content type and if we need to include the text version
      String type = "alternative";
      boolean includeText = true;
      if (email.attachments.size() > 0) {
        type = "mixed";
        includeText = false;
      }

      // Get the text version if there is a template
      Multipart mp = new MimeMultipart(type);
      String text = email.text;
      if (includeText && text != null) {
        BodyPart textPart = new MimeBodyPart();
        textPart.setText(text);
        mp.addBodyPart(textPart);
      }

      // Handle the HTML version if there is one
      String html = email.html;
      if (html != null) {
        BodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(html, "text/html; charset=UTF-8");
        mp.addBodyPart(htmlPart);
      }

      // Part two is attachment
      List<Attachment> attachments = email.attachments;
      for (Attachment attachment : attachments) {
        BodyPart attachPart = new MimeBodyPart();
        DataSource source = new ByteArrayDataSource(attachment.attachment, attachment.mime);
        attachPart.setDataHandler(new DataHandler(source));
        attachPart.setFileName(attachment.name);
        mp.addBodyPart(attachPart);
      }

      // Set the multipart content
      message.setContent(mp);
      return message;
    } catch (MessagingException e) {
      sendResult.transportError = "An error occurred while trying to construct the JavaMail Message object";
      return message;
    } catch (UnsupportedEncodingException e) {
      sendResult.transportError = "Unable to create email addresses. The email was therefore not sent.";
      return message;
    }
  }

  /**
   * The callable for handling async message sending.
   */
  public static class EmailRunnable implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(EmailRunnable.class);

    private final Message message;

    private final MessagingExceptionHandler messagingExceptionHandler;

    private final SendResult sendResult;

    private final Object contextId;

    public EmailRunnable(Object contextId, Message message, SendResult sendResult,
                         MessagingExceptionHandler messagingExceptionHandler) {
      this.contextId = contextId;
      this.message = message;
      this.messagingExceptionHandler = messagingExceptionHandler;
      this.sendResult = sendResult;
    }

    public void run() {
      try {
        logger.debug("Sending mail to JavaMail API");
        Transport.send(message);
        logger.debug("Finished JavaMail send");
      } catch (MessagingException e) {
        messagingExceptionHandler.handle(new PrimeMessagingException(e, contextId, sendResult));
        sendResult.transportError = "Unable to send email via JavaMail";
      }
    }
  }
}