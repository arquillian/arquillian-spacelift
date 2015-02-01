/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat Middleware LLC, and individual contributors
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
package org.arquillian.spacelift.task;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.apache.commons.lang3.SystemUtils;
import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.process.Command;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ToolTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void getJavaToolFromRegistry() {

        TaskRegistry registry = new TestTaskRegistry();
        registry.register(TestJavaTool.class);

        Command javaCommand = registry.find(TestJavaTool.class).parameter("-foo").getCommand();

        if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC_OSX) {
            assertThat(javaCommand.toString(), equalTo("java -foo"));
        }
        else if (SystemUtils.IS_OS_WINDOWS) {
            assertThat(javaCommand.toString(), equalTo("java.exe -foo"));
        }
    }

    @Test
    public void getJavaToolFromRegistryByAlias() {

        TaskRegistry registry = new TestTaskRegistry();
        registry.register(TestJavaTool.class, "java");

        Task<?, ?> task = registry.find("java");
        assertThat(task, notNullValue());
    }

    @Test
    public void getJavaToolFromSpaceliftByAlias() {
        Spacelift.registry().register(TestJavaTool.class, "java");

        Task<?, ?> task = Spacelift.task("java");
        assertThat(task, notNullValue());
    }

    @Test
    public void getInvalidToolType() {

        TaskRegistry registry = new TestTaskRegistry();
        registry.register(TestJavaTool.class, "java");

        registry.find("java", Object.class, Object.class).execute();

        // FIXME, exception is raised on assignement, not during find command
        exception.expect(ClassCastException.class);
        @SuppressWarnings("unused")
        Integer x = registry.find("java", Object.class, Integer.class).execute().await();
    }
}
