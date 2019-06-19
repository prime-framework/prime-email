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
import org.primeframework.email.domain.SendResult;

/**
 * Builds the email information using a builder pattern. Anything specified via the builder overrides data in the
 * EmailTemplate.
 */
public class SendEmailBuilder extends BaseEmailBuilder<SendEmailBuilder, SendResult> {
  protected final Function<SendEmailBuilder, SendResult> laterFunction;

  /**
   * Constructs a new instance.
   *
   * @param contextId   The context id that helps determine how the email is processed.
   * @param templateId  The id of the template.
   * @param email       The email from the configuration.
   * @param nowFunction The function to call when emails are sent now.
   */
  SendEmailBuilder(Object contextId, Object templateId, Email email,
                   Function<SendEmailBuilder, SendResult> laterFunction,
                   Function<SendEmailBuilder, SendResult> nowFunction) {
    super(contextId, templateId, email, nowFunction);
    this.laterFunction = laterFunction;
  }

  /**
   * Sends the email that has been built using this builder at some point in the future.
   *
   * @return The send result, which might include parse, render or send errors, or a Future to track the sending of the
   * email.
   */
  public SendResult later() {
    return laterFunction.apply(this);
  }

  /**
   * Sends the email that has been built using this builder right now in a blocking fashion.
   *
   * @return The send result, which might include parse, render or send errors, or the email hat was sent.
   */
  public SendResult now() {
    return nowFunction.apply(this);
  }
}
