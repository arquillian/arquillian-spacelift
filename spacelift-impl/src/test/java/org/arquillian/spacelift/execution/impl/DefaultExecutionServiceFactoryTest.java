/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.arquillian.spacelift.execution.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.arquillian.spacelift.SpaceliftBootstrap;
import org.arquillian.spacelift.execution.ExecutionService;
import org.arquillian.spacelift.execution.ExecutionServiceFactory;
import org.arquillian.spacelift.process.event.ExecutionServiceInitialized;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.spi.ServiceLoader;
import org.jboss.arquillian.core.spi.context.ApplicationContext;
import org.jboss.arquillian.test.test.AbstractTestTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;

import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DefaultExecutionServiceFactoryTest extends AbstractTestTestBase {

    @Mock
    private ServiceLoader serviceLoader;

    @Override
    protected void addExtensions(List<Class<?>> extensions) {
        extensions.add(ExecutionServiceCreator.class);
    }

    @org.junit.Before
    public void setMocks() {
        bind(ApplicationScoped.class, ServiceLoader.class, serviceLoader);

        Mockito.when(serviceLoader.onlyOne(ExecutionServiceFactory.class))
            .thenReturn(new DefaultExecutionServiceFactory());
        Mockito.when(serviceLoader.onlyOne(ExecutionServiceFactory.class, DefaultExecutionServiceFactory.class))
            .thenReturn(new DefaultExecutionServiceFactory());
    }

    @Test
    public void instantiation() {
        ExecutionServiceFactory factory = serviceLoader.onlyOne(ExecutionServiceFactory.class);
        ExecutionService executor = factory.getExecutionServiceInstance();
        assertThat(executor, notNullValue());
    }

    @Test
    public void singleton() {
        ExecutionServiceFactory factory = serviceLoader.onlyOne(ExecutionServiceFactory.class);
        ExecutionService executor1 = factory.getExecutionServiceInstance();
        assertThat(executor1, notNullValue());

        ExecutionService executor2 = factory.getExecutionServiceInstance();
        assertThat(executor2, sameInstance(executor1));

        ExecutionService executor3 = new DefaultExecutionServiceFactory().getExecutionServiceInstance();
        assertThat(executor3, sameInstance(executor1));

        // if we change environment, it modifies all existing instances
        Map<String, String> env = new HashMap<String, String>();
        env.put("foo", "bar");
        executor3.setEnvironment(env);

        assertThat(executor3, sameInstance(executor1));

        executor3.setWorkingDirectory(System.getProperty("user.dir"));
        assertThat(executor3, sameInstance(executor1));
    }

    @Test
    public void fireBeforeSuite() {
        //getManager().getContext(SuiteContext.class).activate();
        fire(new SpaceliftBootstrap());

        ExecutionService executor = getManager().getContext(ApplicationContext.class)
            .getObjectStore()
            .get(ExecutionService.class);

        assertThat(executor, notNullValue());
        assertEventFired(ExecutionServiceInitialized.class);
    }
}
