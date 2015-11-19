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
import java.util.Objects;

/**
 * This class is an abstraction of a simple email message.
 *
 * @author Brian Pontarelli
 */
public class RawEmailTemplates {
  public List<String> bccDisplays = new ArrayList<>();

  public List<String> ccDisplays = new ArrayList<>();

  public String fromDisplay;

  public String html;

  public String replyToDisplay;

  public String subject;

  public String text;

  public List<String> toDisplays = new ArrayList<>();

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof RawEmailTemplates)) {
      return false;
    }
    RawEmailTemplates that = (RawEmailTemplates) o;
    return Objects.equals(bccDisplays, that.bccDisplays) &&
        Objects.equals(ccDisplays, that.ccDisplays) &&
        Objects.equals(fromDisplay, that.fromDisplay) &&
        Objects.equals(html, that.html) &&
        Objects.equals(replyToDisplay, that.replyToDisplay) &&
        Objects.equals(subject, that.subject) &&
        Objects.equals(text, that.text) &&
        Objects.equals(toDisplays, that.toDisplays);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bccDisplays, ccDisplays, fromDisplay, html, replyToDisplay, subject, text, toDisplays);
  }
}