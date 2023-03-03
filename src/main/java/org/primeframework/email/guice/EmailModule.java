/*
 * Copyright (c) 2012-2023, Inversoft Inc., All Rights Reserved
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
package org.primeframework.email.guice;

import java.util.concurrent.ExecutorService;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import org.primeframework.email.config.DefaultEmailConfiguration;
import org.primeframework.email.config.EmailConfiguration;
import org.primeframework.email.service.DefaultEmailService;
import org.primeframework.email.service.DefaultMessagingExceptionHandler;
import org.primeframework.email.service.EmailExecutorServiceProvider;
import org.primeframework.email.service.EmailRenderer;
import org.primeframework.email.service.EmailService;
import org.primeframework.email.service.EmailTemplateLoader;
import org.primeframework.email.service.EmailTransportService;
import org.primeframework.email.service.FreeMarkerEmailRenderer;
import org.primeframework.email.service.JavaMailEmailTransportService;
import org.primeframework.email.service.MessagingExceptionHandler;

/**
 * Binds all the services and configuration objects for emailing.
 *
 * @author Brian Pontarelli
 */
public abstract class EmailModule extends AbstractModule {
  /**
   * Implement this method to bind the {@link org.primeframework.email.service.MessagingExceptionHandler} interface.
   */
  protected void bindMessagingExceptionHandler() {
    bind(MessagingExceptionHandler.class).to(DefaultMessagingExceptionHandler.class);
  }

  /**
   * Implement this method to bind a JavaMail Session Provider (or the session directly if you want).
   */
  protected abstract void bindSessionProvider();

  /**
   * Implement this method to bind the {@link EmailTemplateLoader} interface.
   */
  protected abstract void bindTemplateLoader();

  @Override
  protected void configure() {
    bind(EmailConfiguration.class).to(DefaultEmailConfiguration.class);
    bind(EmailService.class).to(DefaultEmailService.class);
    bind(EmailRenderer.class).to(FreeMarkerEmailRenderer.class);
    bind(EmailTransportService.class).to(JavaMailEmailTransportService.class);

    // Bind a singleton provider
    bind(ExecutorService.class).annotatedWith(Names.named("EmailExecutorService")).toProvider(EmailExecutorServiceProvider.class).in(Scopes.SINGLETON);

    bindSessionProvider();
    bindTemplateLoader();
    bindMessagingExceptionHandler();
  }
}
