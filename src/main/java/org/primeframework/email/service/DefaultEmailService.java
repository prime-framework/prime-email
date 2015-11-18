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

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.google.inject.Inject;
import org.primeframework.email.EmailException;
import org.primeframework.email.domain.Email;

/**
 * This class implements the {@link EmailService} interface controls the flow of configuring the emails. The {@link
 * EmailRenderer} is used to load additional information for the Email before it is sent. This allows for templatized
 * and localized emails to be stored in a database or on the file system.
 *
 * @author Brian Pontarelli
 */
public class DefaultEmailService implements EmailService {
  private final EmailRenderer emailRenderer;

  private final EmailTemplateLoader emailTemplateLoader;

  private final EmailTransportService emailTransportService;

  /**
   * Constructs a DefaultEmailService. The transport given is used to send the emails.
   *
   * @param emailRenderer         The email template renderer.
   * @param emailTemplateLoader   The template loader.
   * @param emailTransportService Used to send emails.
   */
  @Inject
  public DefaultEmailService(EmailRenderer emailRenderer, EmailTemplateLoader emailTemplateLoader,
                             EmailTransportService emailTransportService) {
    this.emailTemplateLoader = emailTemplateLoader;
    this.emailTransportService = emailTransportService;
    this.emailRenderer = emailRenderer;
  }

  @Override
  public EmailBuilder preview(Email email) throws EmailException {
    return new EmailBuilder(null, email,
        (emailBuilder) -> preview(emailBuilder.getEmail(), emailBuilder.getParams()),
        (emailBuilder) -> {
        });
  }

  @Override
  public EmailBuilder render(Object templateId, List<Locale> preferredLanguages) throws EmailException {
    return new EmailBuilder(templateId, new Email(),
        (emailBuilder) -> render(templateId, emailBuilder.getEmail(), emailBuilder.getParams(), preferredLanguages),
        (emailBuilder) -> {
        });
  }

  /**
   * {@inheritDoc}
   */
  public EmailBuilder send(Object templateId, List<Locale> preferredLanguages) {
    return new EmailBuilder(templateId, new Email(),
        (emailBuilder) -> send(templateId, emailBuilder.getEmail(), emailBuilder.getParams(), preferredLanguages),
        (emailBuilder) -> sendLater(templateId, emailBuilder.getEmail(), emailBuilder.getParams(), preferredLanguages));
  }

  /**
   * This call back builds a preview of the templates but does not send the email.
   *
   * @param email  The email template to preview.
   * @param params The params that are sent to the template.
   * @return The future from the transport.
   */
  private Future<Email> preview(Email email, Map<String, Object> params) {
    emailRenderer.render(email, params);
    return new EmailFuture(email);
  }

  /**
   * This call back renders the templates but does not send it.
   *
   * @param templateId         The id of the template to execute.
   * @param email              The email data from the EmailBuilder.
   * @param params             The params that are sent to the template.
   * @param preferredLanguages The preferred languages to render the email template in.
   * @return The future from the transport.
   */
  private Future<Email> render(Object templateId, Email email, Map<String, Object> params,
                               List<Locale> preferredLanguages) {
    emailTemplateLoader.load(templateId, email, preferredLanguages);
    emailRenderer.render(email, params);
    return new EmailFuture(email);
  }

  /**
   * This call back executes the templates and then sends the email using the transport service that is set in the
   * constructor.
   *
   * @param templateId         The id of the template to execute.
   * @param email              The email data from the EmailBuilder.
   * @param params             The params that are sent to the template.
   * @param preferredLanguages The preferred languages to render the email template in.
   * @return The future from the transport.
   */
  private Future<Email> send(Object templateId, Email email, Map<String, Object> params,
                             List<Locale> preferredLanguages) {
    emailTemplateLoader.load(templateId, email, preferredLanguages);
    emailRenderer.render(email, params);
    return emailTransportService.sendEmail(email);
  }

  /**
   * This call back executes the templates and then sends the email using the transport service that is set in the
   * constructor.
   *
   * @param templateId         The id of the template to execute.
   * @param email              The email to add the text to and send.
   * @param params             The params that are sent to the template.
   * @param preferredLanguages The preferred languages to render the email template in.
   */
  private void sendLater(Object templateId, Email email, Map<String, Object> params,
                         List<Locale> preferredLanguages) {
    emailTemplateLoader.load(templateId, email, preferredLanguages);
    emailRenderer.render(email, params);
    emailTransportService.sendEmailLater(email);
  }

  private static class EmailFuture implements Future<Email> {
    private final Email email;

    public EmailFuture(Email email) {
      this.email = email;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
      return true;
    }

    @Override
    public Email get() {
      return email;
    }

    @Override
    public Email get(long timeout, TimeUnit unit) {
      return email;
    }

    @Override
    public boolean isCancelled() {
      return false;
    }

    @Override
    public boolean isDone() {
      return true;
    }
  }
}