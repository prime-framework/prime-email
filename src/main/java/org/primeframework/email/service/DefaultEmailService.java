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

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.inject.Inject;
import org.primeframework.email.domain.Email;
import org.primeframework.email.domain.ParsedEmailTemplates;
import org.primeframework.email.domain.PreviewResult;
import org.primeframework.email.domain.RawEmailTemplates;
import org.primeframework.email.domain.SendResult;
import org.primeframework.email.domain.ValidateResult;

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
  public PreviewEmailBuilder preview(Object contextId, RawEmailTemplates rawEmailTemplates) {
    return new PreviewEmailBuilder(contextId, null, new Email(),
        (previewEmailBuilder) -> preview(rawEmailTemplates, previewEmailBuilder));
  }

  @Override
  public PreviewEmailBuilder preview(Object contextId, Object templateId, List<Locale> preferredLanguages) {
    return new PreviewEmailBuilder(contextId, templateId, new Email(),
        (previewEmailBuilder) -> preview(contextId, templateId, preferredLanguages, previewEmailBuilder));
  }

  /**
   * {@inheritDoc}
   */
  public SendEmailBuilder send(Object contextId, Object templateId, List<Locale> preferredLanguages) {
    return new SendEmailBuilder(contextId, templateId, new Email(),
        (sendEmailBuilder) -> sendLater(contextId, templateId, preferredLanguages, sendEmailBuilder),
        (sendEmailBuilder) -> send(contextId, templateId, preferredLanguages, sendEmailBuilder));
  }

  @Override
  public ValidateResult validate(Object contextId, RawEmailTemplates rawEmailTemplates,
                                 Map<String, Object> parameters) {
    ValidateResult validateResult = new ValidateResult();
    ParsedEmailTemplates parsedEmailTemplates = emailTemplateLoader.parse(rawEmailTemplates, validateResult);
    emailRenderer.render(parsedEmailTemplates, new Email(), parameters, validateResult);
    return validateResult;
  }

  private PreviewResult preview(RawEmailTemplates rawEmailTemplates, PreviewEmailBuilder previewEmailBuilder) {
    PreviewResult previewResult = new PreviewResult(previewEmailBuilder.getEmail());
    ParsedEmailTemplates parsedEmailTemplates = emailTemplateLoader.parse(rawEmailTemplates, previewResult);
    emailRenderer.render(parsedEmailTemplates, previewEmailBuilder.getEmail(), previewEmailBuilder.getParameters(), previewResult);
    return previewResult;
  }

  private PreviewResult preview(Object contextId, Object templateId, List<Locale> preferredLanguages,
                                PreviewEmailBuilder previewEmailBuilder) {
    PreviewResult previewResult = new PreviewResult(previewEmailBuilder.getEmail());
    ParsedEmailTemplates parsedEmailTemplates = emailTemplateLoader.load(contextId, templateId, preferredLanguages, previewResult);
    emailRenderer.render(parsedEmailTemplates, previewEmailBuilder.getEmail(), previewEmailBuilder.getParameters(), previewResult);
    return previewResult;
  }

  private SendResult send(Object contextId, Object templateId, List<Locale> preferredLanguages,
                          SendEmailBuilder sendEmailBuilder) {
    SendResult sendResult = new SendResult(sendEmailBuilder.getEmail());
    ParsedEmailTemplates parsedEmailTemplates = emailTemplateLoader.load(contextId, templateId, preferredLanguages, sendResult);
    emailRenderer.render(parsedEmailTemplates, sendEmailBuilder.getEmail(), sendEmailBuilder.getParameters(), sendResult);
    emailTransportService.sendEmail(contextId, sendEmailBuilder.getEmail(), sendResult);
    return sendResult;
  }

  private SendResult sendLater(Object contextId, Object templateId, List<Locale> preferredLanguages,
                               SendEmailBuilder sendEmailBuilder) {
    SendResult sendResult = new SendResult(sendEmailBuilder.getEmail());
    ParsedEmailTemplates parsedEmailTemplates = emailTemplateLoader.load(contextId, templateId, preferredLanguages, sendResult);
    emailRenderer.render(parsedEmailTemplates, sendEmailBuilder.getEmail(), sendEmailBuilder.getParameters(), sendResult);
    emailTransportService.sendEmailLater(contextId, sendEmailBuilder.getEmail(), sendResult);
    return sendResult;
  }
}