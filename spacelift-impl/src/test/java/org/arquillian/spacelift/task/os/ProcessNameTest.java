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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import org.apache.commons.lang3.SystemUtils;
import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.execution.ExecutionException;
import org.arquillian.spacelift.process.ProcessInteractionBuilder;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;

public class ProcessNameTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void invalidCommand() {
        exception.expect(ExecutionException.class);

        Spacelift.task(CommandTool.class).programName("foo-invalid-command").parameters("-bar", "-baz").execute().await();
    }

    @Test
    public void invalidResult() {

        // run only on linux
        Assume.assumeThat(SystemUtils.IS_OS_LINUX, is(true));

        exception.expect(ExecutionException.class);
        exception.expectMessage(containsString("java -bar -baz"));

        Spacelift.task(CommandTool.class).programName("java").parameters("-bar", "-baz").execute().await();
    }

    @Test
    public void invalidResultMarkAsPassing() {

        // run only on linux
        Assume.assumeThat(SystemUtils.IS_OS_LINUX, is(true));
        Spacelift.task(CommandTool.class)
            .programName("java")
            .parameters("-bar", "-baz")
            .shouldExitWith(1)
            .execute()
            .await();
    }

    @Test
    public void outputNoPrefix() throws UnsupportedEncodingException {

        // run only on linux
        Assume.assumeThat(SystemUtils.IS_OS_LINUX, is(true));

        final ByteArrayOutputStream errorOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(errorOutput));
        exception = ExpectedException.none();

        try {
            exception.expectMessage(containsString("java -bar -baz"));

            Spacelift.task(CommandTool.class).programName("java").parameters("-bar", "-baz")
                .interaction(new ProcessInteractionBuilder().outputPrefix("").when(".*").printToOut())
                .execute().await();
        } catch (ExecutionException e) {
            // ignore
        }
        String output = errorOutput.toString(Charset.defaultCharset().name());
        Assert.assertThat(output, startsWith("Unrecognized option: -bar"));
    }

    @Test
    public void outputDefaultPrefix() throws UnsupportedEncodingException {

        // run only on linux
        Assume.assumeThat(SystemUtils.IS_OS_LINUX, is(true));

        final ByteArrayOutputStream errorOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(errorOutput));
        exception = ExpectedException.none();

        try {
            exception.expectMessage(containsString("java -bar -baz"));

            Spacelift.task(CommandTool.class).programName("java").parameters("-bar", "-baz")
                .interaction(new ProcessInteractionBuilder().when(".*").printToOut())
                .execute().await();
        } catch (ExecutionException e) {
            // ignore
        }
        String output = errorOutput.toString(Charset.defaultCharset().name());
        Assert.assertThat(output, startsWith("(java):Unrecognized option: -bar"));
    }
}
