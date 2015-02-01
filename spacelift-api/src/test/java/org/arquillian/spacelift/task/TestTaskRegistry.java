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
package org.arquillian.spacelift.task;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.arquillian.spacelift.task.InvalidTaskException;
import org.arquillian.spacelift.task.NoArgConstructorTaskFactory;
import org.arquillian.spacelift.task.Task;
import org.arquillian.spacelift.task.TaskFactory;
import org.arquillian.spacelift.task.TaskRegistry;

public class TestTaskRegistry implements TaskRegistry {

    private Map<Class<?>, TaskFactory<?, ?, ?>> classRegistry;
    private Map<String, TaskFactory<?, ?, ?>> aliasRegistry;

    public TestTaskRegistry() {
        this.classRegistry = new ConcurrentHashMap<Class<?>, TaskFactory<?, ?, ?>>();
        this.aliasRegistry = new ConcurrentHashMap<String, TaskFactory<?, ?, ?>>();
    }

    @Override
    public <IN, OUT, TASK extends Task<? super IN, OUT>, TASK_FACTORY extends TaskFactory<IN, OUT, TASK>> TaskRegistry register(
        Class<TASK> taskDef, TASK_FACTORY taskFactory) throws InvalidTaskException {
        for (String alias : taskFactory.aliases()) {
            aliasRegistry.put(alias, taskFactory);
        }

        if(taskDef!=null) {
            classRegistry.put(taskDef, taskFactory);
        }

        return this;
    }

    @Override
    public <IN, OUT, TASK extends Task<? super IN, OUT>, TASK_FACTORY extends TaskFactory<IN, OUT, TASK>> TaskRegistry register(
        TASK_FACTORY taskFactory) throws InvalidTaskException {

        return register(null, taskFactory);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public <IN, OUT, TASK extends Task<? super IN, OUT>> TaskRegistry register(Class<TASK> taskType, String... aliases)
        throws InvalidTaskException {
        return register(taskType, new NoArgConstructorTaskFactory(taskType, aliases));
    }

    @Override
    public <IN, OUT, TASK extends Task<? super IN, OUT>> TASK find(Class<TASK> taskType) throws InvalidTaskException {
        return new NoArgConstructorTaskFactory<IN, OUT, TASK>(taskType).create();
    }

    @Override
    public Task<?, ?> find(String alias) throws InvalidTaskException {

        if (!aliasRegistry.containsKey(alias)) {
            throw new InvalidTaskException("No task with alias {0} was registered.", alias);
        }
        return aliasRegistry.get(alias).create();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <IN, OUT> Task<IN, OUT> find(String alias, Class<IN> inType, Class<OUT> outType) throws InvalidTaskException {

        if (!aliasRegistry.containsKey(alias)) {
            throw new InvalidTaskException("No task with alias {0} was registered.", alias);
        }
        return (Task<IN, OUT>) aliasRegistry.get(alias).create();
    }
}
