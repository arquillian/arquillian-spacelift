/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.arquillian.spacelift.task.os;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.SystemUtils;
import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.execution.Execution;
import org.arquillian.spacelift.execution.impl.ShutdownHooks;
import org.arquillian.spacelift.process.ProcessResult;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

/**
 * Test shutdown hook activation for command
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 */
public class DaemonHookTest {

    private static void resetShutdownCounter() throws Exception {
        Field counter = ShutdownHooks.SpaceliftShutDownHook.class.getDeclaredField("shutdownCounter");
        counter.setAccessible(true);
        counter.set(null, new AtomicInteger(1));
    }

    private static Integer getShutdownCounter() throws Exception {
        Field counter = ShutdownHooks.SpaceliftShutDownHook.class.getDeclaredField("shutdownCounter");
        counter.setAccessible(true);
        return ((AtomicInteger) counter.get(null)).get();
    }

    @Before
    public void resetCounter() throws Exception {
        resetShutdownCounter();
    }

    @Test
    public void registerHook() throws Exception {

        // run only on linux
        Assume.assumeThat(SystemUtils.IS_OS_LINUX, is(true));

        Execution<ProcessResult> yes = Spacelift.task(CommandTool.class).programName("yes").parameters("spacelift")
            .shouldExitWith(143)
            .execute();

        // wait until process has started
        Thread.sleep(500);

        Assert.assertThat("Shutdown hook was egistered", getShutdownCounter(), is(2));

        yes.terminate();
        Assert.assertThat(yes.isFinished(), is(true));

        ProcessResult result = yes.await();
        if (result != null) {
            Assert.assertThat(result.output().size(), is(not(0)));
        }
    }

    @Test
    public void dontRegisterHook() throws Exception {

        // run only on linux
        Assume.assumeThat(SystemUtils.IS_OS_LINUX, is(true));

        Execution<ProcessResult> yes = Spacelift.task(CommandTool.class).programName("yes").parameters("spacelift")
            .shouldExitWith(143)
            .runAsDaemon().execute();

        // wait until process has started
        Thread.sleep(500);

        Assert.assertThat("Shutdown hook was not registered", getShutdownCounter(), is(1));

        yes.terminate();
        Assert.assertThat(yes.isFinished(), is(true));

        ProcessResult result = yes.await();
        if (result != null) {
            Assert.assertThat(result.output().size(), is(not(0)));
        }
    }
}
