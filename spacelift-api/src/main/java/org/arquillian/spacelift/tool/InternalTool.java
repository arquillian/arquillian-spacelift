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

import java.util.concurrent.Callable;

import org.arquillian.spacelift.process.ProcessExecutor;

/**
 * Representation of the tool that does not require an external command to do the work, that is
 * it is able to do all the work using Java calls
 *
 * @see ProcessExecutor
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 * @param <TOOLTYPE> Type of the tool
 *
 */
public interface InternalTool<TOOLTYPE extends InternalTool<TOOLTYPE, RESULTTYPE>, RESULTTYPE> extends Tool<TOOLTYPE> {

    /**
     * Returns callable representing this internal call
     *
     * @return
     */
    Callable<RESULTTYPE> getCallable();
}
