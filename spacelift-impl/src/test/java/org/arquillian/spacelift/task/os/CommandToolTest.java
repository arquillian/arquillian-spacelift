package org.arquillian.spacelift.task.os;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.SystemUtils;
import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.execution.Execution;
import org.arquillian.spacelift.execution.ExecutionException;
import org.arquillian.spacelift.process.CommandBuilder;
import org.arquillian.spacelift.process.ProcessResult;
import org.arquillian.spacelift.task.os.CommandTool;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class CommandToolTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void runJavaCommandHelp() {

        // run only on linux
        Assume.assumeThat(SystemUtils.IS_OS_LINUX, is(true));

        Spacelift.task(CommandTool.class).programName("java").parameters("-help")
            .execute().await();
    }

    @Test
    public void runJavaCommandAfterRegistration() {

        // run only on linux
        Assume.assumeThat(SystemUtils.IS_OS_LINUX, is(true));

        Spacelift.registry().register(CommandTool.class);

        // previous call caused this task to register
        // NOTE, this is not really useful for Java, but rather for dynamic languages
        ((CommandTool) Spacelift.task("CommandTool")).programName("java").parameters("-help")
        .execute().await();
    }



    @Test
    public void runJavaCommandInvalid() {

        // run only on linux
        Assume.assumeThat(SystemUtils.IS_OS_LINUX, is(true));

        exception.expect(ExecutionException.class);

        Spacelift.task(CommandTool.class).programName("java").parameters("-foo", "-bar")
            .execute().await();
    }

    @Test
    public void runJavaCommandInvalidExpectFailure() throws UnsupportedEncodingException {

        // run only on linux
        Assume.assumeThat(SystemUtils.IS_OS_LINUX, is(true));

        Spacelift.task(CommandTool.class).programName("java").parameters("-foo", "-bar")
            .shouldExitWith(1)
            .execute();
    }

    @Test
    public void splitToParameters() throws Exception{
        CommandBuilder cb = Spacelift.task(CommandTool.class).programName("java").splitToParameters("-foo -bar").commandBuilder;
        Assert.assertThat(cb.build().getNumberOfParameters(), is(2));
    }

    @Test
    public void spawningProcess() throws Exception {

        // run only on linux
        Assume.assumeThat(SystemUtils.IS_OS_LINUX, is(true));

        Execution<ProcessResult> yes = Spacelift.task(CommandTool.class).programName("yes").parameters("spacelift")
            .shouldExitWith(143)
            .execute();

        // wait until process has started
        Thread.sleep(500);

        Assert.assertThat(yes.isFinished(), is(false));

        yes.terminate();
        Assert.assertThat(yes.isFinished(), is(true));

        ProcessResult result = yes.await();
        if (result != null) {
            Assert.assertThat(result.output().size(), is(not(0)));
        }
    }

    @Test
    public void workingDir() throws Exception {
        Spacelift.task(CommandTool.class).programName("yes");
    }
}