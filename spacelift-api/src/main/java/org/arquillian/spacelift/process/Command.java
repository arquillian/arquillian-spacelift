/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
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
package org.arquillian.spacelift.process;

import java.util.List;

/**
 * Command abstraction. Commands are built via {@link CommandBuilder}.
 *
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 */
public interface Command {

    /**
     * Returns a name of the program to be executed
     *
     * @return program name
     */
    String getProgramName();

    /**
     * Returns a list of parameters. Program name is not included in this list.
     *
     * @see Command#getFullCommand()
     */
    List<String> getParameters();

    /**
     * Returns a parameter by index. If {@code index} is {@code 0}, program name is returned
     * <p>
     * If index is out of range, {@code null} is returned
     */
    String getParameter(int i);

    /**
     * Returns number of parameters
     */
    int getNumberOfParameters();

    /**
     * Returns program name and parameters concatenated into a single list
     */
    List<String> getFullCommand();
}
