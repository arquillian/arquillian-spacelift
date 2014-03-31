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

import org.arquillian.spacelift.process.ProcessExecution;
import org.arquillian.spacelift.process.ProcessExecutionException;
import org.arquillian.spacelift.process.Sentence;

/**
 * Representation of a process execution
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class ProcessExecutionImpl implements ProcessExecution {

    private boolean shouldBeFinished;

    private final String processName;

    private final Process process;

    private final List<String> output;

    private final OutputStream ostream;

    /**
     * Creates a process execution, add a name to the process
     *
     * @param process
     * @param processName
     */
    public ProcessExecutionImpl(Process process, String processName) {
        this.process = process;
        this.processName = processName;
        this.output = new ArrayList<String>();
        this.ostream = new BufferedOutputStream(process.getOutputStream());
        this.shouldBeFinished = false;
    }

    /**
     *
     * @return process
     */
    public Process getProcess() {
        return process;
    }

    @Override
    public String getProcessName() {
        return processName;
    }

    @Override
    public ProcessExecution appendOutput(Sentence line) {
        output.add(line.toString());
        return this;
    }

    /**
     *
     * @return current output of the process
     */
    public List<String> getOutput() {
        return output;
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
    public int getExitCode() throws IllegalStateException {

        if (!isFinished()) {
            throw new IllegalStateException("Process " + processName + " is not yet finished");
        }

        return process.exitValue();
    }

    @Override
    public void markAsFinished() {
        this.shouldBeFinished = true;
    }

    @Override
    public boolean isMarkedAsFinished() {
        return shouldBeFinished;
    }

    @Override
    public boolean executionFailed() {
        return getExitCode() != 0;
    }

    @Override
    public OutputStream getStdin() {
        return ostream;
    }

    @Override
    public InputStream getStdoutAndStdErr() {
        return process.getInputStream();
    }

    @Override
    public void terminate() {
        process.destroy();
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            throw new ProcessExecutionException(e, "Interrupted while waiting for {0} to be terminated", processName);
        }
    }

}
