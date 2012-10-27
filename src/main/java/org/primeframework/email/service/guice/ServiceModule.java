/*
 * Copyright (c) 2012, Inversoft Inc., All Rights Reserved
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
package org.primeframework.email.service.guice;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.primeframework.email.guice.Email;
import org.primeframework.email.service.EmailService;
import org.primeframework.email.service.EmailTransportService;
import org.primeframework.email.service.FreeMarkerEmailService;
import org.primeframework.email.service.JavaMailEmailTransportService;

import com.google.inject.AbstractModule;

/**
 * Binds the email services and the executor that is used to send mail asynchronously.
 *
 * @author Brian Pontarelli
 */
public class ServiceModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(EmailService.class).to(FreeMarkerEmailService.class);
    bind(EmailTransportService.class).to(JavaMailEmailTransportService.class);
    bind(ExecutorService.class).annotatedWith(Email.class).toInstance(new ThreadPoolExecutor(1, 5, 500,
      TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
      new RejectedExecutionHandler() {
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
          throw new RejectedExecutionException("An email task was rejected.");
        }
      }));
  }
}
