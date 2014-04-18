package org.arquillian.spacelift.process.impl;

import org.arquillian.spacelift.execution.Execution;
import org.arquillian.spacelift.execution.ExecutionException;
import org.arquillian.spacelift.execution.ExecutionInteraction;
import org.arquillian.spacelift.execution.ExecutionService;
import org.arquillian.spacelift.execution.Task;
import org.arquillian.spacelift.process.Command;
import org.arquillian.spacelift.process.ProcessDetails;

public class CommandExecutionTask implements Task<ProcessDetails> {

    private final Command command;
    private final ExecutionInteraction interaction;
    private ExecutionService service;

    public CommandExecutionTask(Command command, ExecutionInteraction interaction) {
        this.command = command;
        this.interaction = interaction;
    }

    @Override
    public Execution<ProcessDetails> execute() throws ExecutionException {
        if (service == null) {
            throw new ExecutionException("Unable to execute command {0}, execution service was not available", command);
        }

        Process process = service.execute(new SpawnedProcessTask(service.getEnvironment(),
            service.getWorkingDirectory(),
            true, command.getFullCommand()));

        Execution<ProcessDetails> execution = service.executeAsync(new ConsumeProcessOutputTask(process, command, interaction));

        return execution;
    }

    public CommandExecutionTask setExecutionService(ExecutionService service) {
        this.service = service;
        return this;
    }

}
