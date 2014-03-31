/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arquillian.spacelift.process.Answer;
import org.arquillian.spacelift.process.Command;
import org.arquillian.spacelift.process.CommandBuilder;
import org.arquillian.spacelift.process.OutputTransformer;
import org.arquillian.spacelift.process.ProcessExecution;
import org.arquillian.spacelift.process.ProcessExecutionException;
import org.arquillian.spacelift.process.ProcessExecutor;
import org.arquillian.spacelift.process.ProcessInteraction;
import org.arquillian.spacelift.process.ProcessInteractionBuilder;
import org.arquillian.spacelift.process.ProcessNamePrefixOutputTransformer;
import org.arquillian.spacelift.process.Sentence;

/**
 * Executor service which is able to execute external process as well as callables
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class ProcessExecutorImpl implements ProcessExecutor {

    private Map<String, String> environment;
    private File workingDirectory;
    private final ShutDownThreadHolder shutdownThreads;
    private final ExecutorService service;
    private final ScheduledExecutorService scheduledService;

    public ProcessExecutorImpl() {
        this.shutdownThreads = new ShutDownThreadHolder();
        this.service = Executors.newCachedThreadPool();
        this.scheduledService = Executors.newScheduledThreadPool(1);
        this.environment = new HashMap<String, String>();
    }

    @Override
    public ProcessExecutor setEnvironment(Map<String, String> environment) throws IllegalStateException {
        if (environment == null) {
            throw new IllegalStateException(
                "Environment properies map must not be null!");
        }
        this.environment = environment;
        return this;
    }

    @Override
    public ProcessExecutor setWorkingDirectory(String workingDirectory) throws IllegalArgumentException {
        if(workingDirectory == null) {
            this.workingDirectory = null;
            return this;
        }
        return setWorkingDirectory(new File(workingDirectory));
    }

    @Override
    public ProcessExecutor setWorkingDirectory(File workingDirectory) throws IllegalArgumentException {
        if(workingDirectory == null) {
            this.workingDirectory = null;
            return this;
        }
        if(!workingDirectory.exists()) {
            throw new IllegalArgumentException("Specified path " + workingDirectory.getAbsolutePath() + " does not exist!");
        }
        if (!workingDirectory.isDirectory()) {
            throw new IllegalArgumentException("Specified path " + workingDirectory.getAbsolutePath() + " is not a directory!");
        }

        this.workingDirectory = workingDirectory;
        return this;
    }

    @Override
    public <T> Future<T> submit(Callable<T> callable) {
        return service.submit(callable);
    }

    @Override
    public Boolean scheduleUntilTrue(Callable<Boolean> callable, long timeout, long step, TimeUnit unit)
        throws ProcessExecutionException {

        CountDownWatch countdown = new CountDownWatch(timeout, unit);
        while (countdown.timeLeft() > 0) {
            // delay by step
            ScheduledFuture<Boolean> future = scheduledService.schedule(callable, step, unit);
            Boolean result = false;
            try {
                // wait for true up to timeLeft
                // this means we might get less steps then timeout/step
                result = future.get(countdown.timeLeft(), unit);
                if (result == true) {
                    return true;
                }
            } catch (TimeoutException e) {
                continue;
            }
            // rewrap exception
            catch (ExecutionException e) {
                throw new ProcessExecutionException(e.getMessage(), e.getCause() != null ? e.getCause() : e);
            } catch (InterruptedException e) {
                throw new ProcessExecutionException(e.getMessage(), e.getCause() != null ? e.getCause() : e);
            }
        }

        return false;
    }

    @Override
    public ProcessExecution spawn(ProcessInteraction interaction, Command command) throws ProcessExecutionException {
        try {
            Future<Process> processFuture = service.submit(new SpawnedProcess(environment, workingDirectory, true, command.getAsArray()));
            Process process = processFuture.get();
            ProcessExecution execution = new ProcessExecutionImpl(process, command.getFirst());
            service.submit(new ProcessOutputConsumer(execution, interaction));
            shutdownThreads.addHookFor(execution);
            return execution;
        }
        // rewrap exception
        catch (InterruptedException e) {
            throw new ProcessExecutionException(e.getCause() != null ? e.getCause() : e, "Spawning \"{0}\": {1}", new Object[] {
                e.getMessage(),
                command });
        } catch (ExecutionException e) {
            throw new ProcessExecutionException(e.getCause() != null ? e.getCause() : e, "Spawning \"{0}\": {1}", new Object[] {
                e.getMessage(),
                command });
        }
    }

    @Override
    public ProcessExecution execute(ProcessInteraction interaction, Command command) throws ProcessExecutionException {
        Process process = null;
        try {
            Future<Process> processFuture = service.submit(new SpawnedProcess(environment, workingDirectory, true, command.getAsArray()));
            process = processFuture.get();
            Future<ProcessExecution> executionFuture = service.submit(new ProcessOutputConsumer(new ProcessExecutionImpl(process,
                command.getFirst()),
                interaction));
            // wait for process to finish
            process.waitFor();
            // wait for process to finish IO
            ProcessExecution execution = executionFuture.get();
            if (execution.executionFailed()) {
                throw new ProcessExecutionException("Invocation of \"{0}\" failed with {1}", new Object[] { command,
                    execution.getExitCode() });
            }
            return execution;
        }
        // rewrap exception
        catch (InterruptedException e) {
            throw new ProcessExecutionException(e.getCause() != null ? e.getCause() : e, "Executing \"{0}\": {1}", new Object[] {
                e.getMessage(),
                command });
        } catch (ExecutionException e) {
            throw new ProcessExecutionException(e.getCause() != null ? e.getCause() : e, "Executing \"{0}\": {1}", new Object[] {
                e.getMessage(),
                command });
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
    }

    @Override
    public ProcessExecution execute(ProcessInteraction interaction, String[] command) throws ProcessExecutionException {
        return execute(interaction, new CommandBuilder().add(command).build());
    }

    @Override
    public ProcessExecution execute(Command command) throws ProcessExecutionException {
        return execute(ProcessInteractionBuilder.NO_INTERACTION, command);
    }

    @Override
    public ProcessExecution execute(String... command) throws ProcessExecutionException {
        return execute(new CommandBuilder().add(command).build());
    }

    @Override
    public ProcessExecution spawn(ProcessInteraction interaction, String[] command) throws ProcessExecutionException {
        return spawn(interaction, new CommandBuilder().add(command).build());
    }

    @Override
    public ProcessExecution spawn(Command command) throws ProcessExecutionException {
        return spawn(ProcessInteractionBuilder.NO_INTERACTION, command);
    }

    @Override
    public ProcessExecution spawn(String... command) throws ProcessExecutionException {
        return spawn(new CommandBuilder().add(command).build());
    }

    @Override
    public ProcessExecutor removeShutdownHook(ProcessExecution p) {
        shutdownThreads.removeHookFor(p);
        return this;
    }

    private static class ShutDownThreadHolder {

        private final Map<ProcessExecution, Thread> shutdownThreads;

        public ShutDownThreadHolder() {
            this.shutdownThreads = Collections.synchronizedMap(new HashMap<ProcessExecution, Thread>());
        }

        public void addHookFor(final ProcessExecution p) {
            Thread shutdownThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (p != null) {
                        p.terminate();
                    }
                }
            });
            Runtime.getRuntime().addShutdownHook(shutdownThread);
            shutdownThreads.put(p, shutdownThread);
        }

        public void removeHookFor(final ProcessExecution p) {
            shutdownThreads.remove(p);
        }
    }

    private static class SpawnedProcess implements Callable<Process> {

        private final String[] command;
        private final File workingDirectory;
        private boolean redirectErrorStream;
        private Map<String, String> env;

        public SpawnedProcess(Map<String, String> env, File workingDirectory, boolean redirectErrorStream, String[] command) {
            this.env = env;
            this.workingDirectory = workingDirectory;
            this.redirectErrorStream = redirectErrorStream;
            this.command = command;
        }

        @Override
        public Process call() throws Exception {
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(workingDirectory);
            builder.environment().putAll(env);
            builder.redirectErrorStream(redirectErrorStream);
            return builder.start();
        }

    }

    /**
     * Runnable that consumes the output of the process.
     *
     * @author Stuart Douglas
     * @author Karel Piwko
     */
    static class ProcessOutputConsumer implements Callable<ProcessExecution> {

        private static final Logger log = Logger.getLogger(ProcessOutputConsumer.class.getName());

        private final ProcessExecution execution;
        private final ProcessInteraction interaction;

        public ProcessOutputConsumer(ProcessExecution execution, ProcessInteraction interaction) {
            this.execution = execution;
            this.interaction = interaction;

            // FIXME there should be a better way how to propagate process name
            OutputTransformer transformer = interaction.outputTransformer();
            if(transformer instanceof ProcessNamePrefixOutputTransformer) {
                ((ProcessNamePrefixOutputTransformer) transformer).setProcessName(execution.getProcessName());
            }

        }

        @Override
        public ProcessExecution call() throws Exception {
            final InputStream stream = execution.getStdoutAndStdErr();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            // close process input stream if we don't need it
            // closed input stream is a requirement for process not to hang on windows
            if (!interaction.requiresInputInteraction()) {
                try {
                    execution.getStdin().close();
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
                    answer.reply(execution);
                    reachedEOF = execution.isMarkedAsFinished();

                    // save and print output
                    if (sentence.isFinished()) {
                        sentence.trim();
                        log.log(Level.FINEST, "({0}): {1}", new Object[] { execution.getProcessName(), sentence });

                        execution.appendOutput(sentence);
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
                    log.log(Level.FINEST, "{0} outputs: {1}", new Object[] { execution.getProcessName(), sentence });

                    execution.appendOutput(sentence);
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
                OutputStream os = execution.getStdin();
                if (os != null) {
                    os.close();
                }
            } catch (IOException ignore) {
            }

            return execution;
        }
    }

}
