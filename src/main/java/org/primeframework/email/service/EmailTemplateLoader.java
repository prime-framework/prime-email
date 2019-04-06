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

import org.primeframework.email.domain.BaseResult;
import org.primeframework.email.domain.Email;
import org.primeframework.email.domain.ParsedEmailTemplates;
import org.primeframework.email.domain.RawEmailTemplates;

import java.util.List;
import java.util.Locale;

/**
 * Loads templates and localizes the templates into the template fields inside an {@link Email} object.
 *
 * @author Brian Pontarelli
 */
public interface EmailTemplateLoader {
  /**
   * Loads an existing set of templates for an email.
   *
   * @param templateId         The id of the template to load.
   * @param preferredLanguages The preferred langauges that can be used to localize the templates.
   * @param baseResult         The base result that errors are added to.
   * @return The parsed email templates.
   */
  ParsedEmailTemplates load(Object templateId, List<Locale> preferredLanguages, BaseResult baseResult);

  /**
   * Parses the templates using FreeMarker.
   *
   * @param rawEmailTemplates The raw email templates.
   * @param baseResult        The base result that errors are added to.
   * @return The parsed templates.
   */
  ParsedEmailTemplates parse(RawEmailTemplates rawEmailTemplates, BaseResult baseResult);
}
