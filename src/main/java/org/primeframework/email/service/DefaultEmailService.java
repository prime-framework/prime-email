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

import com.google.inject.Inject;
import org.primeframework.email.EmailException;
import org.primeframework.email.EmailTemplateException;
import org.primeframework.email.domain.Email;
import org.primeframework.email.domain.ParsedEmailTemplates;
import org.primeframework.email.domain.RawEmailTemplates;

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
  public EmailBuilder preview(RawEmailTemplates rawEmailTemplates) throws EmailException {
    return new EmailBuilder(null, new Email(),
        (emailBuilder) -> {
          throw new EmailException("You can't preview emails in the future");
        },
        (emailBuilder) -> preview(rawEmailTemplates, emailBuilder.getEmail(), emailBuilder.getParams()));
  }

  @Override
  public EmailBuilder preview(Object templateId, List<Locale> preferredLanguages) throws EmailException {
    return new EmailBuilder(null, new Email(),
        (emailBuilder) -> {
          throw new EmailException("You can't preview emails in the future");
        },
        (emailBuilder) -> preview(templateId, preferredLanguages, emailBuilder.getEmail(), emailBuilder.getParams()));
  }

  /**
   * {@inheritDoc}
   */
  public EmailBuilder send(Object templateId, List<Locale> preferredLanguages) {
    return new EmailBuilder(templateId, new Email(),
        (emailBuilder) -> sendLater(templateId, emailBuilder.getEmail(), emailBuilder.getParams(), preferredLanguages),
        (emailBuilder) -> send(templateId, emailBuilder.getEmail(), emailBuilder.getParams(), preferredLanguages));
  }

  @Override
  public void validate(RawEmailTemplates rawEmailTemplates, Map<String, Object> parameters) throws EmailTemplateException {
    ParsedEmailTemplates parsedEmailTemplates = emailTemplateLoader.parse(rawEmailTemplates);
    emailRenderer.render(parsedEmailTemplates, new Email(), parameters);
  }

  private Email preview(RawEmailTemplates rawEmailTemplates, Email email, Map<String, Object> parameters) {
    ParsedEmailTemplates parsedEmailTemplates = emailTemplateLoader.parse(rawEmailTemplates);
    emailRenderer.render(parsedEmailTemplates, email, parameters);
    return email;
  }

  private Email preview(Object templateId, List<Locale> preferredLanguages, Email email, Map<String, Object> parameters) {
    ParsedEmailTemplates parsedEmailTemplates = emailTemplateLoader.load(templateId, preferredLanguages);
    emailRenderer.render(parsedEmailTemplates, email, parameters);
    return email;
  }

  /**
   * This call back executes the templates and then sends the email using the transport service that is set in the
   * constructor.
   *
   * @param templateId         The id of the template to execute.
   * @param email              The email data from the EmailBuilder.
   * @param parameters             The params that are sent to the template.
   * @param preferredLanguages The preferred languages to render the email template in.
   * @return The future from the transport.
   */
  private Email send(Object templateId, Email email, Map<String, Object> parameters, List<Locale> preferredLanguages) {
    ParsedEmailTemplates parsedEmailTemplates = emailTemplateLoader.load(templateId, preferredLanguages);
    emailRenderer.render(parsedEmailTemplates, email, parameters);
    emailTransportService.sendEmail(email);
    return email;
  }

  /**
   * This call back executes the templates and then sends the email using the transport service that is set in the
   * constructor.
   *
   * @param templateId         The id of the template to execute.
   * @param email              The email to add the text to and send.
   * @param parameters             The params that are sent to the template.
   * @param preferredLanguages The preferred languages to render the email template in.
   */
  private Future<Email> sendLater(Object templateId, Email email, Map<String, Object> parameters, List<Locale> preferredLanguages) {
    ParsedEmailTemplates parsedEmailTemplates = emailTemplateLoader.load(templateId, preferredLanguages);
    emailRenderer.render(parsedEmailTemplates, email, parameters);
    return emailTransportService.sendEmailLater(email);
  }
}