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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.primeframework.email.domain.Attachment;
import org.primeframework.email.domain.Email;
import org.primeframework.email.domain.EmailAddress;

import static java.util.Arrays.*;

/**
 * {@inheritDoc}
 */
public class FreeMarkerEmailCommand implements EmailCommand {
  private final Map<String, Object> params = new HashMap<String, Object>();
  private final String template;
  private final Email email;
  private final FreeMarkerEmailService freeMarkerEmailService;

  /**
   * Constructs a new instance that uses the given service as a callback and the email that was pulled from the
   * configuration.
   *
   * @param template               The name of the template.
   * @param freeMarkerEmailService To send the email.
   * @param email                  The email from the configuration.
   */
  FreeMarkerEmailCommand(String template, FreeMarkerEmailService freeMarkerEmailService, Email email) {
    this.template = template;
    this.freeMarkerEmailService = freeMarkerEmailService;
    this.email = email;
  }

  /**
   * {@inheritDoc}
   */
  public EmailCommand withTemplateParam(String name, Object value) {
    params.put(name, value);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  public EmailCommand withTemplateParams(Map<String, Object> params) {
    this.params.putAll(params);
    return this;
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
  public EmailCommand withSubject(String subject) {
    email.subject = subject;
    return this;
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
  public EmailCommand to(EmailAddress... to) {
    email.to = asList(to);
    return this;
  }

  public EmailCommand to(String... to) {
    for (String s : to) {
      email.to.add(new EmailAddress(s));
    }
    return this;
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
  public EmailCommand from(EmailAddress fromEmail) {
    email.from = fromEmail;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  public EmailCommand from(String from) {
    email.from = new EmailAddress(from);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  public EmailCommand from(String from, String display) {
    email.from = new EmailAddress(from, display);
    return this;
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
  public EmailCommand replyTo(EmailAddress replyTo) {
    email.replyTo = replyTo;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  public EmailCommand replyTo(String replyTo) {
    email.replyTo = new EmailAddress(replyTo);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  public EmailCommand replyTo(String replyTo, String display) {
    email.replyTo = new EmailAddress(replyTo, display);
    return this;
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
  public EmailCommand bcc(EmailAddress... bccEmails) {
    email.bcc = asList(bccEmails);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  public EmailCommand bcc(String... bcc) {
    for (String s : bcc) {
      email.bcc.add(new EmailAddress(s));
    }
    return this;
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
  public EmailCommand cc(EmailAddress... ccEmails) {
    email.cc = asList(ccEmails);
    return this;
  }

  /**
   * {@inheritDoc}
   */
  public EmailCommand cc(String... cc) {
    for (String s : cc) {
      email.cc.add(new EmailAddress(s));
    }
    return this;
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
  public EmailCommand withAttachments(Attachment... attachments) {
    email.attachments = asList(attachments);
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
  public void later() {
    freeMarkerEmailService.sendEmailLater(template, email, params);
  }

  /**
   * {@inheritDoc}
   */
  public Future<Email> inTheFuture() {
    return freeMarkerEmailService.sendEmail(template, email, params);
  }

  /**
   * {@inheritDoc}
   */
  public Email now() throws ExecutionException, InterruptedException {
    Future<Email> future = freeMarkerEmailService.sendEmail(template, email, params);
    return future.get();
  }

  /**
   * {@inheritDoc}
   */
  public TimeCommand withinTheNext(final long amount) {
    final Future<Email> future = freeMarkerEmailService.sendEmail(template, email, params);
    return new TimeCommand() {
      public Email seconds() throws ExecutionException, TimeoutException, InterruptedException {
        return future.get(amount, TimeUnit.SECONDS);
      }

      public Email milliseconds() throws ExecutionException, TimeoutException, InterruptedException {
        return future.get(amount, TimeUnit.MILLISECONDS);
      }
    };
  }
}
