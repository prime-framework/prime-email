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
import org.primeframework.email.domain.Email;
import org.primeframework.email.domain.EmailAddress;
import static java.util.Collections.emptyMap;

/**
 * Loads email templates from the file system using a injected FreeMarker Configuration object.
 *
 * @author Brian Pontarelli
 */
public class FileSystemEmailTemplateLoader implements EmailTemplateLoader {
  private final Configuration freeMarkerConfiguration;

  private final String templatesLocation;

  @Inject
  public FileSystemEmailTemplateLoader(EmailConfiguration emailConfiguration,
                                       @org.primeframework.email.guice.Email Configuration freeMarkerConfiguration) {
    this.freeMarkerConfiguration = freeMarkerConfiguration;
    this.templatesLocation = emailConfiguration.templateLocation();
  }

  @Override
  public void load(Object templateId, Email email, List<Locale> preferredLanguages) {
    if (email.from == null) {
      email.from = new EmailAddress();
    }

    Map<String, ParseException> errors = new HashMap<>();
    email.from.displayTemplate = loadTemplate(templateId + "-from.ftl", preferredLanguages, "from", errors);
    email.htmlTemplate = loadTemplate(templateId + "-html.ftl", preferredLanguages, "html", errors);
    email.subjectTemplate = loadTemplate(templateId + "-subject.ftl", preferredLanguages, "subject", errors);
    email.textTemplate = loadTemplate(templateId + "-text.ftl", preferredLanguages, "text", errors);
    if (errors.size() > 0) {
      throw new EmailTemplateException(errors, emptyMap());
    }
  }

  private Template loadTemplate(String templateName, List<Locale> preferredLanguages, String part,
                                Map<String, ParseException> errors) {
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
