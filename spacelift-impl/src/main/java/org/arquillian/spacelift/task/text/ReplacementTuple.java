/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
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
package org.arquillian.spacelift.task.text;


/**
 * A class for making the API of {@link StringReplacementTask} and {@link StringReplacementTool} fluent.
 *
 * @param <PARENT> Preferably the type of the parent, but can be anything to help with the fluent API.
 */
public class ReplacementTuple<PARENT> {
    private final PARENT parent;

    private String regex;
    private String replacement;

    /**
     * @param parent Should be an instance of the creator. It will be returned when replacement is set in
     *               {@link #with(String)}. Its purpose is to allow for making the API fluent.
     * @param regex  Regex to be used for matching.
     */
    public ReplacementTuple(PARENT parent, String regex) {
        this.parent = parent;
        this.regex = regex;
    }

    /**
     * Sets the replacement for the given regex.
     *
     * @param replacement If null, it will be ignored by the replacement task.
     * @return The instance of {@link PARENT} given in the constructor.
     */
    public PARENT with(String replacement) {
        this.replacement = replacement;
        return parent;
    }

    /**
     * Returns the regex used for matching.
     */
    public String getRegex() {
        return regex;
    }

    /**
     * Returns the string to be used for the replacement.
     */
    public String getReplacement() {
        return replacement;
    }
}