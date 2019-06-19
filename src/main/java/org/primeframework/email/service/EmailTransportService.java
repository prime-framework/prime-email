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

import org.primeframework.email.domain.Email;
import org.primeframework.email.domain.SendResult;

/**
 * This interface defines the transport mechanism for sending email messages.
 *
 * @author Brian Pontarelli
 */
public interface EmailTransportService {
  /**
   * Sends an email using some SMTP transport mechanism. This sends the email immediately.
   *
   * @param contextId  The context id that helps determine how the email is processed.
   * @param email      The email to send.
   * @param sendResult The send result where errors and emails are stored.
   */
  void sendEmail(Object contextId, Email email, SendResult sendResult);

  /**
   * Sends an email using some SMTP transport mechanism. This will always send the message asynchronously and return
   * control immediately to the caller.
   *
   * @param contextId  The context id that helps determine how the email is processed.
   * @param email      The email to send.
   * @param sendResult The send result where errors and emails are stored.
   */
  void sendEmailLater(Object contextId, Email email, SendResult sendResult);
}