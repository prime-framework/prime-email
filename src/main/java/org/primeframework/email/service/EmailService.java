/*
 * Copyright (c) 2001-2007, JCatapult.org, All Rights Reserved
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

import org.primeframework.email.EmailException;

/**
 * This interface defines how to send emails in a simple and templatized manner.
 *
 * @author Brian Pontarelli and James Humphrey
 */
public interface EmailService {
  /**
   * Called to build the email using the specified template as the email body, configure the email using the returned
   * {@link EmailBuilder} object but not send it.
   * <p>
   * Here's an example using the template below named 'hello':
   * <p>
   * <pre>
   * Hello ${name},
   *
   * Thanks for buying all that cool stuff.
   * </pre>
   * <p>
   * You would call this method like this:
   * <p>
   * <pre>
   * Email email = render("hello").withTemplateParam("name", "Joe Blow").to("joe@blow.com").from("info@example.com").now();
   * System.out.println(email.html);
   * </pre>
   *
   * @param templateId         (Required) The id of the template. The implementation will dictate the type of template
   *                           and how they are stored.
   * @param preferredLanguages The preferred languages to send them email in.
   * @return The email building that is used to build up the email configuration and parameters.
   * @throws EmailException Runtime exception thrown if the from and/or subject are null and also if the email message
   *                        contains zero recipients
   */
  EmailBuilder render(Object templateId, List<Locale> preferredLanguages) throws EmailException;

/**
   * Called to build the email using the specified template as the email body, configure the email using the returned
   * {@link EmailBuilder} object and then send the email.
   * <p>
   * Here's an example using the template below named 'hello':
   * <p>
   * <pre>
   * Hello ${name},
   *
   * Thanks for buying all that cool stuff.
   * </pre>
   * <p>
   * You would call this method like this:
   * <p>
   * <pre>
   * send("hello").withTemplateParam("name", "Joe Blow").to("joe@blow.com").from("info@example.com").now();
   * </pre>
   *
   * @param templateId         (Required) The id of the template. The implementation will dictate the type of template
   *                           and how they are stored.
   * @param preferredLanguages The preferred languages to send them email in.
   * @return The email building that is used to build up the email configuration and parameters.
   * @throws EmailException Runtime exception thrown if the from and/or subject are null and also if the email message
   *                        contains zero recipients
   */
  EmailBuilder send(Object templateId, List<Locale> preferredLanguages) throws EmailException;
}