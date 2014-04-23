package org.arquillian.spacelift.tool;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.arquillian.spacelift.execution.ExecutionCondition;
import org.arquillian.spacelift.execution.ExecutionException;
import org.arquillian.spacelift.execution.ExecutionService;
import org.arquillian.spacelift.execution.ExecutionServiceFactory;
import org.arquillian.spacelift.execution.Task;
import org.arquillian.spacelift.execution.Tasks;
import org.arquillian.spacelift.execution.TimeoutExecutionException;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertThat;

import static org.hamcrest.CoreMatchers.notNullValue;

public class TaskChainTest {

    public static class CreateFileTask extends Task<Object, File> {

        private String name;

        public CreateFileTask named(String name) {
            this.name = name;
            return this;
        }

        @Override
        protected File process(Object input) throws Exception {
            File file = new File(name);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            return file;
        }
    }

    public static class DataSampler extends Task<File, File> {

        private StringBuilder randomData = new StringBuilder();

        public DataSampler generateRandomData(int len) {
            for (int i = len; i > 0; i--) {
                randomData.append(Math.abs(new Random().nextInt() % 10));
            }
            return this;
        }

        @Override
        protected File process(File input) throws Exception {

            Writer w = new FileWriter(input);
            w.append(randomData);
            w.close();

            return input;
        }
    }

    public static class FileReader extends Task<File, String> {
        @Override
        protected String process(File input) throws Exception {
            return new Scanner(input).useDelimiter("\\Z").next();
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

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void setup() {
        Tasks.setDefaultExecutionServiceFactory(new ExecutionServiceFactory() {
            @Override
            public ExecutionService getExecutionServiceInstance() {
                return new TestExecutionService();
            }
        });
    }

    @Test
    public void chainTools() {
        String result = Tasks.prepare(CreateFileTask.class)
            .named("target/foobar")
            .then(DataSampler.class)
            .generateRandomData(123)
            .then(FileReader.class)
            .execute().await();

        assertThat(result, notNullValue());
    }

    @Test
    public void scheduleTools() {

        Tasks.prepare(CreateFileTask.class)
            .named("target/foobar")
            .then(DataSampler.class)
            .generateRandomData(123)
            .then(FileReader.class)
            .then(ExecutionCounter.class)
            .execute().until(3, TimeUnit.SECONDS, new ExecutionCondition<Integer>() {
                @Override
                public boolean satisfiedBy(Integer object) throws ExecutionException {
                    return object >= 3;
                }
            });

        // restart counter
        Tasks.prepare(ExecutionCounter.class).restart();

        exception.expect(TimeoutExecutionException.class);

        Tasks.prepare(CreateFileTask.class)
            .named("target/foobar")
            .then(DataSampler.class)
            .generateRandomData(123)
            .then(FileReader.class)
            .then(ExecutionCounter.class)
            .execute()
            .pollEvery(1, TimeUnit.SECONDS).until(3, TimeUnit.SECONDS, new ExecutionCondition<Integer>() {
                @Override
                public boolean satisfiedBy(Integer object) throws ExecutionException {
                    return object >= 5;
                }
            });
    }

    @Test
    public void injectFirstParam() {

        File preparedFile = Tasks.prepare(CreateFileTask.class)
            .named("target/foobar").execute().await();

        String result = Tasks.chain(preparedFile, DataSampler.class)
            .generateRandomData(123)
            .then(FileReader.class)
            .execute()
            .await();

        assertThat(result, notNullValue());
    }
}
