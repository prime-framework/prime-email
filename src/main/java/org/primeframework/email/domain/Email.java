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
package org.primeframework.email.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class is an abstraction of a simple email message.
 *
 * @author Brian Pontarelli
 */
public class Email {
  public List<EmailHeader> additionalHeaders = new ArrayList<>();

  public List<Attachment> attachments = new ArrayList<>();

  public List<EmailAddress> bcc = new ArrayList<>();

  public List<EmailAddress> cc = new ArrayList<>();

  public EmailAddress from;

  public String html;

  public EmailAddress replyTo;

  public String subject;

  public String text;

  public List<EmailAddress> to = new ArrayList<>();

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Email)) {
      return false;
    }
    Email email = (Email) o;
    return Objects.equals(additionalHeaders, email.additionalHeaders) &&
        Objects.equals(attachments, email.attachments) &&
        Objects.equals(bcc, email.bcc) &&
        Objects.equals(cc, email.cc) &&
        Objects.equals(from, email.from) &&
        Objects.equals(html, email.html) &&
        Objects.equals(replyTo, email.replyTo) &&
        Objects.equals(subject, email.subject) &&
        Objects.equals(text, email.text) &&
        Objects.equals(to, email.to);
  }

  @Override
  public int hashCode() {
    return Objects.hash(additionalHeaders, attachments, bcc, cc, from, html, replyTo, subject, text, to);
  }
}