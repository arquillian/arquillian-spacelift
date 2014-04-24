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
package org.arquillian.spacelift.process;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Representation of currently executed process.
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public interface ProcessDetails {

    /**
     * Returns name of the process
     *
     * @return process name
     */
    String getProcessName();

    /**
     * Returns stdout and stderr of the process in the form of list
     *
     * @return current output of the process
     */
    List<String> getOutput();

    /**
     * Returns exit value of the process.
     *
     * @throws IllegalStateException If exit value is not yet known
     */
    Integer getExitValue() throws IllegalStateException;

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
     * Adds a line to output of this process
     *
     * @param line
     * @return this
     */
    ProcessDetails appendOutput(Sentence line);
}