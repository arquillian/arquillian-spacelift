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

/**
 * Represents a line on execution input or output
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public interface Sentence extends CharSequence {

    /**
     * Prepends a character sequence to sentence
     *
     * @param s sequence
     * @return updated sentence
     */
    Sentence prepend(CharSequence s);

    /**
     * Prepends a character to sentence
     *
     * @param c character
     * @return updated sentence
     */
    Sentence prepend(char c);

    /**
     * Appends a character sequence to sentence
     *
     * @param s sequence
     * @return updated sentence
     */
    Sentence append(CharSequence s);

    /**
     * Appends a character to sentence
     *
     * @param c character
     * @return updated sequence
     */
    Sentence append(char c);

    /**
     * Checks whether sentence is finished by newline character(s)
     *
     * @return
     */
    boolean isFinished();

    /**
     * Checks whether sentence is empty, that is does not contain any characters
     *
     * @return
     */
    boolean isEmpty();

    /**
     * Removes a newline character(s) from the end of sentence, if any
     *
     * @return updated sentence
     */
    Sentence trim();

    /**
     * Clears the sentence
     *
     * @return
     */
    Sentence reset();
}