/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.arquillian.spacelift.process;

import java.io.IOException;

import org.arquillian.spacelift.execution.Execution;

/**
 * Represents an non-interactive user input to a sentence. Answer automatically appends new line character
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class TextAnswer implements Answer {

    private final String answerText;

    public TextAnswer(String answerText) {
        this.answerText = answerText;
    }

    @Override
    public int length() {
        return answerText.length();
    }

    @Override
    public char charAt(int index) {
        return answerText.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return answerText.subSequence(start, end);
    }

    @Override
    public <RETURNTYPE> void reply(Execution<RETURNTYPE> execution) throws IOException {
        // TODO
        // OutputStream ostream = execution.getStdin();
        // ostream.flush();
        // ostream.write(answerText.getBytes());
        // ostream.flush();

    }

    @Override
    public String toString() {
        return answerText;
    }
}
