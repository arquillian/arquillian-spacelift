package org.arquillian.spacelift.execution;

import java.util.concurrent.Callable;

/**
 * Representation of a task that can be executed by Arquillian Spacelift.
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 * @param <IN> Input type of this task. Can be {@code Object} to mark that input in not relevant for this task.
 * @param <OUT> Output type of this task.
 */
public abstract class Task<IN, OUT> {

    // task is protected so we can set it
    private Task<?, ? extends IN> previous;
    private ExecutionService executionService;

    /**
     * Allows to connect current task with next task, given the output of this task matches input of next task
     *
     * @param nextTask Task to be executed right after this task is finished
     * @return
     */
    public <OUT_2, TASK_2 extends Task<? super OUT, OUT_2>> TASK_2 then(Class<TASK_2> nextTask) {

        TASK_2 next = Tasks.prepare(nextTask);
        next.setPreviousTask(this);

        return next;
    }

    /**
     * Asynchronously executes current chain of tasks.
     *
     * @return Execution object that allows later retrieved result of the task
     * @throws ExecutionException
     * @throws InvalidSetupException
     */
    public Execution<OUT> execute() throws ExecutionException, InvalidSetupException {

        if (getExecutionService() == null) {
            throw new ExecutionException("Unable to execute a task, execution service was not set.");
        }

        try {
            Task.this.validate();
        } catch (InvalidSetupException ex) {
            throw new InvalidSetupException(
                String.format("Task %s is not set up properly, it failed to pass the validation process.",
                    Task.this.getClass().getName()));
        }

        return getExecutionService().execute(new Callable<OUT>() {
            @Override
            public OUT call() throws Exception {
                return Task.this.run();
            }
        });
    }

    /**
     * Represents a transformation of {@code input} into {@code output}.
     *
     * @param input Input of this task, can be ignored
     * @return
     * @throws Exception if processing fails for any reason
     */
    protected abstract OUT process(IN input) throws Exception;

    /**
     * Validates an environment of a task before it is executed.
     * 
     * @throws InvalidSetupException in case task is badly set up
     */
    protected abstract void validate() throws InvalidSetupException;

    /**
     * Transforms a chain of tasks into action that will be executed asynchronously.
     *
     * @return
     * @throws ExecutionException
     */
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

    /**
     * Returns {@see ExecutionService}. If using {@see Tasks} or {@see ToolRegistry}, this method is guaranteed to never return
     * {@code null}
     *
     * @return
     */
    protected ExecutionService getExecutionService() {
        return executionService;
    }

    /**
     * Sets previous task
     *
     * @param previous
     */
    protected void setPreviousTask(Task<?, ? extends IN> previous) {
        this.previous = previous;
    }

    /**
     * Sets {@see ExecutionService} to be used to execute this task asynchronously
     *
     * @param executionService
     * @return
     */
    protected Task<IN, OUT> setExecutionService(ExecutionService executionService) {
        this.executionService = executionService;
        return this;
    }

}
