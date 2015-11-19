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
import static java.util.Collections.emptyMap;

/**
 * An implementation of the email template loader that loads FreeMarker templates from the file system. The location of
 * the templates is configured via the EmailConfiguration object that is passed into the constructor.
 *
 * @author Brian Pontarelli
 */
public class FreeMarkerEmailRenderer implements EmailRenderer {
  @Override
  public void render(Email email, Map<String, Object> params) {
    Map<String, TemplateException> renderErrors = new HashMap<>();
    renderEmailAddress(email.from, params, "from", renderErrors);

    email.bcc.forEach((bcc) -> renderEmailAddress(bcc, params, "bcc", renderErrors));
    email.cc.forEach((cc) -> renderEmailAddress(cc, params, "cc", renderErrors));
    email.to.forEach((to) -> renderEmailAddress(to, params, "to", renderErrors));

    if (email.html == null && email.htmlTemplate != null) {
      email.html = callTemplate(email.htmlTemplate, params, "html", renderErrors);
    }

    renderEmailAddress(email.replyTo, params, "replyTo", renderErrors);

    if (email.subject == null && email.subjectTemplate != null) {
      email.subject = callTemplate(email.subjectTemplate, params, "subject", renderErrors);
    }

    if (email.text == null && email.textTemplate != null) {
      email.text = callTemplate(email.textTemplate, params, "text", renderErrors);
    }

    if (renderErrors.size() > 0) {
      throw new EmailTemplateException(emptyMap(), renderErrors);
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

  private void renderEmailAddress(EmailAddress emailAddress, Map<String, Object> params, String part,
                                  Map<String, TemplateException> renderErrors) {
    if (emailAddress != null && emailAddress.display == null && emailAddress.displayTemplate != null) {
      emailAddress.display = callTemplate(emailAddress.displayTemplate, params, part, renderErrors);
    }
  }
}
