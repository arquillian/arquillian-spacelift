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
package org.arquillian.nativeplatform.process.impl;

import java.io.IOException;

import org.arquillian.nativeplatform.process.Answer;
import org.arquillian.nativeplatform.process.ProcessExecution;

/**
 * Represents an empty or no answer. It simply does nothing at all.
 * 
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public enum NoAnswer implements Answer {

    INSTANCE;

    @Override
    public int length() {
        return 0;
    }

    @Override
    public char charAt(int index) {
        return 0;
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return null;
    }

    @Override
    public void reply(ProcessExecution execution) throws IOException {
        // just do nothing
    }

    @Override
    public String toString() {
        return "";
    }

}
