package org.arquillian.spacelift.tool.impl;

import java.util.List;

import org.arquillian.spacelift.SpaceliftBootstrap;
import org.arquillian.spacelift.tool.ToolRegistry;
import org.arquillian.spacelift.tool.basic.DownloadTool;
import org.jboss.arquillian.core.spi.context.ApplicationContext;
import org.jboss.arquillian.test.test.AbstractTestTestBase;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import static org.junit.Assert.assertThat;

public class ToolRegistryTest extends AbstractTestTestBase {

    @Override
    protected void addExtensions(List<Class<?>> extensions) {
        extensions.add(ToolRegistrar.class);
    }

    @Test
    public void testToolRegistryCreated() throws Exception {
        fire(new SpaceliftBootstrap());

        ToolRegistry registry = getManager().getContext(ApplicationContext.class)
            .getObjectStore()
            .get(ToolRegistry.class);

        assertThat(registry, is(notNullValue()));

        assertThat(registry.find(DownloadTool.class), is(notNullValue()));
    }

}
