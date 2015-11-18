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
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.inject.Inject;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.primeframework.email.EmailDataException;
import org.primeframework.email.EmailException;
import org.primeframework.email.EmailTemplateException;
import org.primeframework.email.config.EmailConfiguration;
import org.primeframework.email.domain.Email;

/**
 * @author Brian Pontarelli
 */
public class FreeMarkerEmailRenderer implements EmailRenderer {
  protected final EmailConfiguration emailConfiguration;

  protected final Configuration freeMarkerConfiguration;

  @Inject
  public FreeMarkerEmailRenderer(EmailConfiguration emailConfiguration, Configuration freeMarkerConfiguration) {
    this.emailConfiguration = emailConfiguration;
    this.freeMarkerConfiguration = freeMarkerConfiguration;
  }

  @Override
  public void render(Email email, Map<String, Object> params) {

  }

  @Override
  public EmailTemplate load(Object templateId, Map<String, Object> params, List<Locale> preferredLanguages) {
    Map<String, ParseException> parseErrors = new HashMap<>();
    FreeMarkerEmailTemplates templates = loadTemplates(templateId, preferredLanguages, parseErrors);
    if (templates.isEmpty() && parseErrors.isEmpty()) {
      throw new EmailDataException("Unable to locate the FreeMarker template(s) for the id [" + templateId + "] to use for emailing");
    }

    EmailTemplate emailTemplate = new EmailTemplate();
    Map<String, TemplateException> renderErrors = new HashMap<>();
    if (templates.fromNameTemplate != null) {
      emailTemplate.fromName = callTemplate(templates.fromNameTemplate, params, "fromName", renderErrors);
    }

    if (templates.htmlTemplate != null) {
      emailTemplate.html = callTemplate(templates.htmlTemplate, params, "html", renderErrors);
    }

    if (templates.subjectTemplate != null) {
      emailTemplate.subject = callTemplate(templates.subjectTemplate, params, "subject", renderErrors);
    }

    if (templates.textTemplate != null) {
      emailTemplate.text = callTemplate(templates.textTemplate, params, "text", renderErrors);
    }

    if (parseErrors.size() > 0 || renderErrors.size() > 0) {
      throw new EmailTemplateException(parseErrors, renderErrors);
    }

    return emailTemplate;
  }

  @Override
  public void render(EmailTemplate emailTemplate, Map<String, Object> params) {
    Map<String, ParseException> parseErrors = new HashMap<>();
    Map<String, TemplateException> renderErrors = new HashMap<>();

    if (emailTemplate.fromName != null) {
      Template fromNameTemplate = parseTemplate(emailTemplate.fromName, "fromName", parseErrors);
      emailTemplate.fromName = callTemplate(fromNameTemplate, params, "fromName", renderErrors);
    }

    if (emailTemplate.html != null) {
      Template htmlTemplate = parseTemplate(emailTemplate.html, "html", parseErrors);
      emailTemplate.html = callTemplate(htmlTemplate, params, "html", renderErrors);
    }

    if (emailTemplate.subject != null) {
      Template subjectTemplate = parseTemplate(emailTemplate.subject, "subject", parseErrors);
      emailTemplate.subject = callTemplate(subjectTemplate, params, "subject", renderErrors);
    }

    if (emailTemplate.text != null) {
      Template textTemplate = parseTemplate(emailTemplate.text, "text", parseErrors);
      emailTemplate.text = callTemplate(textTemplate, params, "text", renderErrors);
    }

    if (parseErrors.size() > 0 || renderErrors.size() > 0) {
      throw new EmailTemplateException(parseErrors, renderErrors);
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

  /**
   * Loads a FreeMarker template from the file system.
   *
   * @param templateId         The template id (for file loading this is the file name).
   * @param preferredLanguages The preferred languages.
   * @return The template or null if it doesn't exist.
   */
  protected abstract FreeMarkerEmailTemplates loadTemplates(Object templateId, List<Locale> preferredLanguages,
                                                            Map<String, ParseException> errors);

  /**
   * Parses a String into a FreeMarker Template object.
   *
   * @param template The template.
   * @param part     The part of the email that the template is form.
   * @param errors   The errors holder.
   * @return The template or null if the template is invalid.
   */
  protected Template parseTemplate(String template, String part, Map<String, ParseException> errors) {
    try {
      return new Template(null, new StringReader(template), freeMarkerConfiguration);
    } catch (ParseException e) {
      errors.put(part, e);
      return null;
    } catch (IOException e) {
      return null;
    }
  }

  public static class FreeMarkerEmailTemplates {
    public Template fromNameTemplate;

    public Template htmlTemplate;

    public Template subjectTemplate;

    public Template textTemplate;

    public FreeMarkerEmailTemplates() {
    }

    public FreeMarkerEmailTemplates(Template textTemplate, Template subjectTemplate,
                                    Template htmlTemplate, Template fromNameTemplate) {
      this.textTemplate = textTemplate;
      this.subjectTemplate = subjectTemplate;
      this.htmlTemplate = htmlTemplate;
      this.fromNameTemplate = fromNameTemplate;
    }

    public boolean isEmpty() {
      return fromNameTemplate == null && htmlTemplate == null && subjectTemplate == null && textTemplate == null;
    }
  }
}
