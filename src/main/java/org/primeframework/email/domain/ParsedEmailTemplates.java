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
package org.primeframework.email.domain;

import java.util.ArrayList;
import java.util.List;

import freemarker.template.Template;

/**
 * This class is an abstraction of a simple email message.
 *
 * @author Brian Pontarelli
 */
public class ParsedEmailTemplates {
  public List<ParsedEmailAddress> bcc = new ArrayList<>();

  public List<ParsedEmailAddress> cc = new ArrayList<>();

  public ParsedEmailAddress from;

  public Template html;

  public ParsedEmailAddress replyTo;

  public Template subject;

  public Template text;

  public List<ParsedEmailAddress> to = new ArrayList<>();
}