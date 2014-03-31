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
package org.arquillian.spacelift.process;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;
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
     * Adds given environment map to the default inherited environment be used for next process execution.
     * Previous environments settings are discarded.
     *
     * @param environment The new environment settings addon
     * @return Modified instance
     * @throws IllegalStateException In case that environment is {@code null} or contains empty values
     */
    ProcessExecutor setEnvironment(Map<String, String> environment) throws IllegalStateException;

    /**
     * Sets path to be used as a working directory for next process execution. The argument may be {@code null} which
     * means to use the working directory of the current Java process.
     *
     * @see java.lang.ProcessBuilder#directory(java.io.File)
     *
     * @param workingDirectory The working directory
     * @return Modified instance
     * @throws IllegalArgumentException In case that specified path does not exist or is not a directory.
     */
    ProcessExecutor setWorkingDirectory(String workingDirectory) throws IllegalArgumentException;

    /**
     * Sets path to be used as a working directory for next process execution. The argument may be {@code null} which
     * means to use the working directory of the current Java process.
     *
     * @see java.lang.ProcessBuilder#directory(java.io.File)
     *
     * @param workingDirectory The working directory
     * @return Modified instance
     * @throws IllegalArgumentException In case that specified path does not exist or is not a directory.
     */
    ProcessExecutor setWorkingDirectory(File workingDirectory) throws IllegalArgumentException;

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
     * @param callable {@link Callable}
     * @param timeout Total timeout
     * @param step delay before next execution
     * @param unit time unit
     * @return {@code true} if executed successfully, false otherwise
     * @throws ProcessExecutionException if anything goes wrong
     */
    Boolean scheduleUntilTrue(Callable<Boolean> callable, long timeout, long step, TimeUnit unit)
        throws ProcessExecutionException;

    /**
     * Spawns a process defined by command. Process output is consumed by {@link ProcessInteraction}.
     *
     * @param interaction command interaction
     * @param command command to be executed
     * @return spawned process execution
     * @throws ProcessExecutionException if anything goes wrong
     */
    ProcessExecution spawn(ProcessInteraction interaction, String[] command) throws ProcessExecutionException;

    /**
     * Spawns a process defined by command. Process output is consumed by {@link ProcessInteraction}.
     *
     * @param interaction command interaction
     * @param command command to be executed
     * @return spawned process execution
     * @throws ProcessExecutionException if anything goes wrong
     */
    ProcessExecution spawn(ProcessInteraction interaction, Command command) throws ProcessExecutionException;

    /**
     * Spawns a process defined by command. Process output is discarded.
     *
     * @param command command to be executed
     * @return spawned process execution
     * @throws ProcessExecutionException if anything goes wrong
     */
    ProcessExecution spawn(String... command) throws ProcessExecutionException;

    /**
     * Spawns a process defined by command. Process output is discarded.
     *
     * @param command command to be executed
     * @return spawned process execution
     * @throws ProcessExecutionException if anything goes wrong
     */
    ProcessExecution spawn(Command command) throws ProcessExecutionException;

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
     * Executes a process defined by command. Process output is consumed by {@link ProcessInteraction}. Waits for process to
     * finish and checks if process finished with status code 0
     *
     * @param interaction command interaction
     * @param command command to be executed
     * @return spawned process execution
     * @throws ProcessExecutionException if anything goes wrong
     */
    ProcessExecution execute(ProcessInteraction interaction, Command command) throws ProcessExecutionException;

    /**
     * Executes a process defined by command. Process output is discarded. Waits for process to finish and checks if process
     * finished with status code 0
     *
     * @param command command to be executed
     * @return spawned process execution
     * @throws ProcessExecutionException if anything goes wrong
     */
    ProcessExecution execute(String... command) throws ProcessExecutionException;

    /**
     * Executes a process defined by command. Process output is discarded. Waits for process to finish and checks if process
     * finished with status code 0
     *
     * @param command command to be executed
     * @return spawned process execution
     * @throws ProcessExecutionException if anything goes wrong
     */
    ProcessExecution execute(Command command) throws ProcessExecutionException;

    // FIXME
    // This method should not be public nor available
    ProcessExecutor removeShutdownHook(ProcessExecution p);

}
