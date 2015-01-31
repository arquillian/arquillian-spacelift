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
package org.arquillian.spacelift.task.os;

import java.util.List;

import org.arquillian.spacelift.process.ProcessResult;

/**
 * Default implementation of {@link ProcessResult
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
class ProcessResultImpl implements ProcessResult {

    private final Process process;
    private final String processName;
    private final List<String> output;

    public ProcessResultImpl(final Process process, final String processName, final List<String> output) {
        this.processName = processName;
        this.process = process;
        this.output = output;
    }

    @Override
    public String processName() {
        return processName;
    }

    @Override
    public List<String> output() {
        return output;
    }

    @Override
    public Integer exitValue() throws IllegalStateException {
        try {
            return process.exitValue();
        } catch (IllegalThreadStateException e) {
            throw new IllegalStateException("Process " + processName + " is not yet finished, cannot determine exit value.");
        }
    }

}
