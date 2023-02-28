/*
 * Copyright (c) 2023, Inversoft Inc., All Rights Reserved
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

import java.io.Closeable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.inject.Provider;


/**
 * @author Daniel DeGroff
 */
public class EmailExecutorServiceProvider implements Provider<ExecutorService>, Closeable {
  private ExecutorService executorService;

  @Override
  public void close() {
    executorService.shutdownNow();
  }

  @Override
  public ExecutorService get() {

    // Please note:
    //
    //  When using a LinkedBlockingQueue with the ExecutorService, the corePoolSize needs to match the maximumPoolSize. In other words
    //  it has to be a fixed size thread pool. By default, the LinkedBlockingQueue has an unbound capacity (Integer.MAX_VALUE), so it seems
    //  that the ExecutorService will happily queue and never spin up workers past the corePoolSize. See ThreadPoolExecutor.execute for details.
    //
    //  Summary of ThreadPoolExecutor.execute:
    //    1. If thread count is less than corePoolSize, add a new worker with the current command.
    //    2. Else, queue command
    //    3. Else, if queue failed, add a new worker thread.
    //
    //  So because we were using a LinkedBlockingQueue essentially unbound, as long as we could queue up events, we would not start any new threads.
    //
    //  For this reason, you either have to use a fixed thread pool, or use a SynchronousQueue which is essentially queue w/out capacity - a pipe.
    //

    // Create a fixed thread pool with an unbound blocking queue. This means we will always have 5 threads waiting to work, and
    // when all 5 threads are busy, new work will be added to an unbound queue.
    executorService = Executors.newFixedThreadPool(5,
        r -> {
          Thread t = new Thread(r, threadName());
          t.setDaemon(true);
          return t;
        });

    return executorService;
  }

  protected String threadName() {
    return "Prime-Email Executor Thread";
  }
}
