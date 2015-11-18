/*
 * Copyright (c) 2001-2007, JCatapult.org, All Rights Reserved
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
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

import com.google.inject.Inject;
import org.primeframework.email.EmailDataException;
import org.primeframework.email.EmailTransportException;
import org.primeframework.email.domain.Attachment;
import org.primeframework.email.domain.Email;
import org.primeframework.email.domain.EmailAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements the {@link EmailTransportService} interface using the JavaMail API and a JavaMail session.
 *
 * @author Brian Pontarelli
 */
public class JavaMailEmailTransportService implements EmailTransportService {
  private ExecutorService executorService;

  private Session session;

  /**
   * Constructs the transport service.
   *
   * @param session The Java mail session.
   */
  @Inject
  public JavaMailEmailTransportService(Session session) {
    this.session = session;
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
  public Future<Email> sendEmail(Email email) {
    try {
      return executorService.submit(new EmailRunnable(message(email), session), email);
    } catch (RejectedExecutionException ree) {
      throw new EmailTransportException("Unable to submit the JavaMail message to the asynchronous handler " +
          "so that it can be processed at a later time. The email was therefore not sent.", ree);
    }
  }

  public void sendEmailLater(Email email) {
    try {
      executorService.execute(new EmailRunnable(message(email), session));
    } catch (RejectedExecutionException ree) {
      throw new EmailTransportException("Unable to submit the JavaMail message to the asynchronous handler " +
          "so that it can be processed at a later time. The email was therefore not sent.", ree);
    }
  }

  private Message message(Email email) {
    try {
      // Define message
      Message message = new MimeMessage(session);
      EmailAddress from = email.from;
      if (from == null) {
        throw new EmailDataException("email message 'from' not set");
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
        throw new EmailDataException("email message must contain at least one CC, BCC, or To recipient");
      }

      String subject = email.subject;
      if (subject != null) {
        message.setSubject(subject);
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
      throw new EmailDataException("An error occurred while trying to construct the JavaMail Message object", e);
    } catch (UnsupportedEncodingException e) {
      throw new EmailDataException("Unable to create email addresses. The email was therefore not sent.", e);
    }
  }

  /**
   * The callable for handling async message sending.
   */
  public static class EmailRunnable implements Runnable {
    private final static Logger logger = LoggerFactory.getLogger(EmailRunnable.class);

    private Message message;

    private Session session;

    public EmailRunnable(Message message, Session session) {
      this.message = message;
      this.session = session;
    }

    public void run() {
      try {
        logger.debug("Sending mail to JavaMail API");

        Transport transport = session.getTransport();
        transport.connect();
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();

        logger.debug("Finished JavaMail send");
      } catch (MessagingException e) {
        logger.error("Unable to send email via JavaMail", e);
        throw new EmailTransportException("Unable to send email via JavaMail", e);
      }
    }
  }
}