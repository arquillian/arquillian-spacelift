package org.arquillian.spacelift.process.impl;

import org.arquillian.spacelift.execution.ExecutionException;

/**
 * Validate
 *
 * Validation utility
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public final class Validate {

    /**
     * Checks that object is not null, throws exception if it is.
     *
     * @param obj The object to check
     * @param message The exception message
     * @throws IllegalArgumentException Thrown if obj is null
     */
    public static void notNull(final Object obj, final String message) throws IllegalArgumentException {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Checks that the specified String is not null or empty,
     * throws exception if it is.
     *
     * @param string The object to check
     * @param message The exception message
     * @throws IllegalArgumentException Thrown if obj is null
     */
    public static void notNullOrEmpty(final CharSequence string, final String message) throws IllegalArgumentException {
        if (string == null || string.length() == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Checks that obj is not null, throws exception if it is.
     *
     * @param obj The object to check
     * @param message The exception message
     * @throws IllegalStateException Thrown if obj is null
     */
    public static void stateNotNull(final Object obj, final String message) throws IllegalStateException {
        if (obj == null) {
            throw new IllegalStateException(message);
        }
    }

    /**
     * Checks that obj is not null, throws exception if it is.
     *
     * @param obj The object to check
     * @param message The exception message
     * @param params The exception message parameters
     * @throws ExecutionException Thrown if object is null
     */
    public static void executionNotNull(final Object obj, final String messageFormat, Object... params)
        throws ExecutionException {
        if (obj == null) {
            throw new ExecutionException(messageFormat, params);
        }
    }

}
