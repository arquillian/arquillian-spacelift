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

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.arquillian.spacelift.process.ProcessDetails;
import org.arquillian.spacelift.process.Sentence;

/**
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
class ProcessDetailsImpl implements ProcessDetails {

    private final Process process;
    private final String processName;
    private final List<String> output;

    private final OutputStream ostream;
    private final InputStream istream;

    public ProcessDetailsImpl(Process process, String processName) {
        this.processName = processName;
        this.process = process;
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

    @Override
    public Integer getExitValue() throws IllegalStateException {

        try {
            return process.exitValue();
        } catch (IllegalThreadStateException e) {
            throw new IllegalStateException("Process " + processName + " is not yet finished, cannot determine exit value.");
        }

    }
}