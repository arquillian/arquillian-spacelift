package org.arquillian.spacelift.execution;

import java.util.concurrent.TimeUnit;

public interface Execution<RESULT> {

    /**
     * Marks execution as finished. This means that we've discovered that it did the work we expected.
     *
     * @return
     */
    Execution<RESULT> markAsFinished();

    /**
     * Registers a shutdown hook that is applied in case program is still running when JVM is going to terminate
     */
    Execution<RESULT> registerShutdownHook();

    /**
     * Checks whether process is marked as finished
     *
     * @return
     */
    boolean isMarkedAsFinished();

    /**
     * Checks whether process has finished
     *
     * @return
     */
    boolean isFinished();

    /**
     * Checks whether execution failed
     *
     * @return
     */
    boolean hasFailed();

    /**
     * Forcefully terminates execution
     */
    Execution<RESULT> terminate() throws ExecutionException;

    RESULT await() throws ExecutionException;

    RESULT awaitAtMost(long timeout, TimeUnit unit) throws ExecutionException, TimeoutExecutionException;

    Execution<RESULT> pollEvery(long step, TimeUnit unit);

    RESULT until(long timeout, TimeUnit unit, ExecutionCondition<RESULT> condition) throws ExecutionException,
        TimeoutExecutionException;
}