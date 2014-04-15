package org.arquillian.spacelift.tool.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.arquillian.spacelift.SpaceliftBootstrap;
import org.arquillian.spacelift.tool.ToolRegistry;
import org.arquillian.spacelift.tool.basic.DownloadTool;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;

public class ToolRegistrar {

    private static final Logger log = Logger.getLogger(ToolRegistrar.class.getName());

    @Inject
    @ApplicationScoped
    private InstanceProducer<ToolRegistry> toolRegistry;

    public void createProcessExecutor(@Observes SpaceliftBootstrap event) {

        ToolRegistry registry = new ToolRegistryImpl();

        toolRegistry.set(registry);
        log.log(Level.FINE, "Registered Tool Registry");

        // FIXME register default tools here
        registry.register(DownloadTool.class);
    }
}
