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
package org.arquillian.spacelift.execution;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.arquillian.spacelift.execution.Invokable.InvocationException;

/**
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class Tasks {

    private static final Logger log = Logger.getLogger(Tasks.class.getName());

    private static class ExecutionServiceFactoryHolder {
        public static ExecutionServiceFactory lastFactory;
        static {
            try {
                lastFactory = ImplementationLoader.implementationOf(ExecutionServiceFactory.class);
            } catch (InvocationException e) {
                log.log(Level.SEVERE,
                    "Unable to find default implemenation of {0} on classpath, make sure that you set one programatically.",
                    ExecutionServiceFactory.class.getName());
            }
        }
    }

    private static ExecutionServiceFactory getAsSingleton() {
        return ExecutionServiceFactoryHolder.lastFactory;
    }

    public static ExecutionServiceFactory getExecutionServiceFactoryInstance() throws IllegalStateException {
        ExecutionServiceFactory factory = getAsSingleton();
        if (factory == null) {
            throw new IllegalStateException("ExecutionServiceFactory was null. Make sure that you set it up first either by registering implementation via SPI or by programatically calling setter method.");
        }
        return factory;
    }

    public static void setDefaultExecutionServiceFactory(ExecutionServiceFactory executionServiceFactory) {
        synchronized (ExecutionServiceFactoryHolder.class) {
            ExecutionServiceFactoryHolder.lastFactory = executionServiceFactory;
        }
    }

    public static <IN, OUT, TASK extends Task<? super IN, OUT>> TASK prepare(Class<TASK> taskDef) {
        TASK task = SecurityActions.newInstance(taskDef);
        task.setExecutionService(getExecutionServiceFactoryInstance().getExecutionServiceInstance());
        return task;
    }

    public static <IN, OUT, TASK extends Task<? super IN, OUT>> TASK chain(IN input, Class<TASK> taskDef) {

        @SuppressWarnings("unchecked")
        InjectTask<IN> task = SecurityActions.newInstance(InjectTask.class);
        task.setExecutionService(getExecutionServiceFactoryInstance().getExecutionServiceInstance());

        return task.passToNext(input).then(taskDef);
    }

    public static final class InjectTask<NEXT_IN> extends Task<Object, NEXT_IN> {

        private NEXT_IN nextIn;

        public InjectTask<NEXT_IN> passToNext(NEXT_IN next) {
            this.nextIn = next;
            return this;
        }

        @Override
        protected NEXT_IN process(Object input) throws Exception {
            return nextIn;
        }

    }

}
