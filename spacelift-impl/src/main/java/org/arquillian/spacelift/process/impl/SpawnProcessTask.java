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

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.arquillian.spacelift.execution.Execution;
import org.arquillian.spacelift.execution.ExecutionException;
import org.arquillian.spacelift.execution.Task;
import org.arquillian.spacelift.process.Command;

/**
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
class SpawnProcessTask extends Task<Object, Process> {

    private Command command;
    private boolean redirectErrorStream;
    private List<Integer> allowedExitCodes;
    private File workingDirectory;
    private Map<String, String> environment;
    private boolean runsAsDaemon;

    public SpawnProcessTask redirectErrorStream(boolean redirectErrorStream) {
        this.redirectErrorStream = redirectErrorStream;
        this.environment = new HashMap<String, String>();
        return this;
    }

    public SpawnProcessTask command(Command command) {
        this.command = command;
        return this;
    }

    public SpawnProcessTask workingDirectory(File workingDirectory) {
        this.workingDirectory = workingDirectory;
        return this;
    }

    public SpawnProcessTask addEnvironment(Map<String, String> environment) {
        this.environment = environment;
        return this;
    }

    public SpawnProcessTask shouldExitWith(List<Integer> allowedExitCodes) {
        this.allowedExitCodes = allowedExitCodes;
        return this;
    }

    public SpawnProcessTask runAsDaemon(boolean isDaemon) {
        this.runsAsDaemon = isDaemon;
        return this;
    }

    @Override
    public Execution<Process> execute() throws ExecutionException {

        // here we rewrap future based execution into process based execution to get better details about execution
        Execution<Process> processFutureExecution = super.execute();

        Process process = processFutureExecution.await();
        ProcessReference ref = new ProcessReference(command.getProgramName());
        ref.setProcess(process);

        ProcessBasedExecution<Process> execution = new ProcessBasedExecution<Process>(processFutureExecution, ref,
            command.getProgramName(),
            allowedExitCodes);

        // register shutdown hook
        if (!runsAsDaemon) {
            execution.registerShutdownHook();
        }

        return execution;
    }

    @Override
    protected Process process(Object input) throws Exception {

        Validate.executionNotNull(command, "Command must not be null");
        Validate.executionNotNull(command.getProgramName(), "Command program name must not be null");

        ProcessBuilder builder = new ProcessBuilder(command.getFullCommand());
        builder.directory(workingDirectory);
        builder.environment().putAll(environment);
        builder.redirectErrorStream(redirectErrorStream);
        return builder.start();
    }

}
