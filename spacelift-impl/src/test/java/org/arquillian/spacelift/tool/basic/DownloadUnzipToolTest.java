package org.arquillian.spacelift.tool.basic;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.arquillian.spacelift.process.ProcessExecutor;
import org.arquillian.spacelift.process.impl.ProcessExecutorImpl;
import org.arquillian.spacelift.tool.ToolRegistry;
import org.arquillian.spacelift.tool.impl.ToolRegistryImpl;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;

public class DownloadUnzipToolTest {

    static ToolRegistry registry;

    static ProcessExecutor executor;

    @BeforeClass
    public static void setup() {
        executor = new ProcessExecutorImpl();
        registry = new ToolRegistryImpl();
        registry.register(DownloadTool.class);
        registry.register(UnzipTool.class);

    }

    @Test
    public void downloadFile() throws ExecutionException, InterruptedException {

        Callable<File> futureFile = registry.find(DownloadTool.class)
            .from("http://search.maven.org/remotecontent?filepath=net/minidev/json-smart/1.2/json-smart-1.2.jar")
            .to("target/json-smart-1.2.jar")
            .getCallable();

        File jsonSmart = executor.submit(futureFile).get();

        Assert.assertThat(jsonSmart, notNullValue());

        Callable<File> futureDir = registry.find(UnzipTool.class)
            .from(jsonSmart).to("target/json-smart-extracted").getCallable();

        File dir = executor.submit(futureDir).get();

        Assert.assertThat(dir, notNullValue());
    }

}
