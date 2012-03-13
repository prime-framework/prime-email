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

import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.primeframework.email.config.EmailConfiguration;
import org.primeframework.email.domain.Email;
import org.primeframework.email.domain.EmailAddress;
import org.testng.annotations.Test;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import static org.testng.Assert.*;

/**
 * This class tests the FreeMarker email service.
 *
 * @author Brian Pontarelli
 */
public class FreeMarkerEmailServiceTest {
  @Test
  public void sendEmailClassPath() throws Exception {
    BeansWrapper wrapper = new BeansWrapper();
    wrapper.setExposeFields(true);
    Configuration config = new Configuration();
    config.setObjectWrapper(wrapper);

    MockEmailTransportService transport = new MockEmailTransportService();
    FreeMarkerEmailService service = new FreeMarkerEmailService(transport, config, new TestEmailConfiguration(), Locale.US);
    service.sendEmail("test-template").
      cc(new EmailAddress("from@example.com")).
      bcc(new EmailAddress("from@example.com")).
      withSubject("test subject").
      from(new EmailAddress("from@example.com")).
      to(new EmailAddress("to@example.com")).
      withTemplateParam("key1", "value1").
      now();
    assertEquals(transport.email.subject, "test subject");
    assertEquals(transport.email.from.address, "from@example.com");
    assertEquals(transport.email.to.get(0).address, "to@example.com");
    assertEquals(transport.email.text, "Text value1");
    assertEquals(transport.email.html, "HTML value1");
  }

  @Test
  public void sendEmailWebApp() throws Exception {
    BeansWrapper wrapper = new BeansWrapper();
    wrapper.setExposeFields(true);
    Configuration config = new Configuration();
    config.setObjectWrapper(wrapper);

    MockEmailTransportService transport = new MockEmailTransportService();
    FreeMarkerEmailService service = new FreeMarkerEmailService(transport, config, new TestEmailConfiguration(), Locale.US);
    service.sendEmail("test-template").
      cc(new EmailAddress("from@example.com")).
      bcc(new EmailAddress("from@example.com")).
      withSubject("test subject").
      from(new EmailAddress("from@example.com")).
      to(new EmailAddress("to@example.com")).
      withTemplateParam("key1", "value1").
      now();
    assertEquals(transport.email.subject, "test subject");
    assertEquals(transport.email.from.address, "from@example.com");
    assertEquals(transport.email.to.get(0).address, "to@example.com");
    assertEquals(transport.email.text, "Text value1");
    assertEquals(transport.email.html, "HTML value1");
  }

  @Test
  public void sendTemplatedEmail() throws Exception {
    Bean bean = new Bean();
    bean.name = "frank";
    bean.bean2 = new Bean2();
    bean.bean2.hobby = "fishing";

    BeansWrapper wrapper = new BeansWrapper();
    wrapper.setExposeFields(true);
    Configuration config = new Configuration();
    config.setObjectWrapper(wrapper);

    MockEmailTransportService transport = new MockEmailTransportService();
    FreeMarkerEmailService service = new FreeMarkerEmailService(transport, config, new TestEmailConfiguration(), Locale.US);
    service.sendEmail("test-template-with-bean").
      cc(new EmailAddress("from@example.com")).
      bcc(new EmailAddress("from@example.com")).
      withSubject("test subject").
      from(new EmailAddress("from@example.com")).
      to(new EmailAddress("to@example.com")).
      withTemplateParam("bean", bean).
      now();

    assertEquals(transport.email.subject, "test subject");
    assertEquals(transport.email.from.address, "from@example.com");
    assertEquals(transport.email.to.get(0).address, "to@example.com");
    assertEquals(transport.email.text, "Text frank likes fishing");
    assertEquals(transport.email.html, "HTML frank likes fishing");
  }

  public static class MockEmailTransportService implements EmailTransportService {
    public Email email;

    public Future<Email> sendEmail(Email email) {
      this.email = email;
      return new Future<Email>() {
        public boolean cancel(boolean mayInterruptIfRunning) {
          return false;
        }

        public boolean isCancelled() {
          return false;
        }

        public boolean isDone() {
          return false;
        }

        public Email get() throws InterruptedException, ExecutionException {
          return null;
        }

        public Email get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
          return null;
        }
      };
    }

    public void sendEmailLater(Email email) {
      this.email = email;
    }
  }
  
  public static class TestEmailConfiguration implements EmailConfiguration {
    @Override
    public String templateLocation() {
      return "src/java/test/unit/org/primeframework/email";
    }
  }
}