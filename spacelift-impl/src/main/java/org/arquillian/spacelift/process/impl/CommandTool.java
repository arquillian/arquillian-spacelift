package org.arquillian.spacelift.process.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.arquillian.spacelift.execution.Execution;
import org.arquillian.spacelift.execution.ExecutionException;
import org.arquillian.spacelift.execution.ExecutionInteraction;
import org.arquillian.spacelift.execution.ExecutionInteractionBuilder;
import org.arquillian.spacelift.execution.Tasks;
import org.arquillian.spacelift.process.Command;
import org.arquillian.spacelift.process.CommandBuilder;
import org.arquillian.spacelift.process.ProcessDetails;
import org.arquillian.spacelift.tool.Tool;

public class CommandTool extends Tool<Object, ProcessDetails> {

    protected CommandBuilder command;
    protected ExecutionInteraction interaction;

    public CommandTool() {
        this.interaction = ExecutionInteractionBuilder.NO_INTERACTION;
    }

    @Override
    protected Collection<String> aliases() {
        return Arrays.asList("run");
    }

    public CommandTool programName(CharSequence programName) {
        this.command = new CommandBuilder(programName);
        return this;
    }

    public CommandTool parameters(List<? extends CharSequence> parameters) {
        command.parameters(parameters);
        return this;
    }

    public CommandTool parameters(CharSequence... parameters) {
        command.parameters(parameters);
        return this;
    }

    public CommandTool parameter(CharSequence parameter) {
        command.parameter(parameter);
        return this;
    }

    public CommandTool splitToParameters(CharSequence sequenceToBeParsed) {
        command.parameters(sequenceToBeParsed);
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
        this.command = new CommandBuilder(command.getFullCommand().toArray(new String[0]));
        return this;
    }

    public CommandTool command(CommandBuilder commandBuilder) {
        this.command = commandBuilder;
        return this;
    }

    @Override
    protected ProcessDetails process(Object input) throws Exception {

        Process process = null;
        try {
            process = Tasks.prepare(SpawnProcessTask.class)
                .redirectErrorStream(true)
                .command(command.build())
                .execute()
                .await();

            // handle IO
            Execution<ProcessDetails> processDetails = Tasks.chain(process, ConsumeProcessOutputTask.class)
                .execute();

            // wait for process to finish
            process.waitFor();
            // wait for process to finish IO
            ProcessDetails details = processDetails.await();
            if (processDetails.hasFailed()) {
                throw new ExecutionException("Invocation of \"{0}\" failed with {1}", new Object[] { command /*
                                                                                                              * FIXME missing
                                                                                                              * exit code
                                                                                                              */});
            }
            return details;
        }
        // rewrap exception
        catch (InterruptedException e) {
            throw new ExecutionException(e.getCause() != null ? e.getCause() : e,
                "Executing \"{0}\": {1}",
                new Object[] {
                    e.getMessage(),
                    command });
        } catch (ExecutionException e) {
            throw new ExecutionException(e.getCause() != null ? e.getCause() : e,
                "Executing \"{0}\": {1}",
                new Object[] {
                    e.getMessage(),
                    command });
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
