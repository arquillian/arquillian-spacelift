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
package org.arquillian.nativeplatform.process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Builds {@link Command}s.
 *
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class CommandBuilder {

    private List<String> command;

    public CommandBuilder() {
        this.command = new ArrayList<String>();
    }

    /**
     * Adds a list of tokens to the command under construction, ignoring null and empty tokens.
     *
     * @param tokens tokens we are adding to the already existing list
     * @return instance of this {@link CommandBuilder}
     */
    public CommandBuilder add(List<String> tokens) {
        for (String token : tokens) {
            add(token);
        }
        return this;
    }

    /**
     * Adds tokens to the command under construction, ignoring null and empty tokens.
     *
     * @param tokens
     * @return instance of this {@link CommandBuilder}
     */
    public CommandBuilder add(String... tokens) {
        return add(Arrays.asList(tokens));
    }

    /**
     * Adds a token to the command under construction, ignoring null and empty token.
     *
     * @param token token to add to the command list
     * @return instance of this {@link CommandBuilder}
     */
    public CommandBuilder add(String token) {
        if (token != null && token.length() > 0) {
            command.add(token);
        }
        return this;
    }

    /**
     * @param stringToBeParsed
     * @return instance of this {@link CommandBuilder}
     * @see StringUtils#tokenize(String)
     * @return instance of this {@link CommandBuilder}
     */
    public CommandBuilder addTokenized(String stringToBeParsed) {
        return add(StringUtils.tokenize(stringToBeParsed));
    }

    /**
     * Clears the command - all added tokens are removed.
     *
     * @return instance of this {@link CommandBuilder}
     */
    public CommandBuilder clear() {
        command.clear();
        return this;
    }

    /**
     * Remove all occurrences of {@code token} from the command list.
     *
     * @param token token to remove
     * @return instance of this {@link CommandBuilder}
     */
    public CommandBuilder remove(String token) {
        if (token == null || token.trim().equals("")) {
            return this;
        }

        command.removeAll(Arrays.asList(token));

        return this;
    }

    /**
     * Builds so-far constructed command, any subsequent call of this method will build command starting from empty command
     * instance.
     *
     * @return built command
     */
    public Command build() {
        CommandImpl command = new CommandImpl(this.command);
        this.command = null;
        this.command = new ArrayList<String>();
        return command;
    }

    private static class CommandImpl implements Command {

        private final List<String> command;

        public CommandImpl(List<String> command) {
            this.command = new ArrayList<String>(command);
        }

        @Override
        public int size() {
            return command.size();
        }

        @Override
        public List<String> getAsList() {
            return command;
        }

        @Override
        public String[] getAsArray() {
            return command.toArray(new String[0]);
        }

        @Override
        public String get(int i) {
            try {
                return command.get(i);
            } catch (IndexOutOfBoundsException ex) {
                return null;
            }
        }

        @Override
        public String getLast() {
            if (command.isEmpty()) {
                return null;
            }
            return command.get(command.size() - 1);
        }

        @Override
        public String getFirst() {
            if (command.isEmpty()) {
                return null;
            }
            return command.get(0);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (String s : command) {
                sb.append(s);
                sb.append(" ");
            }
            return sb.toString().trim();
        }

    }

    static final class StringUtils {

        /**
         * Parse string to tokens. Tokens are separated by whitespace. In case some token contains whitespace, the whole token
         * has to be quoted. For instance string 'opt0 opt1=val1 "opt2=val2 with space"' results in three tokens.
         *
         * @param stringToBeParsed - string to be parsed to tokens
         * @return List of tokens, returns empty list rather that null value
         */
        public static List<String> tokenize(String stringToBeParsed) {

            final String TOKEN = "\"[^\"\\\\]*(?:\\\\.[^\"\\\\]*)*\"|\\S+";
            final String QUOTED_TOKEN = "^\"(.*)\"$";

            List<String> options = new ArrayList<String>();
            if (stringToBeParsed != null && stringToBeParsed.length() != 0) {
                Pattern p = Pattern.compile(TOKEN, Pattern.DOTALL);
                Matcher m = p.matcher(stringToBeParsed);
                while (m.find()) {
                    if (!(m.group().trim().equals(""))) {
                        options.add(Pattern.compile(QUOTED_TOKEN, Pattern.DOTALL).matcher(m.group().trim()).replaceAll("$1"));
                    }
                }
            }
            return options;
        }

    }
}
