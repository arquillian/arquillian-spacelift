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
package org.arquillian.spacelift.execution.impl;

import java.io.File;
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

import org.arquillian.spacelift.execution.Execution;
import org.arquillian.spacelift.execution.ExecutionCondition;
import org.arquillian.spacelift.execution.ExecutionService;
import org.arquillian.spacelift.execution.TimeoutExecutionException;

/**
 * Executor service which is able to execute external process as well as callables
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class ExecutionServiceImpl implements ExecutionService {

    private Map<String, String> environment;
    private File workingDirectory;
    private final ExecutorService service;
    private final ScheduledExecutorService scheduledService;

    public ExecutionServiceImpl() {
        this.service = Executors.newCachedThreadPool();
        this.scheduledService = Executors.newScheduledThreadPool(1);
        this.environment = new HashMap<String, String>();
    }

    @Override
    public ExecutionService setEnvironment(Map<String, String> environment) throws IllegalStateException {
        if (environment == null) {
            throw new IllegalStateException(
                "Environment properies map must not be null!");
        }
        this.environment = environment;
        return this;
    }

    @Override
    public ExecutionService setWorkingDirectory(String workingDirectory) throws IllegalArgumentException {
        if (workingDirectory == null) {
            this.workingDirectory = null;
            return this;
        }
        return setWorkingDirectory(new File(workingDirectory));
    }

    @Override
    public ExecutionService setWorkingDirectory(File workingDirectory) throws IllegalArgumentException {
        if (workingDirectory == null) {
            this.workingDirectory = null;
            return this;
        }
        if (!workingDirectory.exists()) {
            throw new IllegalArgumentException("Specified path " + workingDirectory.getAbsolutePath() + " does not exist!");
        }
        if (!workingDirectory.isDirectory()) {
            throw new IllegalArgumentException("Specified path " + workingDirectory.getAbsolutePath() + " is not a directory!");
        }

        this.workingDirectory = workingDirectory;
        return this;
    }

    @Override
    public Map<String, String> getEnvironment() {
        return Collections.unmodifiableMap(environment);
    }

    @Override
    public File getWorkingDirectory() {
        return workingDirectory;
    }

    @Override
    public <T> Execution<T> execute(Callable<T> task) throws org.arquillian.spacelift.execution.ExecutionException {
        Future<T> future = service.submit(task);
        return new FutureBasedExecution<T>(future);
    }

    @Override
    public <T> T repeat(Callable<T> task, ExecutionCondition<Future<T>> breakCondition, long step, long timeout, TimeUnit unit)
        throws TimeoutExecutionException, org.arquillian.spacelift.execution.ExecutionException {

        CountDownWatch countdown = new CountDownWatch(timeout, unit);
        while (countdown.timeLeft() > 0) {
            // delay by step
            ScheduledFuture<T> future = scheduledService.schedule(task, step, unit);

            Boolean result = false;
            try {
                // wait for true up to timeLeft
                // this means we might get less steps then timeout/step
                result = breakCondition.timeLeft(countdown.timeLeft(), unit).satisfiedBy(future);
                if (result == true) {
                    return future.get();
                }
            } catch (org.arquillian.spacelift.execution.ExecutionException e) {
                continue;
            }
            // rewrap exception
            catch (ExecutionException e) {
                throw new org.arquillian.spacelift.execution.ExecutionException(e.getCause() != null ? e.getCause() : e,
                    e.getMessage());
            } catch (InterruptedException e) {
                throw new org.arquillian.spacelift.execution.ExecutionException(e.getCause() != null ? e.getCause() : e,
                    e.getMessage());
            }
        }

        throw new TimeoutExecutionException("Unable to trigger condition within {0} {1}.", timeout, unit);
    }

    /*
     * @Override
     * public ProcessExecution spawn(ExecutionInteraction interaction, Command command) throws ProcessExecutionException {
     * try {
     * Future<Process> processFuture = service.submit(new SpawnedProcess(environment,
     * workingDirectory,
     * true,
     * command.getFullCommand()));
     * Process process = processFuture.get();
     * ProcessExecution execution = new ProcessExecutionImpl(process, command.getProgramName());
     * service.submit(new ProcessOutputConsumer(execution, interaction));
     * shutdownThreads.addHookFor(execution);
     * return execution;
     * }
     * // rewrap exception
     * catch (InterruptedException e) {
     * throw new ProcessExecutionException(e.getCause() != null ? e.getCause() : e, "Spawning \"{0}\": {1}", new Object[] {
     * e.getMessage(),
     * command });
     * } catch (ExecutionException e) {
     * throw new ProcessExecutionException(e.getCause() != null ? e.getCause() : e, "Spawning \"{0}\": {1}", new Object[] {
     * e.getMessage(),
     * command });
     * }
     * }
     *
     * @Override
     * public ProcessExecution execute(ExecutionInteraction interaction, Command command) throws ProcessExecutionException {
     * Process process = null;
     * try {
     * Future<Process> processFuture = service.submit(new SpawnedProcess(environment,
     * workingDirectory,
     * true,
     * command.getFullCommand()));
     * process = processFuture.get();
     * Future<ProcessExecution> executionFuture = service.submit(new ProcessOutputConsumer(new ProcessExecutionImpl(process,
     * command.getProgramName()),
     * interaction));
     * // wait for process to finish
     * process.waitFor();
     * // wait for process to finish IO
     * ProcessExecution execution = executionFuture.get();
     * if (execution.executionFailed()) {
     * throw new ProcessExecutionException("Invocation of \"{0}\" failed with {1}", new Object[] { command,
     * execution.getExitCode() });
     * }
     * return execution;
     * }
     * // rewrap exception
     * catch (InterruptedException e) {
     * throw new ProcessExecutionException(e.getCause() != nnew Callable<T>() {
     *
     * @Override
     * public T call() throws Exception {
     * return task.workload();
     * }
     * }ull ? e.getCause() : e,
     * "Executing \"{0}\": {1}",
     * new Object[] {
     * e.getMessage(),
     * command });
     * } catch (ExecutionException e) {
     * throw new ProcessExecutionException(e.getCause() != null ? e.getCause() : e,
     * "Executing \"{0}\": {1}",
     * new Object[] {
     * e.getMessage(),
     * command });
     * } finally {
     * // cleanup
     * if (process != null) {
     * InputStream in = process.getInputStream();
     * InputStream err = process.getErrorStream();
     * OutputStream out = process.getOutputStream();
     * if (in != null) {
     * try {
     * in.close();
     * } catch (IOException ignore) {
     * }
     * }
     * if (out != null) {
     * try {
     * out.close();
     * } catch (IOException ignore) {
     * }
     * }
     * if (err != null) {
     * try {
     * err.close();
     * } catch (IOException ignore) {
     * }
     * }
     * // just in case, something went wrong
     * process.destroy();
     * }
     *
     * }
     * }
     *
     *
     * private static class SpawnedProcess implements Callable<Process> {
     *
     * private final List<String> command;
     * private final File workingDirectory;
     * private boolean redirectErrorStream;
     * private Map<String, String> env;
     *
     * public SpawnedProcess(Map<String, String> env, File workingDirectory, boolean redirectErrorStream, List<String> command)
     * {
     * this.env = env;
     * this.workingDirectory = workingDirectory;
     * this.redirectErrorStream = redirectErrorStream;
     * this.command = command;
     * }
     *
     * @Override
     * public Process call() throws Exception {
     * ProcessBuilder builder = new ProcessBuilder(command);
     * builder.directory(workingDirectory);
     * builder.environment().putAll(env);
     * builder.redirectErrorStream(redirectErrorStream);
     * return builder.start();
     * }
     *
     * }
     */

}
