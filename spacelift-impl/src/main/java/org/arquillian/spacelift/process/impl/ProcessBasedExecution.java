/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.arquillian.spacelift.execution.Execution;
import org.arquillian.spacelift.execution.ExecutionException;
import org.arquillian.spacelift.execution.Sentence;
import org.arquillian.spacelift.execution.impl.ShutdownHooks;
import org.arquillian.spacelift.process.ProcessDetails;

/**
 * Representation of a process execution
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class ProcessBasedExecution implements Execution<ProcessDetails> {

    private boolean shouldBeFinished;
    private final ProcessDetails processDetails;
    private final Process process;

    /**
     * Creates a process execution, add a name to the process
     *
     * @param process
     * @param processName
     */
    public ProcessBasedExecution(Process process, String processName) {
        this.process = process;
        this.processDetails = new ProcessDetailsImpl(process, processName);

    }

    @Override
    public boolean isFinished() {
        try {
            process.exitValue();
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
    public Execution<ProcessDetails> terminate() throws ExecutionException {
        process.destroy();
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            throw new ExecutionException(e,
                "Interrupted while waiting for {0} to be terminated",
                processDetails.getProcessName());
        }
        return this;
    }

    @Override
    public Execution<ProcessDetails> markAsFinished() {
        this.shouldBeFinished = true;
        return this;
    }

    @Override
    public Execution<ProcessDetails> registerShutdownHook() {
        ShutdownHooks.addHookFor(this);
        return this;
    }

    @Override
    public boolean hasFailed() {
        // FIXME there are likely better ways how to do this
        if (!isFinished()) {
            throw new IllegalStateException("Process " + processDetails.getProcessName() + " is not yet finished");
        }

        return process.exitValue() != 0;
    }

    @Override
    public ProcessDetails result() throws ExecutionException {
        return processDetails;
    }

    @Override
    public ProcessDetails result(long timeout, TimeUnit unit) {
        return processDetails;
    }

    private static class ProcessDetailsImpl implements ProcessDetails {

        private final String processName;
        private final List<String> output;

        private final OutputStream ostream;
        private final InputStream istream;

        public ProcessDetailsImpl(Process process, String processName) {
            this.processName = processName;
            this.output = new ArrayList<String>();
            this.ostream = new BufferedOutputStream(process.getOutputStream());
            this.istream = process.getInputStream();
        }

        @Override
        public String getProcessName() {
            return processName;
        }

        @Override
        public List<String> getOutput() {
            return output;
        }

        @Override
        public OutputStream getStdin() {
            return ostream;
        }

        @Override
        public InputStream getStdoutAndStdErr() {
            return istream;
        }

        @Override
        public ProcessDetails appendOutput(Sentence line) {
            output.add(line.toString());
            return this;
        }

    }

}
