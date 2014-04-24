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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.arquillian.spacelift.process.event.ExecutionServiceInitialized;
import org.arquillian.spacelift.tool.ToolRegistry;
import org.arquillian.spacelift.tool.basic.DownloadTool;
import org.arquillian.spacelift.tool.basic.UnzipTool;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;

/**
 * Creator of {@link ToolRegistry}
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class ToolRegistrar {

    private static final Logger log = Logger.getLogger(ToolRegistrar.class.getName());

    @Inject
    @ApplicationScoped
    private InstanceProducer<ToolRegistry> toolRegistry;

    public void createProcessExecutor(@Observes ExecutionServiceInitialized event) {

        ToolRegistry registry = new ToolRegistryImpl();

        toolRegistry.set(registry);
        log.log(Level.FINE, "Registered Tool Registry");

        registry.register(DownloadTool.class);
        registry.register(UnzipTool.class);
    }
}
