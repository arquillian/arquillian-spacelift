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
package org.arquillian.nativeplatform.process.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.arquillian.nativeplatform.ArquillianNativePlatformExtension;
import org.arquillian.nativeplatform.process.ProcessExecutor;
import org.arquillian.nativeplatform.process.ProcessExecutorCreated;
import org.arquillian.nativeplatform.process.ProcessExecutorFactory;
import org.jboss.arquillian.config.descriptor.impl.ArquillianDescriptorImpl;
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
public class DefaultProcessExecutionFactoryTest extends AbstractTestTestBase {

    @Mock
    private ServiceLoader serviceLoader;

    @Override
    protected void addExtensions(List<Class<?>> extensions) {
        extensions.add(ArquillianNativePlatformExtension.class);
        extensions.add(ProcessExecutorCreator.class);
    }

    @org.junit.Before
    public void setMocks() {
        bind(ApplicationScoped.class, ServiceLoader.class, serviceLoader);

        Mockito.when(serviceLoader.onlyOne(ProcessExecutorFactory.class))
            .thenReturn(new DefaultProcessExecutionFactory());
        Mockito.when(serviceLoader.onlyOne(ProcessExecutorFactory.class, DefaultProcessExecutionFactory.class))
            .thenReturn(new DefaultProcessExecutionFactory());
    }

    @Test
    public void instantiation() {
        ProcessExecutorFactory factory = serviceLoader.onlyOne(ProcessExecutorFactory.class);
        ProcessExecutor executor = factory.getProcessExecutorInstance();
        assertThat(executor, notNullValue());
    }

    @Test
    public void singleton() {
        ProcessExecutorFactory factory = serviceLoader.onlyOne(ProcessExecutorFactory.class);
        ProcessExecutor executor1 = factory.getProcessExecutorInstance();
        assertThat(executor1, notNullValue());

        ProcessExecutor executor2 = factory.getProcessExecutorInstance();
        assertThat(executor2, sameInstance(executor1));

        ProcessExecutor executor3 = new DefaultProcessExecutionFactory().getProcessExecutorInstance();
        assertThat(executor3, sameInstance(executor1));

        // if we change environment, it modifies all existing instances
        Map<String, String> env = new HashMap<String, String>();
        env.put("foo", "bar");
        executor3.setEnvironment(env);

        assertThat(executor3, sameInstance(executor1));
    }

    @Test
    public void fireBeforeSuite() {
        //getManager().getContext(SuiteContext.class).activate();
        fire(new ArquillianDescriptorImpl("arquillian.xml"));

        ProcessExecutor executor = getManager().getContext(ApplicationContext.class)
            .getObjectStore()
            .get(ProcessExecutor.class);

        assertThat(executor, notNullValue());
        assertEventFired(ProcessExecutorCreated.class);
    }
}
