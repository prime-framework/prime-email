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
import java.util.Map;

import org.primeframework.email.domain.EmailTemplate;

/**
 * Loads email templates and renders them.
 *
 * @author Brian Pontarelli
 */
public interface EmailTemplateLoader {
  /**
   * Loads the email template using the given id and renders it using the parameters and preferred languages given.
   * Implementations of this interface can use whatever templating system they want.
   *
   * @param templateId The id of the template to load.
   * @param params The parameters that are passed to the template.
   * @param preferredLanguages The preferred languages used to localize the template.
   * @return The rendered email template.
   */
  EmailTemplate load(Object templateId, Map<String, Object> params, List<Locale> preferredLanguages);
}
