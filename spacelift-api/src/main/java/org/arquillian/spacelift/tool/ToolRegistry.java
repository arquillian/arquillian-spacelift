package org.arquillian.spacelift.tool;

import java.util.Collection;
import java.util.Map;

/**
 * Registry that contains all available tool.
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public interface ToolRegistry {

    ToolRegistry register(Class<? extends Tool<?>> tool) throws InvalidToolException;

    <TOOL extends Tool<TOOL>> TOOL find(Class<TOOL> toolType);

    Tool<?> find(String alias);

    InternalTool<?, ?> findInternalTool(String alias) throws InvalidToolException;

    ExternalTool<?> findExternalTool(String alias) throws InvalidToolException;

    Map<Collection<String>, Class<? extends Tool<?>>> allTools();

    Map<Collection<String>, Class<? extends InternalTool<?, ?>>> allInternalTools();

    Map<Collection<String>, Class<? extends ExternalTool<?>>> allExternalTools();
}
