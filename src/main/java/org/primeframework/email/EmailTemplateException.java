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
package org.primeframework.email;

import java.util.HashMap;
import java.util.Map;

import freemarker.core.ParseException;
import freemarker.template.TemplateException;

/**
 * A sub-class of EmailException that is thrown when email templates have failures.
 *
 * @author Brian Pontarelli
 */
public class EmailTemplateException extends EmailException {
  private final Map<String, ParseException> parseErrors = new HashMap<>();

  private final Map<String, TemplateException> renderErrors = new HashMap<>();

  public EmailTemplateException(Map<String, ParseException> parseErrors, Map<String, TemplateException> renderErrors) {
    this.parseErrors.putAll(parseErrors);
    this.renderErrors.putAll(renderErrors);
  }

  public Map<String, ParseException> getParseErrors() {
    return parseErrors;
  }

  public Map<String, TemplateException> getRenderErrors() {
    return renderErrors;
  }
}