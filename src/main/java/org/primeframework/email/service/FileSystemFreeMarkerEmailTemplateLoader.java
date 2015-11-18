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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.inject.Inject;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.primeframework.email.EmailException;
import org.primeframework.email.config.EmailConfiguration;
import org.primeframework.email.domain.EmailTemplate;

/**
 * An implementation of the email template loader that loads FreeMarker templates from the file system. The location of
 * the templates is configured via the EmailConfiguration object that is passed into the constructor.
 *
 * @author Brian Pontarelli
 */
public class FileSystemFreeMarkerEmailTemplateLoader implements EmailTemplateLoader {
  private final freemarker.template.Configuration freeMarkerConfiguration;

  private final String templatesLocation;

  @Inject
  public FileSystemFreeMarkerEmailTemplateLoader(Configuration freeMarkerConfiguration,
                                                 EmailConfiguration emailConfiguration) {
    this.freeMarkerConfiguration = freeMarkerConfiguration;
    this.templatesLocation = emailConfiguration.templateLocation();
  }

  @Override
  public EmailTemplate load(Object templateId, Map<String, Object> params, List<Locale> preferredLanguages) {
    Template textTemplate = loadTemplate(templateId.toString() + "-text.ftl", preferredLanguages);
    Template htmlTemplate = loadTemplate(templateId.toString() + "-html.ftl", preferredLanguages);
    if (textTemplate == null && htmlTemplate == null) {
      throw new EmailException("Unable to locate the FreeMarker template(s) for the id [" + templateId + "] to use for emailing");
    }

    // Get the text version if there is a template
    EmailTemplate emailTemplate = new EmailTemplate();
    if (textTemplate != null) {
      emailTemplate.text = callTemplate(textTemplate, params);
    }

    // Handle the HTML version if there is one
    if (htmlTemplate != null) {
      emailTemplate.html = callTemplate(htmlTemplate, params);
    }

    return emailTemplate;
  }

  /**
   * Processes the FreeMarker template.
   *
   * @param template   The FreeMaker template.
   * @param parameters The parameters that are passed to the template.
   * @return The String result of the processing the template.
   * @throws EmailException If the template couldn't be processed.
   */
  private String callTemplate(Template template, Map<String, Object> parameters) {
    StringWriter writer = new StringWriter();
    try {
      template.process(parameters, writer);
    } catch (Exception e) {
      throw new EmailException(e);
    }

    return writer.toString();
  }

  /**
   * Loads a FreeMarker template from the file system.
   *
   * @param templateName       The template name.
   * @param preferredLanguages The preferred languages.
   * @return The template or null if it doesn't exist.
   */
  private Template loadTemplate(String templateName, List<Locale> preferredLanguages) {
    for (Locale preferredLanguage : preferredLanguages) {
      try {
        return freeMarkerConfiguration.getTemplate(templatesLocation + "/" + templateName, preferredLanguage);
      } catch (IOException fnfe) {
        // Skip it and continue
      }
    }

    return null;
  }
}
