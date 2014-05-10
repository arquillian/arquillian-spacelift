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
package org.arquillian.spacelift.process.impl;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.arquillian.spacelift.execution.Execution;
import org.arquillian.spacelift.execution.ExecutionException;
import org.arquillian.spacelift.execution.Task;
import org.arquillian.spacelift.process.OutputTransformer;
import org.arquillian.spacelift.process.ProcessInteraction;
import org.arquillian.spacelift.process.ProcessResult;
import org.arquillian.spacelift.process.Sentence;

/**
 * An internal task that consumes process I/O and uses {@see ProcessInteraction} to communicate with the process
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
class ConsumeProcessOutputTask extends Task<Execution<Process>, ProcessResult> {

    private static final Logger log = Logger.getLogger(ConsumeProcessOutputTask.class.getName());

    private ProcessInteraction interactionDefinition;
    private String programName;

    public ConsumeProcessOutputTask programName(String programName) {
        this.programName = programName;
        return this;
    }

    public ConsumeProcessOutputTask interaction(ProcessInteraction interaction) {
        this.interactionDefinition = interaction;
        return this;
    }

    @Override
    protected ProcessResult process(Execution<Process> runningProcess) throws Exception {

        Process process = runningProcess.await();

        final List<String> output = new ArrayList<String>();
        final ProcessResult result = new ProcessResultImpl(process, programName, output);
        final ProcessInteractionApplicator interaction = new ProcessInteractionApplicator(interactionDefinition, programName);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        final OutputStream writer = new BufferedOutputStream(process.getOutputStream());

        // close process input stream if we don't need it
        // closed input stream is a requirement for process not to hang on windows
        if (!interaction.requiresInputInteraction()) {
            try {
                writer.close();
            } catch (IOException ignore) {
            }
        }

        try {
            // read character by character
            int i;
            boolean reachedEOF = false;

            // write initial text if any
            if (interaction.typesInitialText() != null) {
                writer.flush();
                writer.write(interaction.typesInitialText().getBytes());
                writer.flush();
                output.add(interaction.typesInitialText());
            }

            Sentence sentence = new SentenceImpl();
            // we have an extra check to figure out whether EOF was reached - using last expected response
            while (!reachedEOF && (i = reader.read()) != -1) {
                // add the character
                sentence.append((char) i);

                boolean shouldTerminate = interaction.shouldTerminate(sentence);
                String answer = interaction.repliesTo(sentence);
                // sentence was not empty, reply
                if (answer != null) {
                    sentence.append(answer);
                    writer.flush();
                    writer.write(answer.getBytes());
                    writer.flush();
                }
                if (shouldTerminate) {
                    runningProcess.markAsFinished();
                    runningProcess.terminate();
                }

                reachedEOF = runningProcess.isMarkedAsFinished();

                // save and print output
                if (sentence.isFinished()) {
                    sentence.trim();
                    log.log(Level.FINEST, "({0}): {1}", new Object[] { result.processName(), sentence });

                    output.add(sentence.toString());
                    // propagate output/error to user
                    if (interaction.shouldOutput(sentence)) {
                        System.out.println(interaction.transform(sentence));
                    }
                    if (interaction.shouldOutputToErr(sentence)) {
                        System.err.println(interaction.transform(sentence));
                    }
                    sentence.reset();
                }
            }

            // handle last line
            if (!sentence.isEmpty()) {
                log.log(Level.FINEST, "{0} outputs: {1}", new Object[] { result.processName(), sentence });

                output.add(sentence.toString());
                // propagate output/error to user
                if (interaction.shouldOutput(sentence)) {
                    System.out.println(interaction.transform(sentence));
                }
                if (interaction.shouldOutputToErr(sentence)) {
                    System.err.println(interaction.transform(sentence));
                }

            }
        } catch (IOException ignore) {
        }

        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException ignore) {
        }

        // let's wait for process to finish. It should be already finished or terminated at this moment
        try {
            process.waitFor();
        }
        // rewrap exception
        catch (InterruptedException e) {
            throw new ExecutionException(e.getCause() != null ? e.getCause() : e,
                "Execution of \"{0}\" was interrupted with: {1}",
                new Object[] {
                    programName, e.getMessage() });
        } finally {
            // cleanup
            if (process != null) {
                InputStream in = process.getInputStream();
                InputStream err = process.getErrorStream();
                OutputStream out = process.getOutputStream();
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ignore) {
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException ignore) {
                    }
                }
                if (err != null) {
                    try {
                        err.close();
                    } catch (IOException ignore) {
                    }
                }
                // just in case, something went wrong
                process.destroy();
            }
        }

        return result;
    }

    /**
     * Applicator of ProcessInteraction to the currently running process
     *
     * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
     *
     */
    private static class ProcessInteractionApplicator {

        final ProcessInteraction interaction;
        final OutputTransformer transformer;

        public ProcessInteractionApplicator(final ProcessInteraction interaction, final String processName) {
            this.interaction = interaction;
            if (interaction.transformer() == null) {
                // add process name transformer if no transformer was defined
                this.transformer = new OutputTransformer() {
                    @Override
                    public Sentence transform(Sentence output) {
                        return output.prepend("):").prepend(processName).prepend("(");
                    }
                };
            }
            else {
                this.transformer = interaction.transformer();
            }
        }

        public String typesInitialText() {
            return interaction.textTypedIn();
        }

        public String repliesTo(Sentence sentence) {
            for (Map.Entry<Pattern, String> entry : interaction.replyMap().entrySet()) {
                if (entry.getKey().matcher(sentence).matches()) {
                    return entry.getValue();
                }
            }
            return null;
        }

        public boolean shouldTerminate(Sentence sentence) {
            for (Pattern p : interaction.terminatingOutput()) {
                if (p.matcher(sentence).matches()) {
                    return true;
                }
            }
            return false;
        }

        public boolean shouldOutput(Sentence sentence) {
            for (Pattern p : interaction.allowedOutput()) {
                if (p.matcher(sentence).matches()) {
                    return true;
                }
            }
            return false;
        }

        public Sentence transform(Sentence original) {
            return transformer.transform(original);
        }

        public boolean shouldOutputToErr(Sentence sentence) {
            for (Pattern p : interaction.errorOutput()) {
                if (p.matcher(sentence).matches()) {
                    return true;
                }
            }
            return false;
        }

        public boolean requiresInputInteraction() {
            return !interaction.replyMap().isEmpty() || (interaction.textTypedIn() != null && interaction.textTypedIn() != "");
        }
    }

}
