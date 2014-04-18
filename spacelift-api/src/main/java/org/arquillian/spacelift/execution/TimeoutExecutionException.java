package org.arquillian.spacelift.execution;

public class TimeoutExecutionException extends ExecutionException {

    private static final long serialVersionUID = 1L;

    public TimeoutExecutionException(String messageFormat, Object... parameters) {
        super(messageFormat, parameters);
    }

    public TimeoutExecutionException(Throwable cause, String messageFormat, Object... parameters) {
        super(cause, messageFormat, parameters);
    }

}
