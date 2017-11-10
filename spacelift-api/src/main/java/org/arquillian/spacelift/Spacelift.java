package org.arquillian.spacelift;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.arquillian.spacelift.Invokable.InvocationException;
import org.arquillian.spacelift.execution.ExecutionService;
import org.arquillian.spacelift.task.InjectTask;
import org.arquillian.spacelift.task.InvalidTaskException;
import org.arquillian.spacelift.task.Task;
import org.arquillian.spacelift.task.TaskRegistry;

/**
 * Arquillian Spacelift
 *
 * @author kpiwko
 */
public class Spacelift {

    /**
     * Creates a task based on task definition
     *
     * @param taskDef
     *     Task definition
     *
     * @return Instantiated task
     */
    public static <IN, OUT, TASK extends Task<? super IN, OUT>> TASK task(Class<TASK> taskDef) {
        return SpaceliftInstance.get().registry().find(taskDef);
    }

    /**
     * Creates a task based on task definition stored under alias
     *
     * @param alias
     *     Task alias
     *
     * @return Instantiated task
     *
     * @throws InvalidTaskException
     *     if no such task exists
     */
    public static Task<?, ?> task(String alias) throws InvalidTaskException {
        return SpaceliftInstance.get().registry().find(alias);
    }

    /**
     * Creates a task based on task definition and passes initial input to it
     *
     * @param input
     *     Input for the task
     * @param taskDef
     *     Task definition
     *
     * @return Instantiated task
     */
    public static <IN, OUT, TASK extends Task<? super IN, OUT>> TASK task(IN input, Class<TASK> taskDef) {
        @SuppressWarnings("unchecked")
        InjectTask<IN> task = SpaceliftInstance.get().registry().find(InjectTask.class);
        return task.passToNext(input).then(taskDef);
    }

    /**
     * Creates a task based on task definition stored under alias and passes initial input to it
     *
     * @param input
     *     input for the task
     * @param alias
     *     Task alias
     *
     * @return Instantiated task
     *
     * @throws InvalidTaskException
     *     if no such task exists
     */
    public static Task<?, ?> task(Object input, String alias) throws InvalidTaskException {
        @SuppressWarnings("unchecked")
        InjectTask<Object> task = SpaceliftInstance.get().registry().find(InjectTask.class);
        return task.passToNext(input).then(alias);
    }

    public static TaskRegistry registry() {
        return SpaceliftInstance.get().registry();
    }

    public static ExecutionService service() {
        return SpaceliftInstance.get().service();
    }

    public static SpaceliftConfiguration configuration() {
        return SpaceliftInstance.get().configuration();
    }

    /**
     * This class should not be used externally, will be replaced by dependency injection
     *
     * @author kpiwko
     */
    @SuppressWarnings({"unchecked"})
    private static class SpaceliftInstance {
        private static final Logger log = Logger.getLogger(Spacelift.class.getName());
        private ExecutionService service;
        private TaskRegistry registry;
        private SpaceliftConfiguration configuration;
        private SpaceliftInstance() {
            try {
                service = ImplementationLoader.implementationOf(ExecutionService.class);
            } catch (InvocationException e) {
                throw new IllegalStateException(
                    MessageFormat.format("Unable to find default implementation of {0} on classpath.",
                        ExecutionService.class.getName()), e);
            }
            try {
                registry = ImplementationLoader.implementationOf(TaskRegistry.class);
                // register inject task for chaining
                registry.register(InjectTask.class);
            } catch (InvocationException e) {
                throw new IllegalStateException(
                    MessageFormat.format("Unable to find default implementation of {0} on classpath.",
                        ExecutionService.class.getName()), e);
            }

            try {
                try {
                    configuration = ImplementationLoader.implementationOf(SpaceliftConfiguration.class);
                    log.log(Level.INFO, "Initialized Spacelift, workspace: {0}, cache: {1}",
                        new Object[] {configuration.workspace().getCanonicalPath(),
                            configuration.cache().getCanonicalPath()});
                } catch (InvocationException e) {
                    configuration = new SpaceliftConfigurationImpl();
                    log.log(Level.INFO, "Initialized Spacelift from defaults, workspace: {0}, cache: {1}",
                        new Object[] {configuration.workspace().getCanonicalPath(),
                            configuration.cache().getCanonicalPath()});
                }
            } catch (IOException e) {
                throw new IllegalStateException(
                    MessageFormat.format("Unable to initialize Spacelift configuration.",
                        ExecutionService.class.getName()), e);
            }
        }

        public static SpaceliftInstance get() {
            return LazyHolder.INSTANCE;
        }

        public TaskRegistry registry() {
            return registry;
        }

        public ExecutionService service() {
            return service;
        }

        public SpaceliftConfiguration configuration() {
            return configuration;
        }

        private static class LazyHolder {
            private static final SpaceliftInstance INSTANCE = new SpaceliftInstance();
        }
    }

    private static class SpaceliftConfigurationImpl implements SpaceliftConfiguration {

        @Override
        public File workspace() {
            return new File(".");
        }

        @Override
        public File cache() throws IOException {
            String userHome = System.getProperty("user.home", ".");
            File cache = new File(userHome, ".spacelift/cache");
            FileUtils.forceMkdir(cache);
            return cache;
        }

        @Override
        public File workpath(String path) throws IllegalArgumentException {

            if (path == null) {
                throw new IllegalArgumentException("Path must not be null.");
            }

            return new File(workspace(), path);
        }

        @Override
        public File cachePath(String path) throws IllegalArgumentException, IOException {

            if (path == null) {
                throw new IllegalArgumentException("Path must not be null.");
            }

            return new File(cache(), path);
        }
    }
}
