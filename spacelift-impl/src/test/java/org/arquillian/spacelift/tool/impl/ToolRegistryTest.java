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

import java.util.List;

import org.arquillian.spacelift.SpaceliftBootstrap;
import org.arquillian.spacelift.execution.ExecutionServiceFactory;
import org.arquillian.spacelift.execution.impl.DefaultExecutionServiceFactory;
import org.arquillian.spacelift.execution.impl.ExecutionServiceCreator;
import org.arquillian.spacelift.tool.ToolRegistry;
import org.arquillian.spacelift.tool.basic.DownloadTool;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.spi.ServiceLoader;
import org.jboss.arquillian.core.spi.context.ApplicationContext;
import org.jboss.arquillian.test.test.AbstractTestTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ToolRegistryTest extends AbstractTestTestBase {

    @Mock
    private ServiceLoader serviceLoader;

    @org.junit.Before
    public void setMocks() {

        Mockito.when(serviceLoader.onlyOne(ExecutionServiceFactory.class))
            .thenReturn(new DefaultExecutionServiceFactory());
        Mockito.when(serviceLoader.onlyOne(ExecutionServiceFactory.class, DefaultExecutionServiceFactory.class))
            .thenReturn(new DefaultExecutionServiceFactory());

        bind(ApplicationScoped.class, ServiceLoader.class, serviceLoader);
    }

    @Override
    protected void addExtensions(List<Class<?>> extensions) {
        extensions.add(ExecutionServiceCreator.class);
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
