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
package org.primeframework.email;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.primeframework.email.domain.Email;
import org.primeframework.email.service.EmailTransportService;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

/**
 * This class provides tests with the ability to setup email handling.
 * <p/>
 * <b>NOTE:</b> This class is thread safe and can be used in parallel test cases.
 *
 * @author Brian Pontarelli
 */
public class EmailTestHelper {
  private static ThreadLocal<Queue<Email>> emailResult = new ThreadLocal<Queue<Email>>();
  private static ThreadLocal<Future<Email>> future = new ThreadLocal<Future<Email>>();
  private static EmailTransportService service;

  /**
   * Returns the email results for the last test run. This is a thread safe retrieval.
   *
   * @return The email results or null if there isn't any.
   */
  public static Queue<Email> getEmailResults() {
    return emailResult.get();
  }

  /**
   * Mocks out an {@link EmailTransportService} so that an SMTP server is not required to run the tests. This will mock
   * return the mail and also provides the emails via the {@link #getEmailResults()} method on this class. This class is
   * thread safe and can be used in parallel test cases.
   *
   * @return A Guice module that contains the email transport service mock. This module should be used with the injector
   *         for the test cases.
   */
  public static Module setup() {
    emailResult.remove();
    future.set(new MockFuture(false));

    service = new EmailTransportService() {
      public Future<Email> sendEmail(Email email) {
        if (emailResult.get() == null) {
          emailResult.set(new LinkedList<Email>());
        }

        emailResult.get().offer(email);
        return future.get();
      }

      public void sendEmailLater(Email email) {
        if (emailResult.get() == null) {
          emailResult.set(new LinkedList<Email>());
        }

        emailResult.get().offer(email);
      }
    };

    return new AbstractModule() {
      protected void configure() {
        bind(EmailTransportService.class).toInstance(service);
      }
    };
  }

  /**
   * @return Returns the mocked out EmailTransportService.
   */
  public static EmailTransportService getService() {
    return service;
  }

  /**
   * This method informs the mock that it should simulate a timeout when sending an email.
   */
  public static void timeout() {
    future.set(new MockFuture(true));
  }

  /**
   * This method informs the mock that it should reset itself and NOT simulate a timeout when sending an email.
   */
  public static void reset() {
    future.set(new MockFuture(false));
  }

  public static class MockFuture implements Future<Email> {
    private final boolean timeout;
    private boolean cancelled;

    public MockFuture(boolean timeout) {
      this.timeout = timeout;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
      cancelled = true;
      return true;
    }

    public boolean isCancelled() {
      return cancelled;
    }

    public boolean isDone() {
      return !timeout;
    }

    public Email get() throws InterruptedException, ExecutionException {
      if (timeout) {
        throw new AssertionError("Timeout set and get() was called. You should be calling " +
          "get(long, TimeUnit) from you code.");
      }
      return emailResult.get().poll();
    }

    public Email get(long duration, TimeUnit unit) throws TimeoutException {
      if (timeout) {
        throw new TimeoutException("Timeout");
      }
      return emailResult.get().poll();
    }
  }
}