/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat Middleware LLC, and individual contributors
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
package org.arquillian.spacelift.execution;

import java.util.concurrent.TimeUnit;

/**
 * Representation of currently running execution that promises to return {@code RESULT} somewhen in the future
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 * @param <RESULT> Type of result to be returned from this execution
 */
public interface Execution<RESULT> {

    /**
     * Marks execution as finished. This means that we've discovered that it did the work we expected.
     *
     * @return
     */
    Execution<RESULT> markAsFinished();

    /**
     * Registers a shutdown hook that is applied in case program is still running when JVM is going to terminate
     */
    Execution<RESULT> registerShutdownHook();

    /**
     * Checks whether process is marked as finished
     *
     * @return
     */
    boolean isMarkedAsFinished();

    /**
     * Checks whether process has finished
     *
     * @return
     */
    boolean isFinished();

    /**
     * Checks whether execution failed
     *
     * @return
     * @throws IllegalStateException If execution status could not yet be determined
     */
    boolean hasFailed() throws IllegalStateException;

    /**
     * Immediately terminates execution evaluation.
     *
     * @return
     * @throws ExecutionException
     */
    Execution<RESULT> terminate() throws ExecutionException;

    /**
     * Blocks execution of current thread, waiting for the execution to be finished.
     *
     * @return Result of execution
     * @throws ExecutionException
     */
    RESULT await() throws ExecutionException;

    /**
     * Blocks execution of current thread, waiting for the execution to be finished
     *
     * @param timeout the timeout
     * @param unit timeout unit
     * @return Result of execution
     * @throws ExecutionException If execution failed
     * @throws TimeoutExecutionException If execution was not retrieved during the timeout
     */
    RESULT awaitAtMost(long timeout, TimeUnit unit) throws ExecutionException, TimeoutExecutionException;

    /**
     * Sets interval for execution reexecution.
     *
     * @param step the time delay
     * @param unit time unit
     * @return this execution with reexecution interval set
     */
    Execution<RESULT> reexecuteEvery(long step, TimeUnit unit);

    /**
     * Continues (re)executing the execution until condition is satisfied. This call can be used to poll regularly for an
     * external process status.
     *
     * @param timeout the timeout
     * @param unit
     * @param condition condition that determines whether reexecution should continue
     * @return
     * @throws ExecutionException
     * @throws TimeoutExecutionException
     */
    RESULT until(long timeout, TimeUnit unit, ExecutionCondition<RESULT> condition) throws ExecutionException,
        TimeoutExecutionException;
}