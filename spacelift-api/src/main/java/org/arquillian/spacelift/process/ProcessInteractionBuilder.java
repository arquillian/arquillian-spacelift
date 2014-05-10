/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat Middleware LLC, and individual contributors
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
package org.arquillian.spacelift.process;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Builder API for process interaction. It uses regular expression to match allowed and error output.
 *
 * @see ProcessInteraction
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class ProcessInteractionBuilder {

    /**
     * No interaction instance
     */
    public static final ProcessInteraction NO_INTERACTION = new ProcessInteractionBuilder().build();

    private String textTypedIn;

    private Map<Pattern, String> replyMap;

    private List<Pattern> allowedOutput;

    private List<Pattern> errorOutput;

    private List<Pattern> terminatingOutput;

    private OutputTransformer transformer;

    private Pattern lastPattern;

    /**
     * Definition of allowed actions when process starts
     *
     * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
     *
     */
    public class StartingProcessInteractionBuilder {

        /**
         * Types in the {@code sentence} when process is started
         *
         * @param sentence the sentence
         * @return current instance to allow chaining
         */
        public ProcessInteractionBuilder typeIn(String sentence) {
            textTypedIn = sentence;
            return ProcessInteractionBuilder.this;
        }
    }

    /**
     * Definition of allowed actions when output is matched
     *
     * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
     *
     */
    public class MatchedOutputProcessInteractionBuilder {

        /**
         * Prints the {@code response} to stdin of the process
         *
         * @param response the response
         * @return current instance to allow chaining
         */
        public ProcessInteractionBuilder replyWith(String response) {
            replyMap.put(lastPattern, response);
            return ProcessInteractionBuilder.this;
        }

        /**
         * Forces current process to terminate
         *
         * @return current instance to allow chaining
         */
        public ProcessInteractionBuilder terminate() {
            terminatingOutput.add(lastPattern);
            return ProcessInteractionBuilder.this;
        }

        /**
         * Echoes the line to standard output of the process running Spacelift
         *
         * @return current instance to allow chaining
         */
        public ProcessInteractionBuilder printToOut() {
            allowedOutput.add(lastPattern);
            return ProcessInteractionBuilder.this;
        }

        /**
         * Echoes the line to error output of the process running Spacelift
         *
         * @return current instance to allow chaining
         */
        public ProcessInteractionBuilder printToErr() {
            errorOutput.add(lastPattern);
            return ProcessInteractionBuilder.this;
        }

    }

    /**
     * Creates empty interaction builder
     */
    public ProcessInteractionBuilder() {
        this.replyMap = new LinkedHashMap<Pattern, String>();
        this.allowedOutput = new ArrayList<Pattern>();
        this.errorOutput = new ArrayList<Pattern>();
        this.terminatingOutput = new ArrayList<Pattern>();
        this.transformer = null;
    }

    /**
     * Defines an interaction when {@code pattern} is matched
     *
     * @param pattern the line
     * @return current instance to allow chaining
     */
    public MatchedOutputProcessInteractionBuilder when(String pattern) {
        this.lastPattern = Pattern.compile(pattern);
        return new MatchedOutputProcessInteractionBuilder();
    }

    /**
     * Defines an interaction when process is started
     *
     * @return current instance to allow chaining
     */
    public StartingProcessInteractionBuilder whenStarts() {
        return new StartingProcessInteractionBuilder();
    }

    /**
     * Defines a prefix for standard output and standard error output. Might be {@code null} or empty string, in such case no
     * prefix is added and process outputs cannot be distinguished
     *
     * @param prefix the prefix
     * @return current instance to allow chaining
     */
    public ProcessInteractionBuilder outputPrefix(final String prefix) {
        if (prefix == null || "".equals(prefix)) {
            this.transformer = new OutputTransformer() {
                @Override
                public Sentence transform(Sentence output) {
                    return output;
                }
            };
        }
        else {
            // sets prefix output transformer
            this.transformer = new OutputTransformer() {
                @Override
                public Sentence transform(Sentence output) {
                    return output.prepend(prefix);
                }
            };
        }
        return this;
    }

    /**
     * Builds {@link ProcessInteraction} object from defined data
     *
     * @return {@link ProcessInteraction}
     */
    public ProcessInteraction build() {
        return new ProcessInteractionImpl(replyMap, transformer, allowedOutput, errorOutput, terminatingOutput, textTypedIn);
    }

    private static class ProcessInteractionImpl implements ProcessInteraction {

        private final String textTypedIn;

        private final Map<Pattern, String> replyMap;

        private final List<Pattern> allowedOutput;

        private final List<Pattern> errorOutput;

        private final List<Pattern> terminatingOutput;

        private final OutputTransformer transformer;

        public ProcessInteractionImpl(Map<Pattern, String> replyMap, OutputTransformer outputTransformer, List<Pattern> allowedOutput,
            List<Pattern> errorOutput, List<Pattern> terminatingOutput, String textTypedIn) {
            this.replyMap = replyMap;
            this.transformer = outputTransformer;
            this.allowedOutput = allowedOutput;
            this.errorOutput = errorOutput;
            this.terminatingOutput = terminatingOutput;
            this.textTypedIn = textTypedIn;
        }

        @Override
        public List<Pattern> allowedOutput() {
            return allowedOutput;
        }

        @Override
        public List<Pattern> errorOutput() {
            return errorOutput;
        }

        @Override
        public Map<Pattern, String> replyMap() {
            return replyMap;
        }

        @Override
        public List<Pattern> terminatingOutput() {
            return terminatingOutput;
        }

        @Override
        public String textTypedIn() {
            return textTypedIn;
        }

        @Override
        public OutputTransformer transformer() {
            return transformer;
        }

    }
}
