package org.arquillian.spacelift.process.impl;

import org.arquillian.spacelift.execution.Task;
import org.arquillian.spacelift.process.Command;

class SpawnProcessTask extends Task<Object, Process> {

    private Command command;
    private boolean redirectErrorStream;

    public SpawnProcessTask redirectErrorStream(boolean redirectErrorStream) {
        this.redirectErrorStream = redirectErrorStream;
        return this;
    }

    public SpawnProcessTask command(Command command) {
        this.command = command;
        return this;
    }

    @Override
    protected Process process(Object input) throws Exception {
        ProcessBuilder builder = new ProcessBuilder(command.getFullCommand());
        builder.directory(getExecutionService().getWorkingDirectory());
        builder.environment().putAll(getExecutionService().getEnvironment());
        builder.redirectErrorStream(redirectErrorStream);
        return builder.start();
    }

}
