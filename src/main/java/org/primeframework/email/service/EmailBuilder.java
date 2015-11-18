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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

import org.primeframework.email.domain.Attachment;
import org.primeframework.email.domain.Email;
import org.primeframework.email.domain.EmailAddress;
import static java.util.Arrays.asList;

/**
 * Builds the email information using a builder pattern. Anything specified via the builder overrides data in the
 * EmailTemplate.
 */
public class EmailBuilder {
  private final Email email;

  private final Function<EmailBuilder, Future<Email>> futureFunction;

  private final Consumer<EmailBuilder> laterConsumer;

  private final Map<String, Object> params = new HashMap<>();

  private final Object templateId;

  /**
   * Constructs a new instance.
   *
   * @param templateId The id of the template.
   * @param email      The email from the configuration.
   */
  EmailBuilder(Object templateId, Email email, Function<EmailBuilder, Future<Email>> futureFunction,
               Consumer<EmailBuilder> laterConsumer) {
    this.templateId = templateId;
    this.email = email;
    this.futureFunction = futureFunction;
    this.laterConsumer = laterConsumer;
  }

  /**
   * {@inheritDoc}
   */
  public EmailBuilder bcc(EmailAddress... bccEmails) {
    email.bcc = asList(bccEmails);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  public EmailBuilder bcc(String... bcc) {
    for (String s : bcc) {
      email.bcc.add(new EmailAddress(s));
    }
    return this;
  }

  /**
   * {@inheritDoc}
   */
  public EmailBuilder cc(EmailAddress... ccEmails) {
    email.cc = asList(ccEmails);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  public EmailBuilder cc(String... cc) {
    for (String s : cc) {
      email.cc.add(new EmailAddress(s));
    }
    return this;
  }

  /**
   * {@inheritDoc}
   */
  public EmailBuilder from(EmailAddress fromEmail) {
    email.from = fromEmail;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  public EmailBuilder from(String from) {
    email.from = new EmailAddress(from);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  public EmailBuilder from(String from, String display) {
    email.from = new EmailAddress(from, display);
    return this;
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
  public Map<String, Object> getParams() {
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
  public Future<Email> inTheFuture() {
    return futureFunction.apply(this);
  }

  /**
   * {@inheritDoc}
   */
  public void later() {
    laterConsumer.accept(this);
  }

  /**
   * {@inheritDoc}
   */
  public Email now() throws ExecutionException, InterruptedException {
    Future<Email> future = futureFunction.apply(this);
    return future.get();
  }

  /**
   * {@inheritDoc}
   */
  public EmailBuilder replyTo(EmailAddress replyTo) {
    email.replyTo = replyTo;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  public EmailBuilder replyTo(String replyTo) {
    email.replyTo = new EmailAddress(replyTo);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  public EmailBuilder replyTo(String replyTo, String display) {
    email.replyTo = new EmailAddress(replyTo, display);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  public EmailBuilder to(EmailAddress... to) {
    email.to = asList(to);
    return this;
  }

  public EmailBuilder to(String... to) {
    for (String s : to) {
      email.to.add(new EmailAddress(s));
    }
    return this;
  }

  /**
   * {@inheritDoc}
   */
  public EmailBuilder withAttachments(Attachment... attachments) {
    email.attachments = asList(attachments);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  public EmailBuilder withSubject(String subject) {
    email.subject = subject;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  public EmailBuilder withTemplateParameter(String name, Object value) {
    params.put(name, value);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  public EmailBuilder withTemplateParameters(Map<String, Object> params) {
    this.params.putAll(params);
    return this;
  }
}
