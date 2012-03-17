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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.primeframework.email.domain.Email;

/**
 * This interface defines time metrics for waiting for emails to be sent.
 *
 * @author Brian Pontarelli
 */
public interface TimeCommand {
  /**
   * The time frame is in seconds.
   *
   * @return The email if it was sent within the amount of time, or an exception is thrown.
   * @throws ExecutionException   If the execution of the send fails.
   * @throws TimeoutException     If the duration times out without sending.
   * @throws InterruptedException If the thread used to send the email is interrupted.
   */
  Email seconds() throws ExecutionException, TimeoutException, InterruptedException;

  /**
   * The time frame is in milli-seconds.
   *
   * @return The email if it was sent within the amount of time, or an exception is thrown.
   * @throws ExecutionException   If the execution of the send fails.
   * @throws TimeoutException     If the duration times out without sending.
   * @throws InterruptedException If the thread used to send the email is interrupted.
   */
  Email milliseconds() throws ExecutionException, TimeoutException, InterruptedException;
}