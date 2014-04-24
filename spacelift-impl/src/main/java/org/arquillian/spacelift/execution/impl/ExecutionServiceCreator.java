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
package org.arquillian.spacelift.execution.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.arquillian.spacelift.SpaceliftBootstrap;
import org.arquillian.spacelift.execution.ExecutionService;
import org.arquillian.spacelift.execution.ExecutionServiceFactory;
import org.arquillian.spacelift.execution.Tasks;
import org.arquillian.spacelift.process.event.ExecutionServiceInitialized;
import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.ServiceLoader;

/**
 * Initialization of {@link ExecutionService}
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class ExecutionServiceCreator {

    private static final Logger log = Logger.getLogger(ExecutionServiceCreator.class.getName());

    @Inject
    @ApplicationScoped
    private InstanceProducer<ExecutionService> processExecutorInstance;

    @Inject
    private Instance<ServiceLoader> serviceLoader;

    @Inject
    private Event<ExecutionServiceInitialized> executionServiceInitialized;

    public void createProcessExecutor(@Observes SpaceliftBootstrap event) {

        ExecutionServiceFactory factory = serviceLoader.get().onlyOne(ExecutionServiceFactory.class,
            DefaultExecutionServiceFactory.class);

        log.log(Level.FINE, "Registering ExecutionServiceFactory for Tasks creation");

        Tasks.setDefaultExecutionServiceFactory(factory);

        log.log(Level.FINE, "Retrieving ExecutionService instance from factory");

        ExecutionService service = factory.getExecutionServiceInstance();
        processExecutorInstance.set(service);

        executionServiceInitialized.fire(new ExecutionServiceInitialized());
    }
}
