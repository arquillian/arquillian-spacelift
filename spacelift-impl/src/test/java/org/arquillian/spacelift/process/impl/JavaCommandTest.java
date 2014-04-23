package org.arquillian.spacelift.process.impl;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.SystemUtils;
import org.arquillian.spacelift.execution.ExecutionInteractionBuilder;
import org.arquillian.spacelift.execution.Tasks;
import org.arquillian.spacelift.execution.impl.DefaultExecutionServiceFactory;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.is;

public class JavaCommandTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void setup() {
        Tasks.setDefaultExecutionServiceFactory(new DefaultExecutionServiceFactory());
    }

    @Test
    public void outputDefaultPrefix() throws UnsupportedEncodingException {

        // run only on linux
        Assume.assumeThat(SystemUtils.IS_OS_LINUX, is(true));

        Tasks.prepare(CommandTool.class).programName("java").parameters("-help")
            .interaction(new ExecutionInteractionBuilder().outputs(".*"))
            .execute().await();
    }

}
