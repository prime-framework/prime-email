/*
 * Copyright (c) 2019, Inversoft Inc., All Rights Reserved
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Daniel DeGroff
 */
public class DefaultMessagingExceptionHandler implements MessagingExceptionHandler {
  private final static Logger logger = LoggerFactory.getLogger(DefaultMessagingExceptionHandler.class);

  public void handle(PrimeMessagingException e) {
    logger.error("Unable to send email via JavaMail", e);
  }
}
