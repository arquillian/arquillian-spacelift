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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TestToolRegistry implements ToolRegistry {

    private Map<Class<? extends Tool<?>>, Tool<?>> toolMap;

    private Map<String, Class<? extends Tool<?>>> toolAlias;

    public TestToolRegistry() {
        this.toolAlias = new HashMap<String, Class<? extends Tool<?>>>();
        this.toolMap = new HashMap<Class<? extends Tool<?>>, Tool<?>>();
    }

    @Override
    public ToolRegistry register(Class<? extends Tool<?>> tool) {

        Tool<?> t;
        try {
            t = tool.newInstance();

            toolMap.put(tool, t);
            for (String alias : t.aliases()) {
                toolAlias.put(alias, tool);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return this;
    }

    @Override
    public <TOOL extends Tool<TOOL>> TOOL find(Class<TOOL> toolType) {
        return toolType.cast(toolMap.get(toolType));
    }

    @Override
    public Tool<?> find(String alias) {
        return toolMap.get(toolAlias.get(alias));
    }

    @Override
    public ExternalTool<?> findExternalTool(String alias) throws InvalidToolException {
        try {
            return ExternalTool.class.cast(find(alias));
        } catch (ClassCastException e) {
            throw new InvalidToolException();
        }
    }

    @Override
    public InternalTool<?, ?> findInternalTool(String alias) throws InvalidToolException {
        try {
            return InternalTool.class.cast(find(alias));
        } catch (ClassCastException e) {
            throw new InvalidToolException();
        }
    }

    @Override
    public Map<Collection<String>, Class<? extends Tool<?>>> allTools() {

        Map<Collection<String>, Class<? extends Tool<?>>> map = new HashMap<Collection<String>, Class<? extends Tool<?>>>();
        for (Map.Entry<String, Class<? extends Tool<?>>> entry : toolAlias.entrySet()) {
            boolean newEntry = true;

            // find entry with same value and add alias
            for (Map.Entry<Collection<String>, Class<? extends Tool<?>>> entry2 : map.entrySet()) {
                if (entry.getValue().equals(entry2.getValue())) {
                    entry2.getKey().add(entry.getKey());
                    newEntry = false;
                    break;
                }
            }

            if (newEntry) {
                List<String> aliases = new ArrayList<String>();
                aliases.add(entry.getKey());
                map.put(aliases, entry.getValue());
            }
        }

        return map;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<Collection<String>, Class<? extends ExternalTool<?>>> allExternalTools() {
        Map<Collection<String>, Class<? extends ExternalTool<?>>> externalTools = new HashMap<Collection<String>, Class<? extends ExternalTool<?>>>();

        for (Map.Entry<Collection<String>, Class<? extends Tool<?>>> entry : allTools().entrySet()) {
            if (ExternalTool.class.isAssignableFrom(entry.getValue())) {
                externalTools.put(entry.getKey(), (Class<? extends ExternalTool<?>>) entry.getValue());
            }
        }

        return externalTools;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<Collection<String>, Class<? extends InternalTool<?, ?>>> allInternalTools() {
        Map<Collection<String>, Class<? extends InternalTool<?, ?>>> internalTools = new HashMap<Collection<String>, Class<? extends InternalTool<?, ?>>>();

        for (Map.Entry<Collection<String>, Class<? extends Tool<?>>> entry : allTools().entrySet()) {
            if (InternalTool.class.isAssignableFrom(entry.getValue())) {
                internalTools.put(entry.getKey(), (Class<? extends InternalTool<?, ?>>) entry.getValue());
            }
        }

        return internalTools;
    }
}
