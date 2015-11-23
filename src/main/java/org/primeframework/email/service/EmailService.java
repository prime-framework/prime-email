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

import org.primeframework.email.domain.RawEmailTemplates;
import org.primeframework.email.domain.ValidateResult;

/**
 * This interface defines how to send emails in a simple and templatized manner.
 *
 * @author Brian Pontarelli and James Humphrey
 */
public interface EmailService {
  /**
   * Builds a preview of an email using raw templates. These templates are passed in rather than loaded using the {@link
   * EmailTemplateLoader}. etc.
   * <p>
   * Here's an example using this method:
   * <p>
   * <pre>
   * EmailTemplates emailTemplates = new EmailTemplates()
   * emailTemplates.text = "Some template ${name}";
   * Email email = emailService.preview()
   *                           .withTemplateParameter("name", "Joe Blow")
   *                           .to("joe@blow.com")
   *                           .from("info@example.com")
   *                           .now();
   * System.out.println(email.text);
   * </pre>
   *
   * @param rawEmailTemplates (Required) The un-rendered email template.
   * @return The EmailBuilder that is used to build up the email configuration and parameters.
   */
  PreviewEmailBuilder preview(RawEmailTemplates rawEmailTemplates);

  /**
   * Builds a preview of an email using the templateId that is loaded via the {@link EmailTemplateLoader}. Once the
   * template is loaded, it is then rendered and returned as an Email (only by calling the now() methods on the
   * EmailBuilder).
   * <p>
   * Here's an example using the template below named 'hello':
   * <p>
   * <pre>
   * Hello ${name},
   *
   * Thanks for buying all that cool stuff.
   * </pre>
   * <p>
   * You would call this method like this:
   * <p>
   * <pre>
   * Email email = emailService.preview("hello", emptyList())
   *                           .withTemplateParam("name", "Joe Blow")
   *                           .to("joe@blow.com")
   *                           .from("info@example.com")
   *                           .now();
   * System.out.println(email.html);
   * </pre>
   *
   * @param templateId         (Required) The id of the template. The implementation will dictate the type of template
   *                           and how they are stored.
   * @param preferredLanguages The preferred languages to send them email in.
   * @return The EmailBuilder that is used to build up the email configuration and parameters.
   */
  PreviewEmailBuilder preview(Object templateId, List<Locale> preferredLanguages);

  /**
   * Sends an email using the email template loaded by passing the templateId to the {@link EmailTemplateLoader}. Once
   * the template is loaded, it is rendered and combine with the data from the {@link BaseEmailBuilder}.
   * <p>
   * Here's an example using the template below named 'hello':
   * <p>
   * <pre>
   * Hello ${name},
   *
   * Thanks for buying all that cool stuff.
   * </pre>
   * <p>
   * You would call this method like this:
   * <p>
   * <pre>
   * emailService.send("hello", emptyList())
   *             .withTemplateParam("name", "Joe Blow")
   *             .to("joe@blow.com")
   *             .from("info@example.com")
   *             .now();
   * </pre>
   *
   * @param templateId         (Required) The id of the template. The implementation will dictate the type of template
   *                           and how they are stored.
   * @param preferredLanguages The preferred languages to send them email in.
   * @return The EmailBuilder that is used to build up the email configuration and parameters.
   */
  SendEmailBuilder send(Object templateId, List<Locale> preferredLanguages);

  /**
   * Validates multiple FreeMarker templates. This will throw an EmailTemplateException that contains all of the errors
   * encountered in each of the templates while parsing and rendering them.
   * <p>
   * Here's an example using this method:
   * <p>
   * <pre>
   * try {
   *   Map&lt;String, String> templates = new HashMap&lt;>();
   *   templates.put("subject", "Some template ${foo}");
   *   emailService.validate(templates, parameters);
   * } catch (EmailTemplateException e) {
   *   if (e.getParseErrors().containsKey("subject")) {
   *     ...
   *   }
   *   if (e.getRenderErrors().containsKey("subject")) {
   *     ...
   *   }
   * }
   * </pre>
   *
   * @param rawEmailTemplates (Required) The templates to validate.
   * @param parameters        (Required) The parameters used to render the template.
   * @return The ValidateResult which contains any errors that were found.
   */
  ValidateResult validate(RawEmailTemplates rawEmailTemplates, Map<String, Object> parameters);
}