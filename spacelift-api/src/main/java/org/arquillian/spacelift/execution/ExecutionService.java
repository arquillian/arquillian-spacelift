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

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.arquillian.spacelift.process.Command;
import org.arquillian.spacelift.task.Task;

/**
 * Wrapper to execute a task, based on {@link Callable}, {@link Command} or {@link Task}.
 * Allows both synchronous and asynchronous task execution
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 */
public interface ExecutionService {

    /**
     * Executes a task asynchronously.
     *
     * @param task
     *     Task to be executed
     *
     * @return Object that describes current execution
     *
     * @throws ExecutionException
     */
    <T> Execution<T> execute(Callable<T> task) throws ExecutionException;

    /**
     * Schedules a task to be executed periodically until {@code breakCondition} is evaluate to {@code true} or timeout is
     * reached.
     *
     * @param task
     *     Task to be executed
     * @param breakCondition
     *     break condition
     * @param step
     *     Time to wait until next execution should be performed
     * @param timeout
     *     Total timeout
     * @param unit
     *     Timeout unit
     *
     * @throws TimeoutExecutionException
     * @throws ExecutionException
     */
    <T> Execution<T> schedule(Callable<T> task, long delay, TimeUnit unit)
        throws TimeoutExecutionException, ExecutionException;
}
