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
import org.arquillian.spacelift.process.ExecutionInteraction;
import org.arquillian.spacelift.process.ExecutionInteractionBuilder;
import org.arquillian.spacelift.process.ProcessDetails;
import org.arquillian.spacelift.tool.Tool;

public class CommandTool extends Tool<Object, ProcessDetails> {

    protected CommandBuilder commandBuilder;
    protected ExecutionInteraction interaction;
    protected List<Integer> allowedExitCodes;
    protected ProcessReference processRef;

    public CommandTool() {
        this.interaction = ExecutionInteractionBuilder.NO_INTERACTION;
        this.allowedExitCodes = new ArrayList<Integer>();
    }

    @Override
    protected Collection<String> aliases() {
        return Arrays.asList("run");
    }

    public CommandTool programName(CharSequence programName) {
        this.commandBuilder = new CommandBuilder(programName);
        return this;
    }

    public CommandTool parameters(List<? extends CharSequence> parameters) {
        commandBuilder.parameters(parameters);
        return this;
    }

    public CommandTool parameters(CharSequence... parameters) {
        commandBuilder.parameters(parameters);
        return this;
    }

    public CommandTool parameter(CharSequence parameter) {
        commandBuilder.parameter(parameter);
        return this;
    }

    public CommandTool splitToParameters(CharSequence sequenceToBeParsed) {
        commandBuilder.parameters(sequenceToBeParsed);
        return this;
    }

    public CommandTool interaction(ExecutionInteraction interaction) {
        this.interaction = interaction;
        return this;
    }

    public CommandTool interaction(ExecutionInteractionBuilder interactionBuilder) {
        this.interaction = interactionBuilder.build();
        return this;
    }

    public CommandTool command(Command command) {
        this.commandBuilder = new CommandBuilder(command.getFullCommand().toArray(new String[0]));
        return this;
    }

    public CommandTool command(CommandBuilder commandBuilder) {
        this.commandBuilder = commandBuilder;
        return this;
    }

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
