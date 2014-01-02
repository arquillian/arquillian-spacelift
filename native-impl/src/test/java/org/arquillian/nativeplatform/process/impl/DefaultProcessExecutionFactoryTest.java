package org.arquillian.nativeplatform.process.impl;

import java.util.Arrays;
import java.util.List;

import org.arquillian.nativeplatform.ArquillianNativePlatformExtension;
import org.arquillian.nativeplatform.process.ProcessExecutor;
import org.arquillian.nativeplatform.process.ProcessExecutorFactory;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.spi.ServiceLoader;
import org.jboss.arquillian.test.test.AbstractTestTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.notNullValue;

import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DefaultProcessExecutionFactoryTest extends AbstractTestTestBase {

    @Mock
    private ServiceLoader serviceLoader;

    @Override
    protected void addExtensions(List<Class<?>> extensions) {
        extensions.add(ArquillianNativePlatformExtension.class);
    }

    @org.junit.Before
    public void setMocks() {
        bind(ApplicationScoped.class, ServiceLoader.class, serviceLoader);

        Mockito.when(serviceLoader.onlyOne(ProcessExecutorFactory.class))
            .thenReturn(new DefaultProcessExecutionFactory());
        Mockito.when(serviceLoader.all(ProcessExecutorFactory.class))
            .thenReturn(Arrays.<ProcessExecutorFactory> asList(new DefaultProcessExecutionFactory()));
    }

    @Test
    public void instantiation() {
        ProcessExecutorFactory factory = serviceLoader.onlyOne(ProcessExecutorFactory.class);
        ProcessExecutor executor = factory.getProcessExecutorInstance();
        assertThat(executor, notNullValue());
    }
}
