package org.arquillian.spacelift.execution;

import java.text.MessageFormat;

public class ExecutionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ExecutionException(String messageFormat, Object... parameters) {
        super(MessageFormat.format(messageFormat, parameters));
    }

    public ExecutionException(Throwable cause, String messageFormat, Object... parameters) {
        super(MessageFormat.format(messageFormat, parameters), cause);
    }

    public ExecutionException(Throwable cause) {
        super(cause);
    }

}
