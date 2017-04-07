package org.arquillian.spacelift.task;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.execution.ExecutionCondition;
import org.arquillian.spacelift.execution.ExecutionException;
import org.arquillian.spacelift.execution.TimeoutExecutionException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class TaskChainTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void chainTools() {
        String result = Spacelift.task(CreateWriterTask.class)
            .then(DataSampler.class)
            .generateRandomData(123)
            .then(MyStringReader.class)
            .execute().await();

        assertThat(result, notNullValue());
    }

    @Test
    public void scheduleTools() {

        Spacelift.task(CreateWriterTask.class)
            .then(DataSampler.class)
            .generateRandomData(123)
            .then(MyStringReader.class)
            .then(ExecutionCounter.class)
            .execute().until(3, TimeUnit.SECONDS, new ExecutionCondition<Integer>() {
            @Override
            public boolean satisfiedBy(Integer object) throws ExecutionException {
                return object >= 3;
            }
        });

        // restart counter
        Spacelift.task(ExecutionCounter.class).restart();

        exception.expect(TimeoutExecutionException.class);

        Spacelift.task(CreateWriterTask.class)
            .then(DataSampler.class)
            .generateRandomData(123)
            .then(MyStringReader.class)
            .then(ExecutionCounter.class)
            .execute()
            .reexecuteEvery(1, TimeUnit.SECONDS).until(3, TimeUnit.SECONDS, new ExecutionCondition<Integer>() {
            @Override
            public boolean satisfiedBy(Integer object) throws ExecutionException {
                return object >= 5;
            }
        });
    }

    @Test
    public void injectFirstParam() {

        StringWriter preparedFile = Spacelift.task(CreateWriterTask.class)
            .execute().await();

        String result = Spacelift.task(preparedFile, DataSampler.class)
            .generateRandomData(123)
            .then(MyStringReader.class)
            .execute()
            .await();

        assertThat(result, notNullValue());
    }

    public static class CreateWriterTask extends Task<Object, StringWriter> {

        @Override
        protected StringWriter process(Object input) throws Exception {
            return new StringWriter();
        }
    }

    public static class DataSampler extends Task<StringWriter, StringReader> {

        private StringBuilder randomData = new StringBuilder();

        public DataSampler generateRandomData(int len) {
            for (int i = len; i > 0; i--) {
                randomData.append(Math.abs(new Random().nextInt() % 10));
            }
            return this;
        }

        @Override
        protected StringReader process(StringWriter input) throws Exception {

            input.append(randomData);
            input.close();

            return new StringReader(input.toString());
        }
    }

    public static class MyStringReader extends Task<StringReader, String> {
        @Override
        protected String process(StringReader input) throws Exception {
            return input.toString();
        }
    }

    public static class ExecutionCounter extends Task<Object, Integer> {

        static AtomicInteger count = new AtomicInteger(0);

        public ExecutionCounter restart() {
            count = new AtomicInteger(0);
            return this;
        }

        @Override
        protected Integer process(Object input) throws Exception {
            // System.out.println("Executing chain no. " + count.incrementAndGet());
            // return count.get();
            return count.incrementAndGet();
        }
    }
}
