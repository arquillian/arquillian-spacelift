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

import java.util.Collection;
import java.util.Map;

/**
 * Registry that contains all available tools.
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public interface ToolRegistry {

    /**
     * Registers tool into the registry
     *
     * @param tool
     * @return
     * @throws InvalidToolException
     */
    <IN, OUT, TOOL extends Tool<IN, OUT>> ToolRegistry register(Class<TOOL> tool) throws InvalidToolException;

    /**
     * Finds tool by its type.
     *
     * @param toolType
     * @return Either tool or {@code null}
     */
    <IN, OUT, TOOL extends Tool<IN, OUT>> TOOL find(Class<TOOL> toolType);

    /**
     * Finds tool by its alias.
     *
     * This method should be used only by metadata bindings.
     *
     * @param alias the alias
     * @return Either tool or {@code null}
     */
    Tool<?, ?> find(String alias);

    /**
     * Finds tool by its alias.
     *
     * This method should be used only by metadata bindings.
     *
     * @param alias the alias
     * @param inType the input type of the tool
     * @param outType the output type of the tool
     * @return Either tool or {@code null}
     * @throws InvalidToolException in case that tool input/output type is different
     */
    <IN, OUT> Tool<IN, OUT> find(String alias, Class<IN> inType, Class<OUT> outType) throws InvalidToolException;

    /**
     * Returns all registered tools
     *
     * @return
     */
    Map<Collection<String>, Class<? extends Tool<?, ?>>> allTools();
}
