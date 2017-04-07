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

/**
 * Registry that contains all available tasks.
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 */
public interface TaskRegistry {

    /**
     * Registers task factory in the registry.
     *
     * @param taskDef
     *     Task created by this factory
     * @param taskFactory
     *     Task fac
     *
     * @throws InvalidTaskException
     */
    <IN, OUT, TASK extends Task<? super IN, OUT>, TASK_FACTORY extends TaskFactory<IN, OUT, TASK>> TaskRegistry register(
        Class<TASK> taskDef, TASK_FACTORY taskFactory) throws InvalidTaskException;

    /**
     * Registers task factory in the registry. Registered task cannot be instantiated by calling {@see
     * TaskFactory#find(Class)}
     * but can be instantiated by aliases provided by factory itself.
     *
     * @throws InvalidTaskException
     */
    <IN, OUT, TASK extends Task<? super IN, OUT>, TASK_FACTORY extends TaskFactory<IN, OUT, TASK>> TaskRegistry register(
        TASK_FACTORY taskFactory) throws InvalidTaskException;

    /**
     * Registers {@link NoArgConstructorTaskFactory} for the task type. Requires task to take no-arg constructor.
     *
     * @param aliases
     *     Optional aliases for the task
     *
     * @throws InvalidTaskException
     */
    <IN, OUT, TASK extends Task<? super IN, OUT>> TaskRegistry register(Class<TASK> taskType, String... aliases)
        throws InvalidTaskException;

    /**
     * Finds task by its type.
     *
     * @throws InvalidTaskException
     */
    <IN, OUT, TASK extends Task<? super IN, OUT>> TASK find(Class<TASK> taskType) throws InvalidTaskException;

    /**
     * Finds task by its alias.
     * <p>
     * This method should be used only by metadata bindings.
     *
     * @param alias
     *     the alias
     */
    Task<?, ?> find(String alias) throws InvalidTaskException;

    /**
     * Finds task by its alias.
     *
     * @param alias
     *     the alias
     * @param inType
     *     the input type of the task
     * @param outType
     *     the output type of the task
     *
     * @throws InvalidTaskException
     *     in case that task input/output type is different
     */
    <IN, OUT> Task<IN, OUT> find(String alias, Class<IN> inType, Class<OUT> outType) throws InvalidTaskException;
}
