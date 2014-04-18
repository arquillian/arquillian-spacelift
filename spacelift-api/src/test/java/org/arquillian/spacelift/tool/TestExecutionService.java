package org.arquillian.spacelift.tool;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.arquillian.spacelift.execution.Execution;
import org.arquillian.spacelift.execution.ExecutionCondition;
import org.arquillian.spacelift.execution.ExecutionException;
import org.arquillian.spacelift.execution.ExecutionService;
import org.arquillian.spacelift.execution.TimeoutExecutionException;

class TestExecutionService implements ExecutionService {

    @Override
    public ExecutionService setEnvironment(Map<String, String> environment) throws IllegalStateException {
        return null;
    }

    @Override
    public Map<String, String> getEnvironment() {
        return null;
    }

    @Override
    public ExecutionService setWorkingDirectory(String workingDirectory) throws IllegalArgumentException {
        return null;
    }

    @Override
    public ExecutionService setWorkingDirectory(File workingDirectory) throws IllegalArgumentException {
        return null;
    }

    @Override
    public File getWorkingDirectory() {
        return null;
    }

    @Override
    public <T> Execution<T> execute(Callable<T> task) throws ExecutionException {

        final Future<T> future = Executors.newSingleThreadExecutor().submit(task);
        return new Execution<T>() {

            @Override
            public Execution<T> markAsFinished() {
                return null;
            }

            @Override
            public Execution<T> registerShutdownHook() {
                return null;
            }

            @Override
            public boolean isMarkedAsFinished() {
                return false;
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean hasFailed() {
                return false;
            }

            @Override
            public Execution<T> terminate() throws ExecutionException {
                return null;
            }

            @Override
            public T waitFor() throws ExecutionException {
                try {
                    return future.get();
                } catch (InterruptedException e) {
                    throw new ExecutionException(e);
                } catch (java.util.concurrent.ExecutionException e) {
                    throw new ExecutionException(e);
                }
            }

            @Override
            public T waitFor(long timeout, TimeUnit unit) throws ExecutionException {
                try {
                    return future.get();
                } catch (InterruptedException e) {
                    throw new ExecutionException(e);
                } catch (java.util.concurrent.ExecutionException e) {
                    throw new ExecutionException(e);
                }
            }
        };
    }

    @Override
    public <T> T repeat(Callable<T> task, ExecutionCondition<Future<T>> breakCondition, long step, long timeout,
        TimeUnit unit) throws TimeoutExecutionException, ExecutionException {
        return null;
    }

}