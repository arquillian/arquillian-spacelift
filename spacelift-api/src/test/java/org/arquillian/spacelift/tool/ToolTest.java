package org.arquillian.spacelift.tool;

import java.util.Arrays;
import java.util.Collection;

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

        @Override
        public Collection<String> aliases() {
            return Arrays.asList("java");
        }

        @Override
        public CommandBuilder getCommandBuilder() {
            if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC_OSX) {
                return new CommandBuilder("java");
            }
            else if (SystemUtils.IS_OS_WINDOWS) {
                return new CommandBuilder("java.exe");
            }
            throw new UnsupportedOperationException("Java tool is not supported for " + SystemUtils.OS_NAME);
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

        Command javaCommand = registry.find(JavaTool.class).getCommandBuilder().parameter("-foo").build();

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

        Command javaCommand = registry.findExternalTool("java").getCommandBuilder().parameter("-foo").build();

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
