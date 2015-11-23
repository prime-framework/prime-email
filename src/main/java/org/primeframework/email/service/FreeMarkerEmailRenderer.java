/*
 * Copyright (c) 2015, Inversoft Inc., All Rights Reserved
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

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.primeframework.email.EmailException;
import org.primeframework.email.EmailTemplateException;
import org.primeframework.email.domain.Email;
import org.primeframework.email.domain.EmailAddress;
import org.primeframework.email.domain.ParsedEmailAddress;
import org.primeframework.email.domain.ParsedEmailTemplates;
import static java.util.Collections.emptyMap;

/**
 * An implementation of the email template loader that loads FreeMarker templates from the file system. The location of
 * the templates is configured via the EmailConfiguration object that is passed into the constructor.
 *
 * @author Brian Pontarelli
 */
public class FreeMarkerEmailRenderer implements EmailRenderer {
  @Override
  public void render(ParsedEmailTemplates parsedEmailTemplates, Email email, Map<String, Object> parameters)
      throws EmailTemplateException {
    Map<String, TemplateException> renderErrors = new HashMap<>();

    if (email.from == null && parsedEmailTemplates.from != null) {
      email.from = renderEmailAddress(parsedEmailTemplates.from, parameters, "from", renderErrors);
    }

    parsedEmailTemplates.bcc.forEach((bcc) -> email.bcc.add(renderEmailAddress(bcc, parameters, "bcc", renderErrors)));
    parsedEmailTemplates.cc.forEach((cc) -> email.bcc.add(renderEmailAddress(cc, parameters, "cc", renderErrors)));
    parsedEmailTemplates.to.forEach((to) -> email.bcc.add(renderEmailAddress(to, parameters, "to", renderErrors)));

    if (email.html == null && parsedEmailTemplates.html != null) {
      email.html = callTemplate(parsedEmailTemplates.html, parameters, "html", renderErrors);
    }

    if (email.replyTo == null) {
      email.replyTo = renderEmailAddress(parsedEmailTemplates.replyTo, parameters, "replyTo", renderErrors);
    }

    if (email.subject == null && parsedEmailTemplates.subject != null) {
      email.subject = callTemplate(parsedEmailTemplates.subject, parameters, "subject", renderErrors);
    }

    if (email.text == null && parsedEmailTemplates.text != null) {
      email.text = callTemplate(parsedEmailTemplates.text, parameters, "text", renderErrors);
    }

    if (renderErrors.size() > 0) {
      throw new EmailTemplateException(email, emptyMap(), renderErrors);
    }
  }

  /**
   * Processes the FreeMarker template.
   *
   * @param template   The FreeMaker template.
   * @param parameters The parameters that are passed to the template.
   * @param errors     The errors map.
   * @return The String result of the processing the template.
   * @throws EmailException If the template couldn't be processed.
   */
  protected String callTemplate(Template template, Map<String, Object> parameters, String part,
                                Map<String, TemplateException> errors) {
    if (template == null) {
      return null;
    }

    StringWriter writer = new StringWriter();
    try {
      template.process(parameters, writer);
    } catch (TemplateException e) {
      errors.put(part, e);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }

    return writer.toString();
  }

  private EmailAddress renderEmailAddress(ParsedEmailAddress parsedEmailAddress, Map<String, Object> parameters,
                                          String part, Map<String, TemplateException> errors) {
    if (parsedEmailAddress != null) {
      return new EmailAddress(parsedEmailAddress.address, callTemplate(parsedEmailAddress.display, parameters, part, errors));
    }

    return null;
  }
}
