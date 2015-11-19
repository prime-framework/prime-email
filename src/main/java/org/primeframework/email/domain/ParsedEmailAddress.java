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

import freemarker.template.Template;

/**
 * An email address.
 *
 * @author Brian Pontarelli
 */
public class ParsedEmailAddress {
  public String address;

  public Template display;

  public ParsedEmailAddress() {
  }

  public ParsedEmailAddress(String address) {
    this.address = address;
    this.display = null;
  }

  public ParsedEmailAddress(String address, Template display) {
    this.address = address;
    this.display = display;
  }
}
