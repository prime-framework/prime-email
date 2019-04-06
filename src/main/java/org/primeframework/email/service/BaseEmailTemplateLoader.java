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

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.primeframework.email.domain.BaseResult;
import org.primeframework.email.domain.ParsedEmailAddress;
import org.primeframework.email.domain.ParsedEmailTemplates;
import org.primeframework.email.domain.RawEmailTemplates;

import java.io.IOException;

/**
 * @author Brian Pontarelli
 */
public abstract class BaseEmailTemplateLoader implements EmailTemplateLoader {
  protected final Configuration freeMarkerConfiguration;

  protected BaseEmailTemplateLoader(Configuration freeMarkerConfiguration) {
    this.freeMarkerConfiguration = freeMarkerConfiguration;
  }

  @Override
  public ParsedEmailTemplates parse(RawEmailTemplates rawEmailTemplates, BaseResult baseResult) {
    ParsedEmailTemplates parsedEmailTemplates = new ParsedEmailTemplates();

    if (rawEmailTemplates.fromDisplay != null) {
      parsedEmailTemplates.from = new ParsedEmailAddress();
      parsedEmailTemplates.from.display = parseTemplate(rawEmailTemplates.fromDisplay, "from", baseResult);
    }
    if (rawEmailTemplates.replyToDisplay != null) {
      parsedEmailTemplates.replyTo = new ParsedEmailAddress();
      parsedEmailTemplates.replyTo.display = parseTemplate(rawEmailTemplates.replyToDisplay, "replyTo", baseResult);
    }

    rawEmailTemplates.bccDisplays.forEach((bccDisplay) -> parsedEmailTemplates.bcc.add(new ParsedEmailAddress(null, parseTemplate(bccDisplay, "bcc", baseResult))));
    rawEmailTemplates.ccDisplays.forEach((ccDisplay) -> parsedEmailTemplates.cc.add(new ParsedEmailAddress(null, parseTemplate(ccDisplay, "cc", baseResult))));
    rawEmailTemplates.toDisplays.forEach((toDisplay) -> parsedEmailTemplates.to.add(new ParsedEmailAddress(null, parseTemplate(toDisplay, "to", baseResult))));
    parsedEmailTemplates.html = parseTemplate(rawEmailTemplates.html, "html", baseResult);
    parsedEmailTemplates.subject = parseTemplate(rawEmailTemplates.subject, "subject", baseResult);
    parsedEmailTemplates.text = parseTemplate(rawEmailTemplates.text, "text", baseResult);

    return parsedEmailTemplates;
  }

  /**
   * Parses the FreeMarker template using the FreeMarker Configuration object.
   *
   * @param template   The template to parse.
   * @param part       The part of the email the template is for (i.e. subject, from, etc).
   * @param baseResult The base result that errors are added to.
   * @return The FreeMarker Template object.
   */
  protected Template parseTemplate(String template, String part, BaseResult baseResult) {
    if (template == null) {
      return null;
    }

    try {
      return new Template(null, template, freeMarkerConfiguration);
    } catch (ParseException e) {
      baseResult.parseErrors.put(part, e);
    } catch (IOException e) {
      // Skip it and continue
    }

    return null;
  }
}
