package org.arquillian.spacelift.process.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.arquillian.spacelift.process.ProcessExecutor;
import org.arquillian.spacelift.process.ProcessExecutorFactory;
import org.arquillian.spacelift.process.event.ProcessExecutorCreated;
import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.ServiceLoader;

public class ProcessExecutorCreator {

    private static final Logger log = Logger.getLogger(ProcessExecutorCreator.class.getName());

    @Inject
    @ApplicationScoped
    private InstanceProducer<ProcessExecutor> processExecutorInstance;

    @Inject
    private Instance<ServiceLoader> serviceLoader;

    @Inject
    private Event<ProcessExecutorCreated> processExecutorCreated;

    public void createProcessExecutor(@Observes ArquillianDescriptor event) {

        ProcessExecutorFactory factory = serviceLoader.get().onlyOne(ProcessExecutorFactory.class,
            DefaultProcessExecutorFactory.class);

        log.log(Level.FINE, "Retrieving ProcessExecutor instance from factory");

        ProcessExecutor executor = factory.getProcessExecutorInstance();
        processExecutorInstance.set(executor);

        processExecutorCreated.fire(new ProcessExecutorCreated(executor));
    }
}
