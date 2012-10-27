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
package org.primeframework.email.config.guice;

import org.primeframework.email.config.DefaultEmailConfiguration;
import org.primeframework.email.config.EmailConfiguration;

import com.google.inject.AbstractModule;

/**
 * Binds the email configuration.
 *
 * @author Brian Pontarelli
 */
public class ConfigurationModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(EmailConfiguration.class).to(DefaultEmailConfiguration.class);
  }
}
