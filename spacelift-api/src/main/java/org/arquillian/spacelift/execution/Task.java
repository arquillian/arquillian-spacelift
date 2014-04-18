package org.arquillian.spacelift.execution;

import java.util.concurrent.Callable;

/**
 * Representation of a task that can be executed by Arquillian Spacelift.
 *
 * @param <IN> Input type of the task. It can be {@code Object} to
 */
public abstract class Task<IN, OUT> {

    private Task<?, ? extends IN> previous;
    private ExecutionService executionService;

    /**
     *
     */
    public <OUT_2, TASK_2 extends Task<? super OUT, OUT_2>> TASK_2 then(Class<TASK_2> nextTask) {

        TASK_2 next = Tasks.prepare(nextTask);
        next.previous = this;

        return next;
    }

    public Execution<OUT> execute() throws ExecutionException {

        if (getExecutionService() == null) {
            throw new ExecutionException("Unable to execute a task, execution service was not set.");
        }

        return getExecutionService().execute(new Callable<OUT>() {
            @Override
            public OUT call() throws Exception {
                return Task.this.run();
            }
        });
    }

    protected abstract OUT process(IN input) throws Exception;

    protected OUT run() throws ExecutionException {
        IN in = null;

        if (previous != null) {
            in = previous.run();
        }

        try {
            return process(in);
        } catch (Exception e) {
            throw new ExecutionException(e, "Unable to execute task {0}", this.getClass().getSimpleName());
        }
    }

    protected ExecutionService getExecutionService() {
        return executionService;
    }

    Task<IN, OUT> setExecutionService(ExecutionService executionService) {
        this.executionService = executionService;
        return this;
    }

}
