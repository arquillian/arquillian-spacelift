package org.arquillian.spacelift.task;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.arquillian.spacelift.execution.CountDownWatch;
import org.arquillian.spacelift.execution.Execution;
import org.arquillian.spacelift.execution.ExecutionCondition;
import org.arquillian.spacelift.execution.ExecutionException;
import org.arquillian.spacelift.execution.ExecutionService;
import org.arquillian.spacelift.execution.TimeoutExecutionException;

class TestExecutionService implements ExecutionService {

    @Override
    public <T> Execution<T> execute(final Callable<T> task) throws ExecutionException {

        final Future<T> future = Executors.newSingleThreadExecutor().submit(task);
        return new TestExecution<T>(task, future);

    }

    @Override
    public <T> Execution<T> schedule(Callable<T> task, long delay, TimeUnit unit) throws TimeoutExecutionException,
        ExecutionException {

        final ScheduledFuture<T> future = Executors.newSingleThreadScheduledExecutor().schedule(task, delay, unit);
        return new TestExecution<T>(task, future);
    }

    public class TestExecution<RESULT> implements Execution<RESULT> {

        private final Callable<RESULT> currentTask;
        private final Future<RESULT> futureResult;

        private long pollInterval = 500;
        private TimeUnit pollUnit = TimeUnit.MILLISECONDS;

        public TestExecution(Callable<RESULT> task, Future<RESULT> futureResult) {
            this.currentTask = task;
            this.futureResult = futureResult;
        }

        @Override
        public Execution<RESULT> markAsFinished() {
            return null;
        }

        @Override
        public Execution<RESULT> registerShutdownHook() {
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
        public Execution<RESULT> terminate() throws ExecutionException {
            return null;
        }

        @Override
        public RESULT await() throws ExecutionException {
            try {
                return futureResult.get();
            } catch (InterruptedException e) {
                throw new ExecutionException(e);
            } catch (java.util.concurrent.ExecutionException e) {
                throw new ExecutionException(e);
            }
        }

        @Override
        public RESULT awaitAtMost(long timeout, TimeUnit unit) throws ExecutionException {
            try {
                return futureResult.get(timeout, unit);
            } catch (InterruptedException e) {
                throw new ExecutionException(e);
            } catch (java.util.concurrent.ExecutionException e) {
                throw new ExecutionException(e);
            } catch (TimeoutException e) {
                throw new ExecutionException(e);
            }
        }

        @Override
        public Execution<RESULT> reexecuteEvery(long interval, TimeUnit unit) {
            this.pollInterval = interval;
            this.pollUnit = unit;
            return this;
        }

        @Override
        public RESULT until(long timeout, TimeUnit unit, ExecutionCondition<RESULT> condition)
            throws ExecutionException, TimeoutExecutionException {

            long startTime = System.currentTimeMillis();
            long timeLeft = TimeUnit.MILLISECONDS.convert(timeout, unit);

            @SuppressWarnings({ "rawtypes", "unchecked" })
            Execution<RESULT> currentExecution = new TestExecution(currentTask, futureResult);

            while (timeLeft > TimeUnit.MILLISECONDS.convert(pollInterval, pollUnit)) {

                Execution<RESULT> nextExecution = TestExecutionService.this.schedule(currentTask, pollInterval, pollUnit);
                RESULT result = currentExecution.awaitAtMost(timeLeft, unit);

                if (condition.satisfiedBy(result)) {
                    nextExecution.terminate();
                    return result;
                }
                timeLeft -= (System.currentTimeMillis() - startTime);
                currentExecution = nextExecution;
            }

            throw new TimeoutExecutionException("Unable to trigger condition within {0} {1}.", timeout, unit.toString()
                .toLowerCase());
        }

        @Override
        public RESULT awaitAtMost(CountDownWatch timeout) throws ExecutionException, TimeoutExecutionException {
            return awaitAtMost(timeout.timeout(), timeout.getTimeUnit());
        }

        @Override
        public RESULT until(CountDownWatch timeout, ExecutionCondition<RESULT> condition) throws ExecutionException,
            TimeoutExecutionException {
            return until(timeout.timeout(), timeout.getTimeUnit(), condition);
        }
    };

}