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
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * This class tests the email command.
 *
 * @author James Humphrey
 */
public class EmailBuilderImplTest {
  @Test
  public void templateParams() {
    EmailBuilder eb = new EmailBuilder(null, new Email(), (eb1) -> null, (eb1) -> {});
    eb = eb.withTemplateParam("key1", "value1").withTemplateParam("key2", "value2");

    assertEquals(eb.getTemplateParams().size(), 2);

    assertEquals(eb.getTemplateParams().get("key1"), "value1");
    assertEquals(eb.getTemplateParams().get("key2"), "value2");
  }

  @Test
  public void subjectExplicit() {
    EmailBuilder eb = new EmailBuilder(null, new Email(), (eb1) -> null, (eb1) -> {});
    eb.withSubject("test subject");
    assertEquals(eb.getSubject(), "test subject");
  }
}
