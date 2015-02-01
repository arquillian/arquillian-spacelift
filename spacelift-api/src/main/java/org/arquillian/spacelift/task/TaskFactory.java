package org.arquillian.spacelift.task;

import java.util.Collection;

import org.arquillian.spacelift.Spacelift;

/**
 * A factory that is able to create tasks instances
 *
 * @author kpiwko
 *
 * @param <IN> Input of the task
 * @param <OUT> Output of the task
 * @param <TASK> {@link Task} that is created by this factory
 */
public interface TaskFactory<IN, OUT, TASK extends Task<? super IN, OUT>> {

    /**
     * Creates an instance of the task
     *
     * @return Task instance
     */
    TASK create();

    /**
     * Returns a collection of aliases for this task factory. An alias can be used to get a task
     * from the {@link TaskRegistry}
     *
     * @return
     */
    Collection<String> aliases();

    public static class ExecutionServiceInjector {
        <IN, OUT, TASK extends Task<? super IN, OUT>> TASK inject(TASK task) {
            task.setExecutionService(Spacelift.service());
            return task;
        }
    }

}
