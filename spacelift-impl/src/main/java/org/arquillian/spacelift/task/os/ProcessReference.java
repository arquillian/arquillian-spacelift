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

import org.arquillian.spacelift.execution.ExecutionException;

/**
 * Holder of executed process that can be asynchronously set later on.
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 */
class ProcessReference {

    private final String processName;
    private volatile Process process;

    public ProcessReference(String processName) {
        this.processName = processName;
    }

    public boolean isInitialized() {
        return process != null;
    }

    public synchronized Process getProcess() throws ExecutionException {

        if (!isInitialized()) {
            throw new ExecutionException(
                "Unable to get {0} process identifier in ref {1}. This is a bug in Arquillian Spacelift.",
                processName,
                hashCode());
        }

        return process;
    }

    public void setProcess(Process process) {
        if (process == null) {
            throw new ExecutionException(
                "Unable to SET SET process object for {0}. This is a bug in Arquillian Spacelift.",
                processName);
        }
        this.process = process;
    }
}
