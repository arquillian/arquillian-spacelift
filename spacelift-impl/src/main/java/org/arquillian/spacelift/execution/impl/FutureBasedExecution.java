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
package org.arquillian.spacelift.execution.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.arquillian.spacelift.execution.CountDownWatch;
import org.arquillian.spacelift.execution.Execution;
import org.arquillian.spacelift.execution.ExecutionCondition;
import org.arquillian.spacelift.execution.ExecutionException;
import org.arquillian.spacelift.execution.ExecutionService;
import org.arquillian.spacelift.execution.TimeoutExecutionException;

/**
 * Execution that is based on {@see Future} and causes thread to block if await is called.
 *
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 * @param <RESULT> Deferred result of the execution
 */
class FutureBasedExecution<RESULT> implements Execution<RESULT> {

    public static final long DEFAULT_POLL_INTERVAL = 500;
    public static final TimeUnit DEFAULT_POLL_TIME_UNIT = TimeUnit.MILLISECONDS;

    private final Callable<RESULT> executionTask;
    private final Future<RESULT> executionFuture;
    private final ExecutionService service;

    private long pollInterval;
    private TimeUnit pollUnit;

    private boolean shouldBeFinished;

    public FutureBasedExecution(ExecutionService service, Callable<RESULT> task, Future<RESULT> future) {
        this.service = service;
        this.executionTask = task;
        this.executionFuture = future;
        this.pollInterval = DEFAULT_POLL_INTERVAL;
        this.pollUnit = DEFAULT_POLL_TIME_UNIT;
    }

    @Override
    public Execution<RESULT> markAsFinished() {
        this.shouldBeFinished = true;
        return this;
    }

    @Override
    public Execution<RESULT> registerShutdownHook() {
        ShutdownHooks.addHookFor(this);
        return this;
    }

    @Override
    public boolean isMarkedAsFinished() {
        return shouldBeFinished;
    }

    @Override
    public boolean isFinished() {
        return isMarkedAsFinished() || executionFuture.isDone();
    }

    @Override
    public boolean hasFailed() {
        return executionFuture.isCancelled();
    }

    @Override
    public Execution<RESULT> terminate() {
        executionFuture.cancel(true);
        return this;
    }

    @Override
    public RESULT await() throws ExecutionException {
        try {
            return executionFuture.get();
        } catch (InterruptedException e) {
            throw unwrapException(e, "Interrupted while executing a task");
        } catch (java.util.concurrent.ExecutionException e) {
            throw unwrapException(e, "Execution of a task failed");
        }
    }

    @Override
    public RESULT awaitAtMost(long timeout, TimeUnit unit) {
        try {
            return executionFuture.get(timeout, unit);
        } catch (InterruptedException e) {
            throw unwrapException(e, "Interrupted while executing a task");
        } catch (java.util.concurrent.ExecutionException e) {
            throw unwrapException(e, "Execution of a task failed");
        } catch (TimeoutException e) {
            throw unwrapExceptionAsTimeoutException(e, "Timed out after {0}{1} while executing a task", timeout, unit);
        }
    }

    @Override
    public Execution<RESULT> reexecuteEvery(long step, TimeUnit unit) {
        this.pollInterval = step;
        this.pollUnit = unit;
        return this;
    }

    @Override
    public RESULT until(long timeout, TimeUnit unit, ExecutionCondition<RESULT> condition) throws ExecutionException,
        TimeoutExecutionException {

        CountDownWatch countdown = new CountDownWatch(timeout, unit);
        Execution<RESULT> currentExecution = new FutureBasedExecution<RESULT>(service, executionTask, executionFuture);

        // keep scheduling task until we have some time
        while (countdown.timeLeft() > 0) {

            Execution<RESULT> nextExecution = service.schedule(executionTask, pollInterval, pollUnit);

            try {
                RESULT result = currentExecution.awaitAtMost(countdown.timeLeft(), countdown.getTimeUnit());
                if (condition.satisfiedBy(result)) {
                    // terminate execution of next callable
                    // we want to ignore failures in termination
                    try {
                        nextExecution.terminate();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    return result;
                }
            } catch (TimeoutExecutionException e) {
                continue;
            }

            // continue evaluating scheduled execution
            currentExecution = nextExecution;
        }

        throw new TimeoutExecutionException("Unable to trigger condition within {0} {1}.", timeout, unit.toString()
            .toLowerCase());

    }

    private static ExecutionException unwrapException(Throwable cause, String messageFormat, Object... parameters) {
        Throwable current = cause;
        ExecutionException deepestCause = null;
        while (current != null) {
            if (current instanceof ExecutionException) {
                deepestCause = (ExecutionException) current;
            }
            current = current.getCause();
        }

        if (deepestCause != null) {
            return deepestCause.prependMessage(messageFormat, parameters);
        }

        return new ExecutionException(cause, messageFormat, parameters);
    }

    private static TimeoutExecutionException unwrapExceptionAsTimeoutException(Throwable cause, String messageFormat,
        Object... parameters) {
        Throwable current = cause;
        while (current != null) {
            if (current instanceof ExecutionException) {
                return new TimeoutExecutionException(current, messageFormat, parameters);
            }
            current = current.getCause();
        }

        return new TimeoutExecutionException(cause, messageFormat, parameters);
    }

    @Override
    public RESULT awaitAtMost(CountDownWatch timeout) throws ExecutionException, TimeoutExecutionException {
        return awaitAtMost(timeout.timeout(), timeout.getTimeUnit());
    }

    @Override
    public RESULT until(CountDownWatch timeout, ExecutionCondition<RESULT> condition) throws ExecutionException, TimeoutExecutionException {
        return until(timeout.timeout(), timeout.getTimeUnit(), condition);
    }
}
