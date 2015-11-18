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

import java.util.Map;

import org.primeframework.email.domain.Email;

/**
 * Loads email templates and renders them.
 *
 * @author Brian Pontarelli
 */
public interface EmailRenderer {
  /**
   * Renders an EmailTemplate. This doesn't load the template, but just renders the one passed in.
   *
   * @param email  The email template to render.
   * @param params The parameters that are passed to the template.
   */
  void render(Email email, Map<String, Object> params);
}
