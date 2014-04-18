package org.arquillian.spacelift.process.impl;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.arquillian.spacelift.execution.Execution;
import org.arquillian.spacelift.execution.ExecutionException;
import org.arquillian.spacelift.execution.ExecutionInteraction;
import org.arquillian.spacelift.execution.ExecutionInteractionBuilder;
import org.arquillian.spacelift.execution.ExecutionTask;
import org.arquillian.spacelift.execution.impl.ExecutionImpl;

public class SpawnedProcessTask implements ExecutionTask<Process> {

    private final List<String> command;
    private final File workingDirectory;
    private boolean redirectErrorStream;
    private final Map<String, String> env;
    private final ExecutionInteraction interaction;

    public SpawnedProcessTask(Map<String, String> env, File workingDirectory, boolean redirectErrorStream, List<String> command)
    {
        this.env = env;
        this.workingDirectory = workingDirectory;
        this.redirectErrorStream = redirectErrorStream;
        this.command = command;
        this.interaction = ExecutionInteractionBuilder.NO_INTERACTION;

    }

    @Override
    public Execution<Process> execute() throws ExecutionException {

        return new ExecutionImpl<Process>(future)

        return new Callable<Process>() {
            @Override
            public Process call() throws Exception {
                ProcessBuilder builder = new ProcessBuilder(command);
                builder.directory(workingDirectory);
                builder.environment().putAll(env);
                builder.redirectErrorStream(redirectErrorStream);
                return builder.start();
            }
        };
    }

    @Override
    public ExecutionInteraction interaction() {
        return interaction;
    }

}
