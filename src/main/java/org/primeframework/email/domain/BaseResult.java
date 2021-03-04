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
package org.primeframework.email.domain;

import java.util.HashMap;
import java.util.Map;

import freemarker.core.ParseException;
import freemarker.template.TemplateException;

/**
 * Stores the common result data for preview, send and validate requests.
 *
 * @author Brian Pontarelli
 */
public abstract class BaseResult {
  public final Map<String, ParseException> parseErrors = new HashMap<>();

  public final Map<String, TemplateException> renderErrors = new HashMap<>();

  // Optional object that is a reference to the actual template loaded by the Template Loader
  public Object template;

  // Optional object that is the unique Id of the template
  public Object templateId;

  public BaseResult() {
  }

  public BaseResult(Map<String, ParseException> parseErrors, Map<String, TemplateException> renderErrors) {
    this.parseErrors.putAll(parseErrors);
    this.renderErrors.putAll(renderErrors);
  }

  public boolean wasSuccessful() {
    return parseErrors.isEmpty() && renderErrors.isEmpty();
  }
}
