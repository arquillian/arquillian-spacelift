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
package org.arquillian.spacelift.process.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arquillian.spacelift.execution.CountDownWatch;
import org.arquillian.spacelift.execution.Execution;
import org.arquillian.spacelift.execution.ExecutionCondition;
import org.arquillian.spacelift.execution.ExecutionException;
import org.arquillian.spacelift.execution.TimeoutExecutionException;
import org.arquillian.spacelift.execution.impl.ShutdownHooks;

/**
 * Representation of a process execution.
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class ProcessBasedExecution<RESULT> implements Execution<RESULT> {
    private static final Logger log = Logger.getLogger(ProcessBasedExecution.class.getName());

    private final Execution<RESULT> processFutureExecution;
    private final ProcessReference processReference;
    private final String processName;
    private final List<Integer> allowedExitCodes;

    private boolean shouldBeFinished;

    /**
     * Creates a process execution, add a name to the process
     *
     * @param processFutureExecution
     * @param processName
     */
    public ProcessBasedExecution(Execution<RESULT> processFutureExecution, ProcessReference processReference, String processName, List<Integer> allowedExitCodes) {
        this.processFutureExecution = processFutureExecution;
        this.processReference = processReference;
        this.processName = processName;
        this.allowedExitCodes = new ArrayList<Integer>(allowedExitCodes);
    }

    @Override
    public boolean isFinished() {

        // if process is marked as finished, consider it so
        if (isMarkedAsFinished()) {
            return true;
        }

        try {
            if (!processReference.isInitialized()) {
                return false;
            }
            processReference.getProcess().exitValue();
            return true;
        } catch (IllegalThreadStateException e) {
            return false;
        }
    }

    @Override
    public boolean isMarkedAsFinished() {
        return shouldBeFinished;
    }

    @Override
    public Execution<RESULT> terminate() throws ExecutionException {

        // if process has not yet started, terminate Future that would lead to its creation
        if (!processReference.isInitialized()) {
            processFutureExecution.terminate();
            return markAsFinished();
        }

        // close STDOUT of the process, if any
        OutputStream ostream = processReference.getProcess().getOutputStream();
        try {
            if (ostream != null) {
                ostream.flush();
                ostream.close();
            }
        } catch (IOException e) {
            log.log(Level.WARNING, "Ignoring IO exception while terminating the process {0}", processName);
        }

        // close STDERR of the process, if any
        InputStream errorStream = processReference.getProcess().getErrorStream();
        try {
            if (errorStream != null) {
                errorStream.close();
            }
        } catch (IOException e) {
            log.log(Level.WARNING, "Ignoring IO exception while terminating the process {0}", processName);
        }

        // close STDIN of the process, if any
        InputStream inputStream = processReference.getProcess().getInputStream();
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            log.log(Level.WARNING, "Ignoring IO exception while terminating the process {0}", processName);
        }

        processReference.getProcess().destroy();
        try {
            processReference.getProcess().waitFor();
        } catch (InterruptedException e) {
            log.log(Level.WARNING, "Ignoring Interuption Exception while terminating the process {0}", processName);
        }

        return this;
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
    public boolean hasFailed() {
        if (!isFinished()) {
            throw new IllegalStateException("Process " + processName
                + " is not yet finished, cannot determine whether it failed.");
        }

        // check whether we have specified exit value and if not adhere to defaults
        if (allowedExitCodes.isEmpty()) {
            return processReference.getProcess().exitValue() != 0;
        }

        return !allowedExitCodes.contains(processReference.getProcess().exitValue());
    }

    @Override
    public RESULT await() throws ExecutionException {
        if (processFutureExecution.hasFailed()) {
            return null;
        }
        return processFutureExecution.await();

    }

    @Override
    public RESULT awaitAtMost(long timeout, TimeUnit unit) throws ExecutionException, TimeoutExecutionException {
        if (processFutureExecution.hasFailed()) {
            return null;
        }
        return processFutureExecution.awaitAtMost(timeout, unit);
    }

    @Override
    public Execution<RESULT> reexecuteEvery(long step, TimeUnit unit) {
        processFutureExecution.reexecuteEvery(step, unit);
        return this;
    }

    @Override
    public RESULT until(long timeout, TimeUnit unit, ExecutionCondition<RESULT> condition) throws ExecutionException,
        TimeoutExecutionException {

        return processFutureExecution.until(timeout, unit, condition);
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
