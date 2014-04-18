package org.arquillian.spacelift.execution;

public class Tasks {

    private static class ExecutionServiceFactoryHolder {
        // FIXME this might be loaded dynamically from classpath via SPI
        public static ExecutionServiceFactory lastFactory = null;
    }

    private static ExecutionServiceFactory getAsSingleton() {
        return ExecutionServiceFactoryHolder.lastFactory;
    }

    public static ExecutionServiceFactory getExecutionServiceFactoryInstance() {
        return getAsSingleton();
    }

    public static void setDefaultExecutionServiceFactory(ExecutionServiceFactory executionServiceFactory) {
        synchronized (ExecutionServiceFactoryHolder.class) {
            ExecutionServiceFactoryHolder.lastFactory = executionServiceFactory;
        }
    }

    public static <IN, OUT, TASK extends Task<? super IN, OUT>> TASK prepare(Class<TASK> taskDef) {
        TASK task = SecurityActions.newInstance(taskDef);
        // FIXME should check for null
        task.setExecutionService(getExecutionServiceFactoryInstance().getExecutionServiceInstance());
        return task;
    }
}
