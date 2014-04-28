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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.arquillian.spacelift.execution.Execution;
import org.arquillian.spacelift.execution.ExecutionException;
import org.arquillian.spacelift.execution.Tasks;
import org.arquillian.spacelift.process.Command;
import org.arquillian.spacelift.process.CommandBuilder;
import org.arquillian.spacelift.process.ProcessInteraction;
import org.arquillian.spacelift.process.ProcessInteractionBuilder;
import org.arquillian.spacelift.process.ProcessDetails;
import org.arquillian.spacelift.tool.Tool;

/**
 * Tool that is able to execute an external, operating system dependent command.
 *
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class CommandTool extends Tool<Object, ProcessDetails> {

    protected CommandBuilder commandBuilder;
    protected ProcessInteraction interaction;
    protected List<Integer> allowedExitCodes;
    protected ProcessReference processRef;

    public CommandTool() {
        this.interaction = ProcessInteractionBuilder.NO_INTERACTION;
        this.allowedExitCodes = new ArrayList<Integer>();
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
     *
     * @param code the exit code
     * @param others possible other codes
     * @return
     * @see ExecutionException
     */
    public CommandTool shouldExitWith(Integer code, Integer... others) {
        allowedExitCodes.add(code);
        if (others.length > 0) {
            allowedExitCodes.addAll(Arrays.asList(others));
        }
        return this;
    }

    @Override
    public Execution<ProcessDetails> execute() throws ExecutionException {
        // here we rewrap future based execution into process based execution to get better details about execution
        // and ability to terminate the process
        this.processRef = new ProcessReference(commandBuilder.build().getProgramName());
        Execution<ProcessDetails> processFutureExecution = super.execute();

        ProcessBasedExecution<ProcessDetails> execution = new ProcessBasedExecution<ProcessDetails>(processFutureExecution,
            processRef,
            commandBuilder.build().getProgramName(),
            allowedExitCodes);

        return execution;
    }

    @Override
    protected ProcessDetails process(Object input) throws Exception {

        Command command = commandBuilder.build();
        Process process = null;
        try {
            Execution<Process> spawnedProcess = Tasks.prepare(SpawnProcessTask.class)
                .redirectErrorStream(true)
                .shouldExitWith(allowedExitCodes)
                .command(command)
                .execute();

            // wait for process to finish
            process = spawnedProcess.await();

            // set processReference
            processRef.setProcess(process);

            // handle IO of spawned process
            Execution<ProcessDetails> processConsumer = Tasks.chain(spawnedProcess, ConsumeProcessOutputTask.class)
                .programName(command.getProgramName()).interaction(interaction).execute();

            // FIXME could this be moved to execution itself
            process.waitFor();
            // wait for process to finish IO
            ProcessDetails details = processConsumer.await();

            if (spawnedProcess.hasFailed()) {
                throw new ExecutionException("Invocation of \"{0}\" failed with {1}", new Object[] {
                    command,
                    details.getExitValue() });
            }

            return details;
        }
        // rewrap exception
        catch (InterruptedException e) {
            throw new ExecutionException(e.getCause() != null ? e.getCause() : e,
                "Executing \"{0}\": {1}",
                new Object[] {
                    e.getMessage(),
                    commandBuilder });
        } finally {
            // cleanup
            if (process != null) {
                InputStream in = process.getInputStream();
                InputStream err = process.getErrorStream();
                OutputStream out = process.getOutputStream();
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ignore) {
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException ignore) {
                    }
                }
                if (err != null) {
                    try {
                        err.close();
                    } catch (IOException ignore) {
                    }
                }
                // just in case, something went wrong
                process.destroy();
            }
        }
    }
}
