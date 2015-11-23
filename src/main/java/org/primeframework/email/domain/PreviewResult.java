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

import java.util.Map;

import freemarker.core.ParseException;
import freemarker.template.TemplateException;

/**
 * Stores the result data when emails are previewed.
 *
 * @author Brian Pontarelli
 */
public class PreviewResult extends BaseResult {
  public Email email;

  public PreviewResult() {
  }

  public PreviewResult(Email email) {
    this.email = email;
  }

  public PreviewResult(Map<String, ParseException> parseErrors, Map<String, TemplateException> renderErrors) {
    super(parseErrors, renderErrors);
  }
}
