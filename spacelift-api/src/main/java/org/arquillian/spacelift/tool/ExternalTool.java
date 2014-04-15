package org.arquillian.spacelift.tool;

import org.arquillian.spacelift.process.Command;
import org.arquillian.spacelift.process.CommandBuilder;
import org.arquillian.spacelift.process.ProcessExecutor;
import org.arquillian.spacelift.process.ProcessInteraction;

/**
 * Representation of the tool that requires an external command to do the work.
 *
 * @see Command
 * @see CommandBuilder
 * @see ProcessExecutor
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 * @param <TOOLTYPE> Type of the tool
 */
public interface ExternalTool<TOOLTYPE extends ExternalTool<TOOLTYPE>> extends Tool<TOOLTYPE> {

    /**
     * Returns a {@see {@link CommandBuilder} that can be used to run the {@see Command} via {@see ProcessExecutor}. It is
     * expected to
     * return command compatible with current platform.
     *
     * @return
     * @throws UnsupportedOperationException In case given tool is not supported on current platform
     */
    CommandBuilder getCommandBuilder() throws UnsupportedOperationException;

    /**
     * Returns a {@see ProcessInteraction} that defines how {@see ProcessExecution} is handled by default for a particular tool.
     *
     * @return
     */
    ProcessInteraction getInteraction();
}
