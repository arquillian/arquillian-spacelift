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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.arquillian.spacelift.tool.ExternalTool;
import org.arquillian.spacelift.tool.InternalTool;
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

    Map<Class<? extends Tool<?>>, Collection<String>> toolTypeAliases;

    Map<String, Class<? extends Tool<?>>> toolClasses;

    public ToolRegistryImpl() {
        this.toolTypeAliases = new HashMap<Class<? extends Tool<?>>, Collection<String>>();
        this.toolClasses = new HashMap<String, Class<? extends Tool<?>>>();
    }

    @Override
    public synchronized ToolRegistry register(Class<? extends Tool<?>> tool) throws InvalidToolException {

        try {
            Tool<?> instance = SecurityActions.newInstance(tool);
            toolTypeAliases.put(tool, new ArrayList<String>(instance.aliases()));
            for (String alias : instance.aliases()) {
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
    public <TOOL extends Tool<TOOL>> TOOL find(Class<TOOL> toolType) {

        if (toolTypeAliases.containsKey(toolType)) {
            return SecurityActions.newInstance(toolType);
        }

        // no such tool was registered
        return null;
    }

    @Override
    public Tool<?> find(String alias) {

        Class<? extends Tool<?>> toolType = toolClasses.get(alias);
        if (toolType != null) {
            return SecurityActions.newInstance(toolType);
        }

        // no such tool was registered
        return null;
    }

    @Override
    public InternalTool<?, ?> findInternalTool(String alias) throws InvalidToolException {

        Tool<?> tool = find(alias);
        if (tool != null) {
            try {
                return InternalTool.class.cast(tool);
            } catch (ClassCastException e) {
                throw new InvalidToolException("Tool implementation {0} registered by alias {1} is not an InternalTool",
                    tool.getClass().getName(),
                    alias);
            }
        }

        // no such tool was registered
        return null;
    }

    @Override
    public ExternalTool<?> findExternalTool(String alias) throws InvalidToolException {

        Tool<?> tool = find(alias);
        if (tool != null) {
            try {
                return ExternalTool.class.cast(tool);
            } catch (ClassCastException e) {
                throw new InvalidToolException("Tool implementation {0} registered by alias {1} is not an ExternalTool",
                    tool.getClass().getName(),
                    alias);
            }
        }

        // no such tool was registered
        return null;
    }

    @Override
    public Map<Collection<String>, Class<? extends Tool<?>>> allTools() {

        Map<Collection<String>, Class<? extends Tool<?>>> allTools = new HashMap<Collection<String>, Class<? extends Tool<?>>>();

        for (Map.Entry<Class<? extends Tool<?>>, Collection<String>> entry : toolTypeAliases.entrySet()) {
            allTools.put(entry.getValue(), entry.getKey());
        }

        return allTools;
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

}
