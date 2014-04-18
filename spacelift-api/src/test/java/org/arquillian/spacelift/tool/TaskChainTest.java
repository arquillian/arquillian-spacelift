package org.arquillian.spacelift.tool;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Random;
import java.util.Scanner;

import org.arquillian.spacelift.execution.ExecutionService;
import org.arquillian.spacelift.execution.ExecutionServiceFactory;
import org.arquillian.spacelift.execution.Task;
import org.arquillian.spacelift.execution.Tasks;
import org.junit.BeforeClass;
import org.junit.Test;

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
            .execute().waitFor();

        assertThat(result, notNullValue());
    }
}
