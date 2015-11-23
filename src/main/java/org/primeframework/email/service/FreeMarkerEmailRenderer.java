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
import java.util.Map;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.primeframework.email.domain.BaseResult;
import org.primeframework.email.domain.Email;
import org.primeframework.email.domain.EmailAddress;
import org.primeframework.email.domain.ParsedEmailAddress;
import org.primeframework.email.domain.ParsedEmailTemplates;

/**
 * An implementation of the email template loader that loads FreeMarker templates from the file system. The location of
 * the templates is configured via the EmailConfiguration object that is passed into the constructor.
 *
 * @author Brian Pontarelli
 */
public class FreeMarkerEmailRenderer implements EmailRenderer {
  @Override
  public void render(ParsedEmailTemplates parsedEmailTemplates, Email email, Map<String, Object> parameters,
                     BaseResult baseResult) {
    if (email.from == null && parsedEmailTemplates.from != null) {
      email.from = renderEmailAddress(parsedEmailTemplates.from, parameters, "from", baseResult);
    }

    parsedEmailTemplates.bcc.forEach((bcc) -> email.bcc.add(renderEmailAddress(bcc, parameters, "bcc", baseResult)));
    parsedEmailTemplates.cc.forEach((cc) -> email.bcc.add(renderEmailAddress(cc, parameters, "cc", baseResult)));
    parsedEmailTemplates.to.forEach((to) -> email.bcc.add(renderEmailAddress(to, parameters, "to", baseResult)));

    if (email.html == null && parsedEmailTemplates.html != null) {
      email.html = callTemplate(parsedEmailTemplates.html, parameters, "html", baseResult);
    }

    if (email.replyTo == null) {
      email.replyTo = renderEmailAddress(parsedEmailTemplates.replyTo, parameters, "replyTo", baseResult);
    }

    if (email.subject == null && parsedEmailTemplates.subject != null) {
      email.subject = callTemplate(parsedEmailTemplates.subject, parameters, "subject", baseResult);
    }

    if (email.text == null && parsedEmailTemplates.text != null) {
      email.text = callTemplate(parsedEmailTemplates.text, parameters, "text", baseResult);
    }
  }

  /**
   * Processes the FreeMarker template.
   *
   * @param template   The FreeMaker template.
   * @param parameters The parameters that are passed to the template.
   * @param baseResult The base result where errors are added.
   * @return The String result of the processing the template.
   */
  protected String callTemplate(Template template, Map<String, Object> parameters, String part,
                                BaseResult baseResult) {
    if (template == null) {
      return null;
    }

    StringWriter writer = new StringWriter();
    try {
      template.process(parameters, writer);
    } catch (TemplateException e) {
      baseResult.renderErrors.put(part, e);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }

    return writer.toString();
  }

  private EmailAddress renderEmailAddress(ParsedEmailAddress parsedEmailAddress, Map<String, Object> parameters,
                                          String part, BaseResult baseResult) {
    if (parsedEmailAddress != null) {
      return new EmailAddress(parsedEmailAddress.address, callTemplate(parsedEmailAddress.display, parameters, part, baseResult));
    }

    return null;
  }
}
