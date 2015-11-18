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

/**
 * This class is an abstraction of a simple email message.
 *
 * @author Brian Pontarelli
 */
public class Email {
  public List<Attachment> attachments = new ArrayList<Attachment>();

  public List<EmailAddress> bcc = new ArrayList<EmailAddress>();

  public List<EmailAddress> cc = new ArrayList<EmailAddress>();

  public EmailAddress from;

  public String html;

  public boolean renderHtml;

  public EmailAddress replyTo;

  public String subject;

  public boolean renderSubject;

  public String text;

  public boolean renderText;

  public List<EmailAddress> to = new ArrayList<EmailAddress>();
}