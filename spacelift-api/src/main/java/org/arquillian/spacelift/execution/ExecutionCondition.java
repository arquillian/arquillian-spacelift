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

/**
 * A condition to match {@see Execution} result.
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 * @param <RESULT> Type of the execution result
 */
public interface ExecutionCondition<RESULT> {

    /**
     * Evaluates a condition using {@code object}
     *
     * @param object Object to be inspected
     * @return {@code true} if object matches condition, {@code false} otherwise
     * @throws ExecutionException if execution of the result failed
     */
    boolean satisfiedBy(RESULT object) throws ExecutionException;

}
