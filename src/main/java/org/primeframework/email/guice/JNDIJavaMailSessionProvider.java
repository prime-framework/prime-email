/*
 * Copyright (c) 2012, Inversoft Inc., All Rights Reserved
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
package org.primeframework.email.guice;

import javax.mail.Session;
import javax.naming.InitialContext;
import javax.naming.NameParser;
import javax.naming.NamingException;

import com.google.inject.Provider;

/**
 * Provides JavaMail sessions via JNDI.
 *
 * @author Brian Pontarelli
 */
public class JNDIJavaMailSessionProvider implements Provider<Session> {
  private final String jndiName;

  public JNDIJavaMailSessionProvider(String jndiName) {
    this.jndiName = jndiName;
  }

  @Override
  public Session get() {
    try {
      InitialContext context = new InitialContext();
      NameParser np = context.getNameParser("");
      return (Session) context.lookup(np.parse(jndiName));
    } catch (NamingException ne) {
      throw new RuntimeException(ne);
    }
  }
}
