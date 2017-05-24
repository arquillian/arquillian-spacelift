package org.arquillian.spacelift.task.execution;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.execution.ExecutionCondition;
import org.arquillian.spacelift.execution.ExecutionException;
import org.arquillian.spacelift.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author <a href="mailto:Stefan.Miklosovic@sk.ibm.com">Stefan Miklosovic</a>
 */
@RunWith(JUnit4.class)
public class ExecutionCountTest {

    @Test
    public void testExecutionCount() {

        final List<Long> executionTimes = Spacelift.task(ExecutionCountTask.class)
            .execute()
            .reexecuteEvery(1, SECONDS)
            .until(15, SECONDS, new ExecutionCondition<List<Long>>() {

                @Override
                public boolean satisfiedBy(List<Long> executionTimes) throws ExecutionException {
                    return !executionTimes.isEmpty();
                }
            });

        assertFalse(executionTimes.isEmpty());
        assertEquals(10, executionTimes.size());

        for (int i = 0; i < 9; i++) {
            assertTrue(executionTimes.get(i + 1) - executionTimes.get(i) >= 1000);
        }
    }

    private static class ExecutionCountTask extends Task<Object, List<Long>> {

        private int counter = 0;

        private List<Long> executionTimes = new ArrayList<Long>();

        public List<Long> getExecutionTimes() {
            return executionTimes;
        }

        @Override
        protected List<Long> process(Object arg0) throws Exception {
            counter += 1;

            executionTimes.add(System.currentTimeMillis());

            if (counter == 10) {
                return executionTimes;
            }

            return new ArrayList<Long>();
        }
    }
}
