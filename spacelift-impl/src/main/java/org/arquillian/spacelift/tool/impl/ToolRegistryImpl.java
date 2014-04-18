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
package org.arquillian.spacelift.tool.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.arquillian.spacelift.execution.Tasks;
import org.arquillian.spacelift.tool.InvalidToolException;
import org.arquillian.spacelift.tool.Tool;
import org.arquillian.spacelift.tool.ToolRegistry;

/**
 * Implementation of tool registry. This registry creates a new tool instance per each {@link ToolRegistry#find(Class)} or
 * {@link ToolRegistry#find(String)} call.
 *
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class ToolRegistryImpl implements ToolRegistry {

    private Map<Class<? extends Tool<?, ?>>, Collection<String>> toolTypeAliases;

    private Map<String, Class<? extends Tool<?, ?>>> toolClasses;

    public ToolRegistryImpl() {
        this.toolTypeAliases = new HashMap<Class<? extends Tool<?, ?>>, Collection<String>>();
        this.toolClasses = new HashMap<String, Class<? extends Tool<?, ?>>>();
    }

    @Override
    public <IN, OUT, TOOL extends Tool<IN, OUT>> ToolRegistry register(Class<TOOL> tool) throws InvalidToolException {
        try {
            Tool<IN, OUT> instance = Tasks.prepare(tool);
            Collection<String> aliases = getAliases(instance);
            toolTypeAliases.put(tool, new ArrayList<String>(aliases));
            for (String alias : aliases) {
                toolClasses.put(alias, tool);
            }
        } catch (IllegalStateException e) {
            throw new InvalidToolException(e, "Unable to register tool {0}", tool);
        } catch (IllegalArgumentException e) {
            throw new InvalidToolException(e, "Unable to register tool {0}", tool);
        } catch (RuntimeException e) {
            throw new InvalidToolException(e, "Unable to register tool {0}", tool);
        }

        return this;
    }

    @Override
    public <IN, OUT, TOOL extends Tool<IN, OUT>> TOOL find(Class<TOOL> toolType) {
        if (toolTypeAliases.containsKey(toolType)) {
            return Tasks.prepare(toolType);
        }
        return null;
    }

    @Override
    public Tool<?, ?> find(String alias) {

        Class toolType = toolClasses.get(alias);
        if (toolType != null) {
            return (Tool<?, ?>) Tasks.prepare(toolType);
        }

        // no such tool was registered
        return null;
    }

    @Override
    public <IN, OUT> Tool<IN, OUT> find(String alias, Class<IN> inType, Class<OUT> outType) throws InvalidToolException {

        Class toolType = toolClasses.get(alias);
        if (toolType != null) {
            return (Tool<IN, OUT>) Tasks.prepare(toolType);
        }

        // no such tool was registered
        return null;
    }

    @Override
    public Map<Collection<String>, Class<? extends Tool<?, ?>>> allTools() {

        Map<Collection<String>, Class<? extends Tool<?, ?>>> allTools = new HashMap<Collection<String>, Class<? extends Tool<?, ?>>>();

        for (Map.Entry<Class<? extends Tool<?, ?>>, Collection<String>> entry : toolTypeAliases.entrySet()) {
            allTools.put(entry.getValue(), entry.getKey());
        }

        return allTools;
    }

    private Collection<String> getAliases(Tool<?, ?> toolInstance) {
        try {
            return (Collection<String>) SecurityActions.getMethod(Tool.class, "aliases").invoke(toolInstance);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

}
