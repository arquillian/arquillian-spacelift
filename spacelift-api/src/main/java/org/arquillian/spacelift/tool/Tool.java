package org.arquillian.spacelift.tool;

import java.util.Collection;

/**
 * Abstraction of a tool. Tool is able to run a command on current platform.
 *
 * Tool can also provide a high level API to work with the command.
 *
 * Tool is supposed to have a no-arg constructor.
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 * @param <TOOLTYPE> Type of the tool
 */
public interface Tool<TOOLTYPE extends Tool<TOOLTYPE>> {

    /**
     * Returns a collection of aliases for this tool. An alias can be used to get a tool
     * from the {@link ToolRegistry}
     *
     * @return
     */
    Collection<String> aliases();
}
