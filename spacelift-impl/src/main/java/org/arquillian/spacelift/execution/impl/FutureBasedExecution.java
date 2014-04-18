package org.arquillian.spacelift.execution.impl;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.arquillian.spacelift.execution.Execution;
import org.arquillian.spacelift.execution.ExecutionException;

public class FutureBasedExecution<X> implements Execution<X> {

    private Future<X> executionFuture;

    private boolean shouldBeFinished;

    public FutureBasedExecution(Future<X> future) {
        this.executionFuture = future;
    }

    @Override
    public Execution<X> markAsFinished() {
        this.shouldBeFinished = true;
        return this;
    }

    @Override
    public Execution<X> registerShutdownHook() {
        ShutdownHooks.addHookFor(this);
        return this;
    }

    @Override
    public boolean isMarkedAsFinished() {
        return shouldBeFinished;
    }

    @Override
    public boolean isFinished() {
        return isMarkedAsFinished() || executionFuture.isDone();
    }

    @Override
    public boolean hasFailed() {
        return executionFuture.isCancelled();
    }

    @Override
    public Execution<X> terminate() {

        executionFuture.cancel(true);
        return this;

        // FIXME what to do here!
        // process.destroy();
        // try {
        // process.waitFor();
        // } catch (InterruptedException e) {
        // throw new ExecutionException(e, "Interrupted while waiting for {0} to be terminated", processName);
        // }
    }

    @Override
    public X waitFor() throws ExecutionException {
        try {
            return executionFuture.get();
        } catch (InterruptedException e) {
            throw new ExecutionException(e, "Interrupted while executing a task");
        } catch (java.util.concurrent.ExecutionException e) {
            throw new ExecutionException(e, "Interrupted while executing a task");
        }
    }

    @Override
    public X waitFor(long timeout, TimeUnit unit) {
        try {
            return executionFuture.get(timeout, unit);
        } catch (InterruptedException e) {
            throw new ExecutionException(e, "Interrupted while executing a task");
        } catch (java.util.concurrent.ExecutionException e) {
            throw new ExecutionException(e, "Execution of a task failed");
        } catch (TimeoutException e) {
            throw new ExecutionException(e, "Timed out after {0}{1} while executing a task", timeout, unit);
        }
    }
}
