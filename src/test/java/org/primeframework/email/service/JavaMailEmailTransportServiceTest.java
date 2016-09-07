/*
 * Copyright (c) 2001-2016, JCatapult.org, All Rights Reserved
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

import org.primeframework.email.domain.Attachment;
import org.primeframework.email.domain.Email;
import org.primeframework.email.domain.EmailAddress;
import org.primeframework.email.domain.SendResult;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.mail.Session;
import java.util.Properties;

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
  public void sendEmail() throws Exception {
    JavaMailEmailTransportService service = new JavaMailEmailTransportService(() -> session);
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
    JavaMailEmailTransportService service = new JavaMailEmailTransportService(() -> session);
    Email email = new Email();
    email.from = new EmailAddress("brian@inversoft.com");
    email.to.add(new EmailAddress("brian@inversoft.com"));
    email.subject = "Test email";
    email.text = "text";
    email.html = "<html><body><h3>html</h3></body></html>";
    email.attachments.add(new Attachment("test.txt", "text/plain", "Hello world".getBytes()));

    sendAndVerify(service, email);
  }

  private void sendAndVerify(JavaMailEmailTransportService service, Email email) throws Exception {
    SendResult sendResult = new SendResult(email);
    service.sendEmailLater(email, sendResult);

    assertNotNull(sendResult.future);
    assertSame(sendResult.future.get(), sendResult);
    assertTrue(sendResult.future.get().wasSuccessful());
  }
}