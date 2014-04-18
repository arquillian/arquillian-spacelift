package org.arquillian.spacelift.tool.impl;

import org.arquillian.spacelift.execution.Execution;
import org.arquillian.spacelift.execution.ExecutionException;
import org.arquillian.spacelift.execution.ExecutionService;
import org.arquillian.spacelift.execution.Task;
import org.arquillian.spacelift.execution.impl.DefaultExecutionServiceFactory;
import org.arquillian.spacelift.tool.InternalTool;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;

public abstract class InternalToolTask<TOOLTYPE extends InternalTool<TOOLTYPE, RETURNTYPE>, RETURNTYPE> implements
    InternalTool<TOOLTYPE, RETURNTYPE> {

    @Inject
    Instance<ExecutionService> executionServiceInstance;

    @Override
    public Execution<RETURNTYPE> execute() throws ExecutionException {
        return getExecutionServiceSafely().execute(this);
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
                getExecutionServiceSafely().execute(InternalToolTask.this);
                // TODO Auto-generated method stub
                return null;
            }
        };
    }

    protected ExecutionService getExecutionServiceSafely() {
        // Arquillian injection might not be activated if not running via Arquillian
        if (executionServiceInstance != null && executionServiceInstance.get() != null) {
            return executionServiceInstance.get();
        }

        return new DefaultExecutionServiceFactory().getExecutionServiceInstance();
    }

}
