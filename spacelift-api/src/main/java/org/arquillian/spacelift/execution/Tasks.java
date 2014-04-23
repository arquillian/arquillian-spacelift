package org.arquillian.spacelift.execution;

public class Tasks {

    private static class ExecutionServiceFactoryHolder {
        // FIXME this might be loaded dynamically from classpath via SPI
        public static ExecutionServiceFactory lastFactory = null;
    }

    private static ExecutionServiceFactory getAsSingleton() {
        return ExecutionServiceFactoryHolder.lastFactory;
    }

    public static ExecutionServiceFactory getExecutionServiceFactoryInstance() throws IllegalStateException {
        ExecutionServiceFactory factory = getAsSingleton();
        if (factory == null) {
            throw new IllegalStateException("ExecutionServiceFactory was null. If you are not running from Arquillian, make sure that you set it up first.");
        }
        return factory;
    }

    public static void setDefaultExecutionServiceFactory(ExecutionServiceFactory executionServiceFactory) {
        synchronized (ExecutionServiceFactoryHolder.class) {
            ExecutionServiceFactoryHolder.lastFactory = executionServiceFactory;
        }
    }

    public static <IN, OUT, TASK extends Task<? super IN, OUT>> TASK prepare(Class<TASK> taskDef) {
        TASK task = SecurityActions.newInstance(taskDef);
        task.setExecutionService(getExecutionServiceFactoryInstance().getExecutionServiceInstance());
        return task;
    }

    public static <IN, OUT, TASK extends Task<? super IN, OUT>> TASK chain(IN input, Class<TASK> taskDef) {

        @SuppressWarnings("unchecked")
        InjectTask<IN> task = SecurityActions.newInstance(InjectTask.class);
        task.setExecutionService(getExecutionServiceFactoryInstance().getExecutionServiceInstance());

        return task.passToNext(input).then(taskDef);
    }

    public static final class InjectTask<NEXT_IN> extends Task<Object, NEXT_IN> {

        private NEXT_IN nextIn;

        public InjectTask<NEXT_IN> passToNext(NEXT_IN next) {
            this.nextIn = next;
            return this;
        }

        @Override
        protected NEXT_IN process(Object input) throws Exception {
            return nextIn;
        }

    }

}
