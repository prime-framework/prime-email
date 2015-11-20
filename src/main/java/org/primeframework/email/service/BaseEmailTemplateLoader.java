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
import java.util.HashMap;
import java.util.Map;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.primeframework.email.EmailTemplateException;
import org.primeframework.email.domain.ParsedEmailAddress;
import org.primeframework.email.domain.ParsedEmailTemplates;
import org.primeframework.email.domain.RawEmailTemplates;
import static java.util.Collections.emptyMap;

/**
 * @author Brian Pontarelli
 */
public abstract class BaseEmailTemplateLoader implements EmailTemplateLoader {
  protected final Configuration freeMarkerConfiguration;

  protected BaseEmailTemplateLoader(Configuration freeMarkerConfiguration) {
    this.freeMarkerConfiguration = freeMarkerConfiguration;
  }

  @Override
  public ParsedEmailTemplates parse(RawEmailTemplates rawEmailTemplates) throws EmailTemplateException {
    ParsedEmailTemplates parsedEmailTemplates = new ParsedEmailTemplates();
    parsedEmailTemplates.from = new ParsedEmailAddress();
    parsedEmailTemplates.replyTo = new ParsedEmailAddress();

    Map<String, ParseException> errors = new HashMap<>();
    parsedEmailTemplates.from.display = parseTemplate(rawEmailTemplates.fromDisplay, "from", errors);
    parsedEmailTemplates.replyTo.display = parseTemplate(rawEmailTemplates.replyToDisplay, "replyTo", errors);
    rawEmailTemplates.bccDisplays.forEach((bccDisplay) -> parsedEmailTemplates.bcc.add(new ParsedEmailAddress(null, parseTemplate(bccDisplay, "bcc", errors))));
    rawEmailTemplates.ccDisplays.forEach((ccDisplay) -> parsedEmailTemplates.cc.add(new ParsedEmailAddress(null, parseTemplate(ccDisplay, "cc", errors))));
    rawEmailTemplates.toDisplays.forEach((toDisplay) -> parsedEmailTemplates.to.add(new ParsedEmailAddress(null, parseTemplate(toDisplay, "to", errors))));
    parsedEmailTemplates.html = parseTemplate(rawEmailTemplates.html, "html", errors);
    parsedEmailTemplates.subject = parseTemplate(rawEmailTemplates.subject, "subject", errors);
    parsedEmailTemplates.text = parseTemplate(rawEmailTemplates.text, "text", errors);
    if (errors.size() > 0) {
      throw new EmailTemplateException(errors, emptyMap());
    }

    return parsedEmailTemplates;
  }

  private Template parseTemplate(String template, String part, Map<String, ParseException> errors) {
    if (template == null) {
      return null;
    }

    try {
      return new Template(null, template, freeMarkerConfiguration);
    } catch (ParseException e) {
      errors.put(part, e);
    } catch (IOException e) {
      // Skip it and continue
    }

    return null;
  }
}
