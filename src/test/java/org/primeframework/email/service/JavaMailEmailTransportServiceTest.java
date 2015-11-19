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

import javax.mail.Session;
import java.util.Properties;
import java.util.concurrent.Future;

import org.primeframework.email.domain.Attachment;
import org.primeframework.email.domain.Email;
import org.primeframework.email.domain.EmailAddress;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

/**
 * This class tests the JavaMailEmailTransportService.
 *
 * @author Brian Pontarelli
 */
@Test(groups = "unit")
public class JavaMailEmailTransportServiceTest {
  private static Session session;

  @BeforeClass
  public static void setupJNDI() {
    // Set the auth
    Properties props = new Properties();
    props.setProperty("mail.smtp.host", "localhost");
    props.setProperty("mail.host", "localhost");

    // Set the protocol for the JavaMail client
    props.setProperty("mail.transport.protocol", "smtp");
    props.setProperty("mail.smtp.localhost", "localhost");

    session = Session.getInstance(props, null);
  }

  @Test
  public void sendEmail() {
    JavaMailEmailTransportService service = new JavaMailEmailTransportService(session);
    Email email = new Email();
    email.from = new EmailAddress("james@inversoft.com");
    email.to.add(new EmailAddress("brian@inversoft.com"));
    email.subject = "Test email";
    email.text = "text";
    email.html = "<html><body><h3>html</h3></body></html>";

    try {
      Future<Email> future = service.sendEmailLater(email);
      assertNotNull(future);
      future.get();
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unable to send emails.  Are you running a SMTP server?  Try executing: sudo postfix start");
    }
  }

  @Test
  public void sendEmailWithAttachments() throws Exception {
    JavaMailEmailTransportService service = new JavaMailEmailTransportService(session);
    Email email = new Email();
    email.from = new EmailAddress("brian@inversoft.com");
    email.to.add(new EmailAddress("brian@inversoft.com"));
    email.subject = "Test email";
    email.text = "text";
    email.html = "<html><body><h3>html</h3></body></html>";
    email.attachments.add(new Attachment("test.txt", "text/plain", "Hello world".getBytes()));

    try {
      Future<Email> future = service.sendEmailLater(email);
      assertNotNull(future);
      future.get();
    } catch (Exception e) {
      fail("Unable to send emails.  Are you running a SMTP server?  Try executing: sudo postfix start");
    }
  }
}