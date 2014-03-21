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
package org.arquillian.spacelift.process.impl;

import static org.hamcrest.CoreMatchers.*;

import java.io.IOException;

import org.arquillian.spacelift.process.CommandBuilder;
import org.arquillian.spacelift.process.ProcessExecutionException;
import org.arquillian.spacelift.process.ProcessExecutor;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ProcessNameTest {

    private ProcessExecutor executor;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        executor = new ProcessExecutorImpl();

    }

    @After
    public void tearDown() {
        executor = null;
    }

    @Test
    public void invalidCommand() {
        exception.expect(ProcessExecutionException.class);
        executor.execute(new CommandBuilder().add("foo-invalid-command", "-bar", "-baz").build());
    }

    @Test
    public void invalidResult() {
        exception.expect(ProcessExecutionException.class);
        exception.expectMessage(containsString("java -bar -baz"));
        executor.execute(new CommandBuilder().add("java", "-bar", "-baz").build());
    }


}
