package org.arquillian.spacelift.tool;

import java.text.MessageFormat;

public class InvalidToolException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidToolException() {
        super();
    }

    public InvalidToolException(Throwable cause, String messageFormat, Object... args) {
        super(MessageFormat.format(messageFormat, args), cause);
    }

    public InvalidToolException(String messageFormat, Object... args) {
        super(MessageFormat.format(messageFormat, args));
    }

}
