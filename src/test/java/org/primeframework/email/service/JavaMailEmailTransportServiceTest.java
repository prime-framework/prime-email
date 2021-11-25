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

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import org.primeframework.email.domain.Attachment;
import org.primeframework.email.domain.Email;
import org.primeframework.email.domain.EmailAddress;
import org.primeframework.email.domain.SendResult;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

/**
 * This class tests the JavaMailEmailTransportService.
 *
 * @author Brian Pontarelli
 */
@Test(groups = "unit")
public class JavaMailEmailTransportServiceTest {
  private static Session session;

  @BeforeClass
  public static void setup() {
    Properties props = new Properties();
    props.setProperty("mail.transport.protocol", "smtp");
    props.setProperty("mail.smtp.host", "localhost");
    props.setProperty("mail.host", "localhost");
    props.setProperty("mail.smtp.localhost", "localhost");

    Authenticator auth = null;

    // To test against a real SMTP server such as sendgrid, set a username & password, don't check those in please.
    String username = "";
    String password = "";

    //noinspection ConstantConditions
    if (!password.equals("")) {
      // Set the auth
      props.setProperty("mail.smtp.host", "smtp.sendgrid.net");
      props.setProperty("mail.host", "smtp.sendgrid.net");
      props.setProperty("mail.smtp.port", "" + 587);
      props.setProperty("mail.smtp.starttls.enable", "true");
      props.setProperty("mail.smtp.auth", "true");
      props.setProperty("mail.user", username);
      props.setProperty("mail.password", password);
      props.setProperty("mail.smtp.username", username);
      props.setProperty("mail.smtp.password", password);
      props.put("mail.smtp.ssl.protocols", "TLSv1 TLSv1.1 TLSv1.2");
      auth = new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(username, password);
        }
      };
    }

    session = Session.getInstance(props, auth);
  }

  @Test
  public void sendEmail() throws Exception {
    JavaMailEmailTransportService service = new JavaMailEmailTransportService(new DefaultMessagingExceptionHandler(), new TestJavaMailSessionProvider(session));
    Email email = new Email();
    email.from = new EmailAddress("dev@inversoft.com");
    email.to.add(new EmailAddress("brian@inversoft.com"));
    email.subject = "Test email";
    email.text = "text";
    email.html = "<html><body><h3>html</h3></body></html>";

    sendAndVerify(service, email);
  }

  @Test
  public void sendEmailWithAttachments() throws Exception {
    JavaMailEmailTransportService service = new JavaMailEmailTransportService(new DefaultMessagingExceptionHandler(), new TestJavaMailSessionProvider(session));
    Email email = new Email();
    email.from = new EmailAddress("brian@inversoft.com");
    email.to.add(new EmailAddress("brian@inversoft.com"));
    email.subject = "Test email";
    email.text = "text";
    email.html = "<html><body><h3>html</h3></body></html>";
    email.attachments.add(new Attachment("test.txt", "text/plain", "Hello world".getBytes()));

    sendAndVerify(service, email);
  }

  @Test
  public void sendEmailWithHeaders() throws Exception {
    JavaMailEmailTransportService service = new JavaMailEmailTransportService(new DefaultMessagingExceptionHandler(), new TestJavaMailSessionProvider(session));
    Email email = new Email();
    email.from = new EmailAddress("brett@fusionauth.io");
    email.to.add(new EmailAddress("brett@fusionauth.io"));
    email.subject = "Test email";
    email.text = "text";
    email.html = "<html><body><h3>html</h3></body></html>";
    email.headers.put("X-SES-CONFIGURATION-SET", "ConfigSet");
    email.headers.put("Return-Path", "<admin@fusionauth.io>");

    sendAndVerify(service, email);
  }

  @Test(enabled = false)
  public void send_multiByteSubjectAndBody() throws Exception {
    JavaMailEmailTransportService service = new JavaMailEmailTransportService(new DefaultMessagingExceptionHandler(), new TestJavaMailSessionProvider(session));
    Email email = new Email();
    email.from = new EmailAddress("dev@fusionauth.com");
    email.to.add(new EmailAddress("daniel@fusionauth.io"));
    email.subject = "Multi Byte ąęćń";
    email.text = "text ąęćń";
    email.html = "<html><body><h3>html</h3>ąęćń</body></html>";

    sendAndVerify(service, email);
  }

  private void sendAndVerify(JavaMailEmailTransportService service, Email email) throws Exception {
    SendResult sendResult = new SendResult(email);
    service.sendEmailLater(null, email, sendResult);

    assertNotNull(sendResult.future);
    assertSame(sendResult.future.get(), sendResult);
    assertTrue(sendResult.future.get().wasSuccessful());
  }

  public static class TestJavaMailSessionProvider implements JavaMailSessionProvider {
    private final Session session;

    TestJavaMailSessionProvider(Session session) {
      this.session = session;
    }

    @Override
    public Session get(Object contextId) {
      return session;
    }
  }
}