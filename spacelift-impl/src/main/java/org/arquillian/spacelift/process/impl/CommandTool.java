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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.arquillian.spacelift.execution.Execution;
import org.arquillian.spacelift.execution.ExecutionException;
import org.arquillian.spacelift.execution.ValidationException;
import org.arquillian.spacelift.execution.Tasks;
import org.arquillian.spacelift.process.Command;
import org.arquillian.spacelift.process.CommandBuilder;
import org.arquillian.spacelift.process.ProcessInteraction;
import org.arquillian.spacelift.process.ProcessInteractionBuilder;
import org.arquillian.spacelift.process.ProcessResult;
import org.arquillian.spacelift.tool.Tool;

/**
 * Tool that is able to execute an external, operating system dependent command.
 *
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class CommandTool extends Tool<Object, ProcessResult> {

    protected CommandBuilder commandBuilder;
    protected ProcessInteraction interaction;
    protected List<Integer> allowedExitCodes;
    protected File workingDirectory;
    protected Map<String, String> environment;
    protected boolean isDaemon;

    protected ProcessReference processRef;

    public CommandTool() {
        this.interaction = ProcessInteractionBuilder.NO_INTERACTION;
        this.allowedExitCodes = new ArrayList<Integer>();
        this.workingDirectory = null;
        this.environment = new HashMap<String, String>();
        this.isDaemon = false;
    }

    @Override
    protected Collection<String> aliases() {
        return Arrays.asList("run");
    }

    /**
     * Sets executable to be executed. This can either a an absolute path or executed on path of underlying file system
     *
     * @param programName program name
     * @return
     * @throws IllegalArgumentException If program name is null or empty
     */
    public CommandTool programName(CharSequence programName) throws IllegalArgumentException {
        Validate.notNullOrEmpty(programName, "Program name must not be empty nor null");
        this.commandBuilder = new CommandBuilder(programName);
        return this;
    }

    /**
     * Adds a list of parameters to the command to be executed
     *
     * @param parameters parameters
     * @return
     */
    public CommandTool parameters(List<? extends CharSequence> parameters) {
        commandBuilder.parameters(parameters);
        return this;
    }

    /**
     * Adds a list of parameters to the command to be executed
     *
     * @param parameters parameters
     * @return
     */
    public CommandTool parameters(CharSequence... parameters) {
        commandBuilder.parameters(parameters);
        return this;
    }

    /**
     * Adds a parameter to the command to be executed
     *
     * @param parameter parameter
     * @return
     */
    public CommandTool parameter(CharSequence parameter) {
        commandBuilder.parameter(parameter);
        return this;
    }

    /**
     * Splits {@code sequenceToBeParsed} into list of parameters, using unescaped spaces as delimiters
     *
     * @param sequenceToBeParsed string to be parsed
     * @return
     */
    public CommandTool splitToParameters(CharSequence sequenceToBeParsed) {
        commandBuilder.parameters(sequenceToBeParsed);
        return this;
    }

    /**
     * Sets interaction for the command
     *
     * @param interaction the interaction
     * @return
     */
    public CommandTool interaction(ProcessInteraction interaction) {
        this.interaction = interaction;
        return this;
    }

    /**
     * Sets interaction for the command
     *
     * @param interactionBuilder the interaction
     * @return
     */
    public CommandTool interaction(ProcessInteractionBuilder interactionBuilder) {
        this.interaction = interactionBuilder.build();
        return this;
    }

    /**
     * Sets the command. Overrides all previous parameters and program name
     *
     * @param command the command
     * @return
     */
    public CommandTool command(Command command) {
        this.commandBuilder = new CommandBuilder(command.getFullCommand().toArray(new String[0]));
        return this;
    }

    /**
     * Sets the command. Overrides all previous parameters and program name
     *
     * @param command the command
     * @return
     */
    public CommandTool command(CommandBuilder commandBuilder) {
        this.commandBuilder = commandBuilder;
        return this;
    }

    /**
     * Adds list of valid exit codes for the command. If command finishes execution and exit code does
     * not match the one set, {@see ExecutionException} is thrown
     *
     * By default, allowed exit code is set to {@code 0}
     *
     * @param exitCodes the exit code
     * @param others possible other codes
     * @return
     * @see ExecutionException
     */
    public CommandTool shouldExitWith(Integer... exitCodes) {
        if (exitCodes.length > 0) {
            allowedExitCodes.addAll(Arrays.asList(exitCodes));
        }
        return this;
    }

    /**
     * Sets working directory for the command
     *
     * @param workingDirectory working directory, can be {@code null} to use current directory of running Java process
     * @return
     * @throws IllegalArgumentException if working directory does not exist
     */
    public CommandTool workingDir(String workingDirectory) throws IllegalArgumentException {

        if (workingDirectory == null) {
            this.workingDirectory = null;
            return this;
        }

        File workingDirectoryFile = new File(workingDirectory);

        if (!workingDirectoryFile.exists()) {
            throw new IllegalArgumentException("Specified path " + workingDirectoryFile.getAbsolutePath() + " does not exist!");
        }
        if (!workingDirectoryFile.isDirectory()) {
            throw new IllegalArgumentException("Specified path " + workingDirectoryFile.getAbsolutePath()
                + " is not a directory!");
        }

        this.workingDirectory = workingDirectoryFile;
        return this;
    }

    /**
     * Adds a map of key, value environment variables to the default process environment
     *
     * @param envVariables environment variables. Value might be null.
     * @return
     * @throws IllegalArgumentException
     */
    public CommandTool addEnvironment(Map<String, String> envVariables) throws IllegalArgumentException {
        Validate.notNull(envVariables, "Environment variables must not be null");
        this.environment.putAll(envVariables);
        return this;
    }

    /**
     * Adds a sequence of key, value environment variables to the default process environment
     *
     * @param envVariables environment variables. Value might be null.
     * @return
     * @throws IllegalArgumentException If values do not form complete pairs or if key is null
     */
    public CommandTool addEnvironment(CharSequence... envVariables) throws IllegalArgumentException {

        if (envVariables.length % 2 == 1) {
            throw new IllegalArgumentException("Environment variables must be a sequence of key, value pairs.");
        }

        for (int i = 0; i < (envVariables.length / 2); i += 2) {
            CharSequence key = envVariables[i];
            CharSequence value = envVariables[i + 1];

            Validate.notNull(key, "Environment variable name must not be null nor empty");
            environment.put(key.toString(), value != null ? value.toString() : null);
        }

        return this;
    }

    /**
     * Indicates that command should be executed as daemon and survive JVM process.
     *
     * @return
     */
    public CommandTool runAsDaemon() {
        this.isDaemon = true;
        return this;
    }

    @Override
    public Execution<ProcessResult> execute() throws ExecutionException {
        // here we rewrap future based execution into process based execution to get better details about execution
        // and ability to terminate the process
        this.processRef = new ProcessReference(commandBuilder.build().getProgramName());
        Execution<ProcessResult> processFutureExecution = super.execute();

        ProcessBasedExecution<ProcessResult> execution = new ProcessBasedExecution<ProcessResult>(processFutureExecution,
            processRef,
            commandBuilder.build().getProgramName(),
            allowedExitCodes);

        return execution;
    }

    @Override
    protected ProcessResult process(Object input) throws Exception {

        Validate.executionNotNull(commandBuilder, "Command must not be null");

        Command command = commandBuilder.build();
        Process process = null;

        Execution<Process> spawnedProcess = Tasks.prepare(SpawnProcessTask.class)
            .redirectErrorStream(true)
            .shouldExitWith(allowedExitCodes)
            .command(command)
            .workingDir(workingDirectory)
            .addEnvironment(environment)
            .runAsDaemon(isDaemon)
            .execute();

        // wait for process to finish
        process = spawnedProcess.await();

        // set processReference
        processRef.setProcess(process);

        // handle IO of spawned process
        Execution<ProcessResult> processConsumer = Tasks.chain(spawnedProcess, ConsumeProcessOutputTask.class)
            .programName(command.getProgramName()).interaction(interaction).execute();

        // wait for process to finish IO
        ProcessResult result = processConsumer.await();

        if (spawnedProcess.hasFailed()) {
            throw new ExecutionException("Invocation of \"{0}\" failed with {1}", new Object[] {
                command,
                result.exitValue() });
        }

        return result;
    }

    @Override
    protected void validate() throws ValidationException {
    }
}
