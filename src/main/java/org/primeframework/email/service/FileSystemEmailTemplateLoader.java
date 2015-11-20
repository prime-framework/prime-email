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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.inject.Inject;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.primeframework.email.EmailTemplateException;
import org.primeframework.email.config.EmailConfiguration;
import org.primeframework.email.domain.ParsedEmailAddress;
import org.primeframework.email.domain.ParsedEmailTemplates;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;

/**
 * Loads email templates from the file system using a injected FreeMarker Configuration object.
 *
 * @author Brian Pontarelli
 */
public class FileSystemEmailTemplateLoader extends BaseEmailTemplateLoader {
  public static final List<Locale> EMPTY_LOCALES = singletonList(null);

  private final String templatesLocation;

  @Inject
  public FileSystemEmailTemplateLoader(EmailConfiguration emailConfiguration,
                                       @org.primeframework.email.guice.Email Configuration freeMarkerConfiguration) {
    super(freeMarkerConfiguration);
    this.templatesLocation = emailConfiguration.templateLocation();
  }

  @Override
  public ParsedEmailTemplates load(Object templateId, List<Locale> preferredLanguages) throws EmailTemplateException {
    ParsedEmailTemplates parsedEmailTemplates = new ParsedEmailTemplates();
    parsedEmailTemplates.from = new ParsedEmailAddress();

    Map<String, ParseException> errors = new HashMap<>();
    parsedEmailTemplates.from.display = loadTemplate(templateId + "-from.ftl", preferredLanguages, "from", errors);
    parsedEmailTemplates.html = loadTemplate(templateId + "-html.ftl", preferredLanguages, "html", errors);
    parsedEmailTemplates.subject = loadTemplate(templateId + "-subject.ftl", preferredLanguages, "subject", errors);
    parsedEmailTemplates.text = loadTemplate(templateId + "-text.ftl", preferredLanguages, "text", errors);
    if (errors.size() > 0) {
      throw new EmailTemplateException(errors, emptyMap());
    }

    return parsedEmailTemplates;
  }

  private Template loadTemplate(String templateName, List<Locale> preferredLanguages, String part,
                                Map<String, ParseException> errors) {
    if (preferredLanguages == null || preferredLanguages.isEmpty()) {
      preferredLanguages = EMPTY_LOCALES;
    }

    for (Locale preferredLanguage : preferredLanguages) {
      try {
        return freeMarkerConfiguration.getTemplate(templatesLocation + "/" + templateName, preferredLanguage);
      } catch (ParseException e) {
        errors.put(part, e);
        return null;
      } catch (IOException e) {
        // Skip it and continue
      }
    }

    return null;
  }
}
