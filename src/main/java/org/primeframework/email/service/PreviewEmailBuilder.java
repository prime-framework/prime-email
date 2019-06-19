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

import java.util.function.Function;

import org.primeframework.email.domain.Email;
import org.primeframework.email.domain.PreviewResult;

/**
 * Builds the email information using a builder pattern. Anything specified via the builder overrides data in the
 * EmailTemplate.
 */
public class PreviewEmailBuilder extends BaseEmailBuilder<PreviewEmailBuilder, PreviewResult> {
  /**
   * Constructs a new instance.
   *
   * @param contextId       The context id that helps determine how the email is processed.
   * @param templateId      The id of the template.
   * @param email           The email from the configuration.
   * @param previewFunction The function to call when emails are previewed.
   */
  PreviewEmailBuilder(Object contextId, Object templateId, Email email,
                      Function<PreviewEmailBuilder, PreviewResult> previewFunction) {
    super(contextId, templateId, email, previewFunction);
  }

  public PreviewResult go() {
    return nowFunction.apply(this);
  }
}
