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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.inject.Inject;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.primeframework.email.config.EmailConfiguration;
import org.primeframework.email.guice.Email;

/**
 * An implementation of the email template loader that loads FreeMarker templates from the file system. The location of
 * the templates is configured via the EmailConfiguration object that is passed into the constructor.
 *
 * @author Brian Pontarelli
 */
public class FileSystemFreeMarkerEmailRenderer extends BaseFreeMarkerEmailRenderer implements EmailRenderer {
  private final String templatesLocation;

  @Inject
  public FileSystemFreeMarkerEmailRenderer(EmailConfiguration emailConfiguration,
                                           @Email Configuration freeMarkerConfiguration) {
    super(emailConfiguration, freeMarkerConfiguration);
    this.templatesLocation = emailConfiguration.templateLocation();
  }

  /**
   * Loads a FreeMarker template from the file system.
   *
   * @param templateId         The template id (for file loading this is the file name).
   * @param preferredLanguages The preferred languages.
   * @return The template or null if it doesn't exist.
   */
  @Override
  protected FreeMarkerEmailTemplates loadTemplates(Object templateId, List<Locale> preferredLanguages,
                                                   Map<String, ParseException> errors) {
    FreeMarkerEmailTemplates emailTemplates = new FreeMarkerEmailTemplates();
    emailTemplates.fromNameTemplate = loadTemplate(templateId + "-fromName.ftl", preferredLanguages, "fromName", errors);
    emailTemplates.htmlTemplate = loadTemplate(templateId + "-html.ftl", preferredLanguages, "html", errors);
    emailTemplates.subjectTemplate = loadTemplate(templateId + "-subject.ftl", preferredLanguages, "subject", errors);
    emailTemplates.textTemplate = loadTemplate(templateId + "-text.ftl", preferredLanguages, "text", errors);
    return emailTemplates;
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
