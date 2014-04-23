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
package org.arquillian.spacelift.process;

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

    private String programName;
    private List<String> parameters;

    private boolean isDaemon;

    /**
     * Creates a command builder with program name and parameters to be executed
     *
     * @param command Program name with subsequent parameters
     * @throws IllegalArgumentException If {@code command} is an empty array
     */
    public CommandBuilder(CharSequence... command) throws IllegalArgumentException {
        if (command.length < 1) {
            throw new IllegalArgumentException("Command must not be empty");
        }
        this.programName = command[0].toString();
        this.parameters = new ArrayList<String>(command.length);
        parameters(Arrays.copyOfRange(command, 1, command.length));
    }

    /**
     * Creates a command builder with program name to be executed.
     *
     * It is not checked whether {@code programName} is a valid command for current operating system
     *
     * @param programName
     */
    public CommandBuilder(CharSequence programName) {
        this.programName = programName.toString();
        this.parameters = new ArrayList<String>();
    }

    /**
     * Adds a list of parameters to the command under construction, ignoring null and empty parameters.
     *
     * @param parameters parameters we are adding to the already existing list
     * @return instance of this {@link CommandBuilder}
     */
    public CommandBuilder parameters(List<? extends CharSequence> parameters) {
        for (CharSequence parameter : parameters) {
            parameter(parameter.toString());
        }
        return this;
    }

    /**
     * Adds parameters to the command under construction, ignoring null and empty parameters.
     *
     * @param parameters
     * @return instance of this {@link CommandBuilder}
     */
    public CommandBuilder parameters(CharSequence... parameters) {
        return parameters(Arrays.asList(parameters));
    }

    /**
     * Adds a parameter to the command under construction, ignoring null and empty parameter.
     *
     * @param parameter parameter to add to the command list
     * @return instance of this {@link CommandBuilder}
     */
    public CommandBuilder parameter(CharSequence parameter) {
        if (parameter != null && parameter.length() > 0) {
            parameters.add(parameter.toString());
        }
        return this;
    }

    /**
     * @param stringToBeParsed
     * @return instance of this {@link CommandBuilder}
     * @return instance of this {@link CommandBuilder}
     */
    public CommandBuilder splitToParameters(CharSequence sequenceToBeParsed) {
        if (sequenceToBeParsed != null && sequenceToBeParsed.length() > 0) {
            parameters(StringUtils.parameterize(sequenceToBeParsed.toString()));
        }
        return this;
    }

    public CommandBuilder runAsDaemon() {
        this.isDaemon = true;
        return this;
    }

    /**
     * Builds so-far constructed command, any subsequent call of this method will build command starting from empty command
     * instance.
     *
     * @return built command
     */
    public Command build() {
        return new CommandImpl(this.programName, this.parameters, isDaemon);
    }

    @Override
    public String toString() {
        return build().toString();
    }

    private static class CommandImpl implements Command {

        private final String programName;
        private final List<String> parameters;
        private final boolean isDaemon;

        public CommandImpl(String programName, List<String> parameters, boolean isDaemon) {
            this.programName = programName;
            this.parameters = new ArrayList<String>(parameters);
            this.isDaemon = isDaemon;
        }

        @Override
        public int getNumberOfParameters() {
            return parameters.size();
        }

        @Override
        public String getParameter(int i) {

            // return program name if 0 is passed
            if (i == 0) {
                return getProgramName();
            }

            // return parameter
            try {
                return parameters.get(i - 1);
            } catch (IndexOutOfBoundsException ex) {
                return null;
            }
        }

        @Override
        public String getProgramName() {
            return programName;
        }

        @Override
        public List<String> getParameters() {
            return new ArrayList<String>(parameters);
        }

        @Override
        public List<String> getFullCommand() {
            List<String> fullCommand = new ArrayList<String>(parameters.size() + 1);
            fullCommand.add(programName);
            fullCommand.addAll(parameters);
            return fullCommand;
        }

        @Override
        public boolean runsAsDeamon() {
            return isDaemon;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            String delimiter = "";

            for (String s : getFullCommand()) {
                sb.append(delimiter);
                // check wheter we should print command in escaped form.
                if (StringUtils.parameterize(s).size() > 1) {
                    sb.append('"').append(s).append('"');
                }
                else {
                    sb.append(s);
                }
                delimiter = " ";
            }
            return sb.toString();
        }

    }

    static final class StringUtils {

        /**
         * Parse string to parameters. Tokens are separated by whitespace. In case some parameter contains whitespace, the whole
         * parameter
         * has to be quoted. For instance string 'opt0 opt1=val1 "opt2=val2 with space"' results in three parameters.
         *
         * @param stringToBeParsed - string to be parsed to parameters
         * @return List of parameters, returns empty list rather that null value
         */
        public static List<String> parameterize(String stringToBeParsed) {

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
