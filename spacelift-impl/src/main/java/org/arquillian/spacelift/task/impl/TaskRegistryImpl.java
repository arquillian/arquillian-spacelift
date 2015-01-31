package org.arquillian.spacelift.task.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.arquillian.spacelift.task.InvalidTaskException;
import org.arquillian.spacelift.task.NoArgConstructorTaskFactory;
import org.arquillian.spacelift.task.Task;
import org.arquillian.spacelift.task.TaskFactory;
import org.arquillian.spacelift.task.TaskRegistry;

public class TaskRegistryImpl implements TaskRegistry {

    private final Map<Class<?>, TaskFactory<?, ?, ?>> classRegistry;
    private final Map<String, TaskFactory<?, ?, ?>> aliasRegistry;

    public TaskRegistryImpl() {
        this.classRegistry = new ConcurrentHashMap<Class<?>, TaskFactory<?, ?, ?>>();
        this.aliasRegistry = new ConcurrentHashMap<String, TaskFactory<?, ?, ?>>();
    }

    @Override
    public <IN, OUT, TASK extends Task<? super IN, OUT>, TASK_FACTORY extends TaskFactory<IN, OUT, TASK>> TaskRegistry register(
        Class<TASK> taskDef, TASK_FACTORY taskFactory) throws InvalidTaskException {
        classRegistry.put(taskDef, taskFactory);
        for (String alias : taskFactory.aliases()) {
            aliasRegistry.put(alias, taskFactory);
        }

        return this;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public <IN, OUT, TASK extends Task<? super IN, OUT>> TaskRegistry register(Class<TASK> taskType, String... aliases)
        throws InvalidTaskException {
        return register(taskType, new NoArgConstructorTaskFactory(taskType, aliases));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public <IN, OUT, TASK extends Task<? super IN, OUT>> TASK find(Class<TASK> taskType) throws InvalidTaskException {

        // if there is no such class, try to register default factory
        if (!classRegistry.containsKey(taskType)) {
            register(taskType, new NoArgConstructorTaskFactory(taskType));
        }

        return (TASK) classRegistry.get(taskType).create();
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
