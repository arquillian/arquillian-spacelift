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
package org.arquillian.spacelift.tool;

import java.util.Collection;

import org.arquillian.spacelift.execution.Task;

/**
 * Abstraction of a tool. Tool is able to run a command on current platform.
 *
 * Tool can also provide a high level API to work with the command.
 *
 * Tool is supposed to have a no-arg constructor.
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 * @param <TOOLTYPE> Type of the tool
 */
public abstract class Tool<IN, OUT> extends Task<IN, OUT> {

    /**
     * Returns a collection of aliases for this tool. An alias can be used to get a tool
     * from the {@link ToolRegistry}
     *
     * @return
     */
    protected abstract Collection<String> aliases();
}
