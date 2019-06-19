/*
 * Copyright (c) 2019, Inversoft Inc., All Rights Reserved
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

import javax.mail.Session;

/**
 * @author Brian Pontarelli
 */
public interface JavaMailSessionProvider {
  /**
   * Provides a JavaMail session.
   *
   * @param contextId The context id in case this provider needs it to determine the JavaMail session to return.
   * @return The JavaMail session and never null.
   */
  Session get(Object contextId);
}
