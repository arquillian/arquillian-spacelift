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
package org.arquillian.spacelift.tool;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.arquillian.spacelift.process.Command;
import org.arquillian.spacelift.process.CommandBuilder;
import org.arquillian.spacelift.process.ProcessInteraction;
import org.arquillian.spacelift.process.ProcessInteractionBuilder;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

import static org.junit.Assert.assertThat;

public class ToolTest {

    static class JavaTool implements ExternalTool<JavaTool> {

        private CommandBuilder command;

        public JavaTool() {
            if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC_OSX) {
                this.command = new CommandBuilder("java");
            }
            else if (SystemUtils.IS_OS_WINDOWS) {
                this.command = new CommandBuilder("java.exe");
            }
            throw new UnsupportedOperationException("Java tool is not supported for " + SystemUtils.OS_NAME);
        }

        @Override
        public Collection<String> aliases() {
            return Arrays.asList("java");
        }

        @Override
        public JavaTool parameter(CharSequence parameter) {
            command.parameter(parameter);
            return this;
        }

        @Override
        public JavaTool parameters(CharSequence... parameters) {
            command.parameters(parameters);
            return this;
        }

        @Override
        public JavaTool parameters(List<? extends CharSequence> parameters) {
            command.parameters(parameters);
            return this;
        }

        @Override
        public JavaTool splitToParameters(CharSequence sequenceToBeParsed) {
            command.splitToParameters(sequenceToBeParsed);
            return this;
        }

        @Override
        public Command build() {
            return command.build();
        }

        @Override
        public ProcessInteraction getInteraction() {
            return new ProcessInteractionBuilder().outputs(".*").build();
        }
    }

    @Test
    public void getJavaToolFromRegistry() {

        ToolRegistry registry = new TestToolRegistry();
        registry.register(JavaTool.class);

        Command javaCommand = registry.find(JavaTool.class).parameter("-foo").build();

        if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC_OSX) {
            assertThat(javaCommand.toString(), equalTo("java -foo"));
        }
        else if (SystemUtils.IS_OS_WINDOWS) {
            assertThat(javaCommand.toString(), equalTo("java.exe -foo"));
        }
    }

    @Test
    public void getJavaToolFromRegistryById() {

        ToolRegistry registry = new TestToolRegistry();
        registry.register(JavaTool.class);

        Command javaCommand = registry.findExternalTool("java").parameter("-foo").build();

        if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC_OSX) {
            assertThat(javaCommand.toString(), equalTo("java -foo"));
        }
        else if (SystemUtils.IS_OS_WINDOWS) {
            assertThat(javaCommand.toString(), equalTo("java.exe -foo"));
        }
    }

    @Test(expected = InvalidToolException.class)
    public void getInvalidToolType() {

        ToolRegistry registry = new TestToolRegistry();
        registry.register(JavaTool.class);

        registry.findInternalTool("java").getCallable();
    }

    @Test
    public void checkRegistryContent() {
        ToolRegistry registry = new TestToolRegistry();
        registry.register(JavaTool.class);

        assertThat(registry.allTools().size(), equalTo(1));
        assertThat(registry.allExternalTools().size(), equalTo(1));
        assertThat(registry.allInternalTools().size(), equalTo(0));
    }

}
