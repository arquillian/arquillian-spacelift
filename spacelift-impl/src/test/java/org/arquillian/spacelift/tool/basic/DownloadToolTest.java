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

public class DownloadToolTest {

    static ToolRegistry registry;

    static ProcessExecutor executor;

    @BeforeClass
    public static void setup() {
        executor = new ProcessExecutorImpl();
        registry = new ToolRegistryImpl();
        registry.register(DownloadTool.class);

    }

    @Test
    public void downloadFile() throws ExecutionException, InterruptedException {

        Callable<File> futureFile = registry.find(DownloadTool.class)
            .from("http://www.arquillian.org")
            .to("target/index.html")
            .getCallable();

        File indexHtml = executor.submit(futureFile).get();

        Assert.assertThat(indexHtml, notNullValue());
    }

}
