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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.primeframework.email.domain.Attachment;
import org.primeframework.email.domain.BaseResult;
import org.primeframework.email.domain.Email;
import org.primeframework.email.domain.EmailAddress;
import static java.util.Arrays.asList;

/**
 * Builds the email information using a builder pattern. Anything specified via the builder overrides data in the
 * EmailTemplate.
 */
@SuppressWarnings("unchecked")
public abstract class BaseEmailBuilder<T extends BaseEmailBuilder<T, U>, U extends BaseResult> {
  protected final Email email;

  protected final Function<T, U> nowFunction;

  protected final Map<String, Object> params = new HashMap<>();

  protected final Object templateId;

  /**
   * Constructs a new instance.
   *
   * @param templateId  The id of the template.
   * @param email       The email from the configuration.
   * @param nowFunction The function to call when emails are sent now.
   */
  BaseEmailBuilder(Object templateId, Email email, Function<T, U> nowFunction) {
    this.templateId = templateId;
    this.email = email;
    this.nowFunction = nowFunction;
  }

  /**
   * {@inheritDoc}
   */
  public T bcc(EmailAddress... bccEmails) {
    email.bcc = asList(bccEmails);
    return (T) this;
  }

  /**
   * {@inheritDoc}
   */
  public T bcc(String... bcc) {
    for (String s : bcc) {
      email.bcc.add(new EmailAddress(s));
    }
    return (T) this;
  }

  /**
   * {@inheritDoc}
   */
  public T cc(EmailAddress... ccEmails) {
    email.cc = asList(ccEmails);
    return (T) this;
  }

  /**
   * {@inheritDoc}
   */
  public T cc(String... cc) {
    for (String s : cc) {
      email.cc.add(new EmailAddress(s));
    }
    return (T) this;
  }

  /**
   * {@inheritDoc}
   */
  public T from(EmailAddress fromEmail) {
    email.from = fromEmail;
    return (T) this;
  }

  /**
   * {@inheritDoc}
   */
  public T from(String from) {
    email.from = new EmailAddress(from);
    return (T) this;
  }

  /**
   * {@inheritDoc}
   */
  public T from(String from, String display) {
    email.from = new EmailAddress(from, display);
    return (T) this;
  }

  /**
   * {@inheritDoc}
   */
  public List<Attachment> getAttachments() {
    return email.attachments;
  }

  /**
   * {@inheritDoc}
   */
  public List<EmailAddress> getBcc() {
    return email.bcc;
  }

  /**
   * {@inheritDoc}
   */
  public List<EmailAddress> getCc() {
    return email.cc;
  }

  /**
   * {@inheritDoc}
   */
  public Email getEmail() {
    return email;
  }

  /**
   * {@inheritDoc}
   */
  public EmailAddress getFrom() {
    return email.from;
  }

  /**
   * {@inheritDoc}
   */
  public Map<String, Object> getParameters() {
    return params;
  }

  /**
   * {@inheritDoc}
   */
  public EmailAddress getReplyTo() {
    return email.replyTo;
  }

  /**
   * {@inheritDoc}
   */
  public String getSubject() {
    return email.subject;
  }

  /**
   * {@inheritDoc}
   */
  public Object getTemplateId() {
    return templateId;
  }

  /**
   * {@inheritDoc}
   */
  public Map<String, Object> getTemplateParams() {
    return params;
  }

  /**
   * {@inheritDoc}
   */
  public List<EmailAddress> getTo() {
    return email.to;
  }

  /**
   * {@inheritDoc}
   */
  public T replyTo(EmailAddress replyTo) {
    email.replyTo = replyTo;
    return (T) this;
  }

  /**
   * {@inheritDoc}
   */
  public T replyTo(String replyTo) {
    email.replyTo = new EmailAddress(replyTo);
    return (T) this;
  }

  /**
   * {@inheritDoc}
   */
  public T replyTo(String replyTo, String display) {
    email.replyTo = new EmailAddress(replyTo, display);
    return (T) this;
  }

  /**
   * {@inheritDoc}
   */
  public T to(EmailAddress... to) {
    email.to = asList(to);
    return (T) this;
  }

  public T to(String... to) {
    for (String s : to) {
      email.to.add(new EmailAddress(s));
    }
    return (T) this;
  }

  /**
   * {@inheritDoc}
   */
  public T withAttachments(Attachment... attachments) {
    email.attachments = asList(attachments);
    return (T) this;
  }

  /**
   * {@inheritDoc}
   */
  public T withSubject(String subject) {
    email.subject = subject;
    return (T) this;
  }

  /**
   * {@inheritDoc}
   */
  public T withTemplateParameter(String name, Object value) {
    params.put(name, value);
    return (T) this;
  }

  /**
   * {@inheritDoc}
   */
  public T withTemplateParameters(Map<String, Object> params) {
    this.params.putAll(params);
    return (T) this;
  }
}
