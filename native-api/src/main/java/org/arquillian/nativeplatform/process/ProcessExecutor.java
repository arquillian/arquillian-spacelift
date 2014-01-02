/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.arquillian.nativeplatform.process;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Executor service which is able to execute external process as well as callables
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public interface ProcessExecutor {

    /**
     * Submit callable to be executed
     *
     * @param callable to be executed
     * @return future
     */
    <T> Future<T> submit(Callable<T> callable);

    /**
     * Schedules a callable to be executed in regular intervals
     *
     * @param callable Callable
     * @param timeout Total timeout
     * @param step delay before next execution
     * @param unit time unit
     * @return {@code true} if executed successfully, false otherwise
     * @throws InterruptedException
     * @throws ExecutionException
     */
    Boolean scheduleUntilTrue(Callable<Boolean> callable, long timeout, long step, TimeUnit unit)
        throws InterruptedException, ExecutionException;

    /**
     * Spawns a process defined by command. Process output is consumed by {@link ProcessInteraction}.
     *
     * @param interaction command interaction
     * @param command command to be execution
     * @return spawned process execution
     */
    ProcessExecution spawn(ProcessInteraction interaction, String[] command) throws ProcessExecutionException;

    /**
     * Spawns a process defined by command. Process output is discarded.
     *
     * @param command command to be execution
     * @return spawned process execution
     * @throws ProcessExecutionException if anything goes wrong
     */
    ProcessExecution spawn(String... command) throws ProcessExecutionException;

    /**
     * Executes a process defined by command. Process output is consumed by {@link ProcessInteraction}. Waits for process to
     * finish and checks if process finished with status code 0
     *
     * @param interaction command interaction
     * @param command command to be execution
     * @return spawned process execution
     * @throws ProcessExecutionException if anything goes wrong
     */
    ProcessExecution execute(ProcessInteraction interaction, String[] command) throws ProcessExecutionException;

    /**
     * Executes a process defined by command. Process output is discarded. Waits for process to finish and checks if process
     * finished with status code 0
     *
     * @param command command to be execution
     * @return spawned process execution
     * @throws ProcessExecutionException if anything goes wrong
     */
    ProcessExecution execute(String... command) throws ProcessExecutionException;

    // FIXME
    ProcessExecutor removeShutdownHook(ProcessExecution p);

}
