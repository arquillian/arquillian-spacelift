package org.arquillian.spacelift.process.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arquillian.spacelift.execution.Execution;
import org.arquillian.spacelift.execution.Task;
import org.arquillian.spacelift.execution.impl.SentenceImpl;
import org.arquillian.spacelift.process.Answer;
import org.arquillian.spacelift.process.ExecutionInteraction;
import org.arquillian.spacelift.process.OutputTransformer;
import org.arquillian.spacelift.process.ProcessDetails;
import org.arquillian.spacelift.process.ProcessNamePrefixOutputTransformer;
import org.arquillian.spacelift.process.Sentence;

class ConsumeProcessOutputTask extends Task<Execution<Process>, ProcessDetails> {

    private static final Logger log = Logger.getLogger(ConsumeProcessOutputTask.class.getName());

    private ExecutionInteraction interaction;
    private String programName;

    public ConsumeProcessOutputTask programName(String programName) {
        this.programName = programName;
        return this;
    }

    public ConsumeProcessOutputTask interaction(ExecutionInteraction interaction) {
        this.interaction = interaction;

        // FIXME there should be a better way how to propagate process name
        OutputTransformer transformer = interaction.outputTransformer();
        if (transformer instanceof ProcessNamePrefixOutputTransformer) {
            ((ProcessNamePrefixOutputTransformer) transformer).setProcessName(programName);
        }

        return this;
    }

    @Override
    protected ProcessDetails process(Execution<Process> runningProcess) throws Exception {

        Process process = runningProcess.await();

        ProcessDetails details = new ProcessDetailsImpl(process, programName);
        final InputStream stream = details.getStdoutAndStdErr();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        // close process input stream if we don't need it
        // closed input stream is a requirement for process not to hang on windows
        if (!interaction.requiresInputInteraction()) {
            try {
                details.getStdin().close();
            } catch (IOException ignore) {
            }
        }

        try {
            // read character by character
            int i;
            boolean reachedEOF = false;
            Sentence sentence = new SentenceImpl();
            // we have an extra check to figure out whether EOF was reached - using last expected response
            while (!reachedEOF && (i = reader.read()) != -1) {
                // add the character
                sentence.append((char) i);

                Answer answer = interaction.repliesTo(sentence);
                sentence.append(answer);
                answer.reply(runningProcess);
                reachedEOF = runningProcess.isMarkedAsFinished();

                // save and print output
                if (sentence.isFinished()) {
                    sentence.trim();
                    log.log(Level.FINEST, "({0}): {1}", new Object[] { details.getProcessName(), sentence });

                    details.appendOutput(sentence);
                    // propagate output/error to user
                    if (interaction.shouldOutput(sentence)) {
                        System.out.println(interaction.outputTransformer().transform(sentence));
                    }
                    if (interaction.shouldOutputToErr(sentence)) {
                        System.err.println(interaction.outputTransformer().transform(sentence));
                    }
                    sentence.reset();
                }
            }

            // handle last line
            if (!sentence.isEmpty()) {
                log.log(Level.FINEST, "{0} outputs: {1}", new Object[] { details.getProcessName(), sentence });

                details.appendOutput(sentence);
                // propagate output/error to user
                if (interaction.shouldOutput(sentence)) {
                    System.out.println(interaction.outputTransformer().transform(sentence));
                }
                if (interaction.shouldOutputToErr(sentence)) {
                    System.err.println(interaction.outputTransformer().transform(sentence));
                }

            }
        } catch (IOException ignore) {
        }

        try {
            OutputStream os = details.getStdin();
            if (os != null) {
                os.close();
            }
        } catch (IOException ignore) {
        }

        return details;
    }

}
