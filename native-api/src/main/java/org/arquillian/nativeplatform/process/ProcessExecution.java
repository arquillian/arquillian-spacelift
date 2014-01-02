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
package org.arquillian.nativeplatform.process;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Representation of a process execution. It automatically consumes process output and error output to avoid process getting
 * stuck.
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public interface ProcessExecution {

    /**
     * Returns identification of the process
     *
     * @return process id
     */
    String getProcessId();

    /**
     * Adds a line to output of this process
     *
     * @param line
     * @return this
     */
    ProcessExecution appendOutput(Sentence line);

    /**
     * Returns stdout and stderr of the process in the form of array
     *
     * @return current output of the process
     */
    List<String> getOutput();

    /**
     * Returns stdin of the process
     *
     * @return
     */
    OutputStream getStdin();

    /**
     * Returns stdout combined with stderr of the process
     *
     * @return
     */
    InputStream getStdoutAndStdErr();

    /**
     * Marks process execution as finished. This means that process did what we expected and might be considered as finished.
     * This is handy for environments where a native process starts another process we have no control of, but we can determine
     * process termination by scanning its stdout or stderr.
     *
     */
    void markAsFinished();

    /**
     * Checks whether process is marked as finished
     */
    boolean isMarkedAsFinished();

    /**
     * Checks whether process has finished
     *
     * @return true if process has finished, false otherwise
     */
    boolean isFinished();

    /**
     * Returns exit code
     *
     * @return exit code
     * @throws IllegalStateException thrown if process is not finished
     */
    int getExitCode() throws IllegalStateException;

    /**
     * Checks whether {@link ProcessExecution#getExitCode()} was {@code 0}
     *
     * @return true if exit code is not 0, false otherwise
     */
    boolean executionFailed();

    /**
     * Forcefully terminates the process execution
     */
    void terminate();

}
