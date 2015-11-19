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

import java.util.List;
import java.util.Locale;

import freemarker.template.Template;
import org.primeframework.email.domain.Email;

/**
 * Loads templates and localizes the templates into the template fields inside an {@link Email} object.
 *
 * @author Brian Pontarelli
 */
public interface EmailTemplateLoader {
  /**
   * Loads templates and localizes the templates into the template fields inside an {@link Email} object.
   *
   * @param templateId         The id template of the templates to load.
   * @param email              The email to load the templates into.
   * @param preferredLanguages The preferred languages that are used to localize the templates.
   */
  void load(Object templateId, Email email, List<Locale> preferredLanguages);

  /**
   * Loads templates and localizes the templates into the template fields inside an {@link Email} object.
   *
   * @param templateId         The id template of the templates to load.
   * @param email              The email to load the templates into.
   * @param preferredLanguages The preferred languages that are used to localize the templates.
   */
  Template parse(String template);
}
