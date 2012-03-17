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
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.primeframework.email.domain.Attachment;
import org.primeframework.email.domain.Email;
import org.primeframework.email.domain.EmailAddress;

/**
 * Interface for the EmailCommand object.  This is a service level object only and is only used within the EmailService
 * interface in order to collect the necessary information about an email and then send it. Although it is
 * implementation independent, in most cases this instance will be pre-populated with configured email information and
 * then allow the caller to override whatever data they need.
 */
public interface EmailCommand {
  /**
   * Adds a single template param for token replacement within the template.
   *
   * @param name  the param name
   * @param value the param value
   * @return This instance.
   */
  EmailCommand withTemplateParam(String name, Object value);

  /**
   * Adds template params for token replacement within the template.
   *
   * @param params The params to add for token replacement in te template.
   * @return This instance.
   */
  EmailCommand withTemplateParams(Map<String, Object> params);

  /**
   * Returns a map of all the template params.
   *
   * @return the template param map
   */
  Map<String, Object> getTemplateParams();

  /**
   * Sets the email subject
   *
   * @param subject the email subject
   * @return This instance.
   */
  EmailCommand withSubject(String subject);

  /**
   * @return The previous set subject or null.
   */
  String getSubject();

  /**
   * Method to set a list of email to addresses.  This method assumes that the to display is equal to the to address.
   *
   * @param to The list of to email to send the email to (required).
   * @return This instance.
   */
  EmailCommand to(EmailAddress... to);

  /**
   * Method to set a list of email to addresses.  This method assumes that the to display is equal to the to address.
   *
   * @param to The list of to email to send the email to (required).
   * @return This instance.
   */
  EmailCommand to(String... to);

  /**
   * @return The previously set to addresses or null.
   */
  List<EmailAddress> getTo();

  /**
   * Sets the from email address
   *
   * @param from The from email.
   * @return This instance.
   */
  EmailCommand from(EmailAddress from);

  /**
   * Sets the from email address
   *
   * @param from The from email.
   * @return This instance.
   */
  EmailCommand from(String from);

  /**
   * Sets the from email address
   *
   * @param from    The from email.
   * @param display The display part of the email address.
   * @return This instance.
   */
  EmailCommand from(String from, String display);

  /**
   * @return The previously set from address or null.
   */
  EmailAddress getFrom();

  /**
   * Sets the reply to email address
   *
   * @param replyTo The reply to email address.
   * @return This instance.
   */
  EmailCommand replyTo(EmailAddress replyTo);

  /**
   * Sets the reply to email address
   *
   * @param replyTo The reply to email address.
   * @return This instance.
   */
  EmailCommand replyTo(String replyTo);

  /**
   * Sets the reply to email address
   *
   * @param replyTo The reply to email address.
   * @param display The display part of the email address.
   * @return This instance.
   */
  EmailCommand replyTo(String replyTo, String display);

  /**
   * @return The previously set reply to address or null.
   */
  EmailAddress getReplyTo();

  /**
   * A vararg method to add blind carbon copies.
   *
   * @param bcc The blind carbon copy email addresses.
   * @return This instance.
   */
  EmailCommand bcc(EmailAddress... bcc);

  /**
   * A vararg method to add blind carbon copies.
   *
   * @param bcc The blind carbon copy email addresses.
   * @return This instance.
   */
  EmailCommand bcc(String... bcc);

  /**
   * @return The previously set bcc address or null.
   */
  List<EmailAddress> getBcc();

  /**
   * A vararg method to add email carbon copies.
   *
   * @param cc The carbon copy email addresses.
   * @return This instance.
   */
  EmailCommand cc(EmailAddress... cc);

  /**
   * A vararg method to add email carbon copies.
   *
   * @param cc The carbon copy email addresses.
   * @return This instance.
   */
  EmailCommand cc(String... cc);

  /**
   * @return The previously set cc addresses or null.
   */
  List<EmailAddress> getCc();

  /**
   * Vararg method to add attachments to the email
   *
   * @param attachments The list of email attachments.
   * @return This instance.
   */
  EmailCommand withAttachments(Attachment... attachments);

  /**
   * @return The previously set list of attachments.
   */
  List<Attachment> getAttachments();

  /**
   * Sends the email right now and waits until it is sent.
   *
   * @return The email if it was successfully sent, false if it wasn't sent.
   * @throws ExecutionException   If the execution of the email send failed.
   * @throws InterruptedException If the thread used to send the email was interrupted.
   */
  Email now() throws ExecutionException, InterruptedException;

  /**
   * Sends the email within the next given period of time.
   *
   * @param amount The amount of time.
   * @return A time command that allows the time parameters to be set.
   */
  TimeCommand withinTheNext(long amount);

  /**
   * Sends the email later without waiting for a result or allowing the caller to handle the result.
   */
  void later();

  /**
   * <p> Sends the email sometime in the future. </p>
   * <p/>
   * <p> <strong>NOTE</strong> You must handle the future in order to ensure correct email handling. In some cases if
   * you don't handle the future, the email will not be sent. If you don't want to handle the future and want to send
   * the email later, always use the {@link #later()} method. </p>
   *
   * @return A Future that represents the sending operation which might have already happened or will happen in the
   *         future, depending on the speed of things.
   */
  Future<Email> inTheFuture();
}