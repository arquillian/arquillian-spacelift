package org.arquillian.spacelift.tool.impl;

import java.util.List;

import org.arquillian.spacelift.execution.Execution;
import org.arquillian.spacelift.execution.ExecutionException;
import org.arquillian.spacelift.execution.ExecutionService;
import org.arquillian.spacelift.execution.Task;
import org.arquillian.spacelift.execution.impl.DefaultExecutionServiceFactory;
import org.arquillian.spacelift.process.Command;
import org.arquillian.spacelift.process.CommandBuilder;
import org.arquillian.spacelift.process.ProcessDetails;
import org.arquillian.spacelift.tool.ExternalTool;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;

public abstract class ExternalToolTask<TOOLTYPE extends ExternalTool<TOOLTYPE>> implements
    ExternalTool<TOOLTYPE> {

    @Inject
    Instance<ExecutionService> executionServiceInstance;

    protected CommandBuilder builder;

    public ExternalToolTask() {
        this.builder = new CommandBuilder(getProgramName());
    }

    @Override
    public TOOLTYPE parameters(List<? extends CharSequence> parameters) {
        builder.parameters(parameters);
        return currentTypeInstance();
    }

    @Override
    public TOOLTYPE parameters(CharSequence... parameters) {
        builder.parameters(parameters);
        return currentTypeInstance();
    }

    @Override
    public TOOLTYPE parameter(CharSequence parameter) {
        builder.parameter(parameter);
        return currentTypeInstance();
    }

    @Override
    public TOOLTYPE splitToParameters(CharSequence sequenceToBeParsed) {
        builder.splitToParameters(sequenceToBeParsed);
        return currentTypeInstance();
    }

    @Override
    public <RETURNTYPE_B> Task<RETURNTYPE_B> then(Task<RETURNTYPE_B> nextTask) {

        return new Task<RETURNTYPE_B>() {
            @Override
            public <RETURNTYPE_C> Task<RETURNTYPE_C> then(Task<RETURNTYPE_C> nextTask) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public RETURNTYPE_B workload() throws ExecutionException {

                getExecutionService().execute(ExternalToolTask.this);
                // TODO Auto-generated method stub
                return null;
            }
        };
    }

    @Override
    public Execution<ProcessDetails> execute() throws ExecutionException {
        return getExecutionService().execute(this);
    }

    @Override
    public Command getCommand() {
        return builder.build();
    }

    /**
     * Returns program name based on current system. This is used to initialize {@link CommandBuilder}
     *
     * @return
     */
    protected abstract String getProgramName();

    protected ExecutionService getExecutionService() {
        // Arquillian injection might not be activated if not running via Arquillian
        if (executionServiceInstance != null && executionServiceInstance.get() != null) {
            return executionServiceInstance.get();
        }

        return new DefaultExecutionServiceFactory().getExecutionServiceInstance();
    }

    @SuppressWarnings("unchecked")
    protected TOOLTYPE currentTypeInstance() {
        return (TOOLTYPE) this;
    }

}
