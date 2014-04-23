/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
package org.arquillian.spacelift.execution;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/**
 * A set of privileged actions that are not to leak out of this package
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 */
final class SecurityActions {

    // -------------------------------------------------------------------------------||
    // Constructor -------------------------------------------------------------------||
    // -------------------------------------------------------------------------------||

    /**
     * No instantiation
     */
    private SecurityActions() {
        throw new UnsupportedOperationException("No instantiation");
    }

    // -------------------------------------------------------------------------------||
    // Utility Methods ---------------------------------------------------------------||
    // -------------------------------------------------------------------------------||

    /**
     * Obtains the Thread Context ClassLoader
     */
    static ClassLoader getThreadContextClassLoader() {
        return AccessController.doPrivileged(GetTcclAction.INSTANCE);
    }

    static <T> T newInstance(Class<T> clazz) throws IllegalArgumentException, IllegalStateException, RuntimeException {
        return newInstance(clazz, new Class<?>[0], new Object[0], clazz);
    }

    /**
     * Create a new instance by finding a constructor that matches the argumentTypes signature using the arguments for
     * instantiation.
     *
     * @param clazz Clazz to create
     * @param argumentTypes Constructor argument types
     * @param arguments Constructor arguments
     * @param expectedType Expected type of created class
     * @throws IllegalArgumentException if clazz, argumentTypes, or arguments are null
     * @throws RuntimeException if any exceptions during creation
     * @return
     */
    private static <T> T newInstance(final Class<?> clazz, final Class<?>[] argumentTypes, final Object[] arguments,
        final Class<T> expectedType) throws IllegalArgumentException, IllegalStateException, RuntimeException {
        if (argumentTypes == null) {
            throw new IllegalArgumentException("ArgumentTypes must be specified. Use empty array if no arguments");
        }
        if (arguments == null) {
            throw new IllegalArgumentException("Arguments must be specified. Use empty ar>ray if no arguments");
        }
        final Object obj;

        try {
            Constructor<?> constructor = getConstructor(clazz, argumentTypes);
            constructor.setAccessible(true);
            obj = constructor.newInstance(arguments);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Unable to find a constructor for implementation class "
                + getConstructorName(clazz.getName(), argumentTypes));
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Unable to instantiate a " + clazz.getName(), e);
        } catch (InstantiationException e) {
            throw new IllegalStateException("Unable to instantiate a " + clazz.getName(), e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Unable to instantiate a " + clazz.getName()
                + " instance, access refused by SecurityManager.", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(
                String.format("Unable to instantiate tool via %s: %s",
                    getConstructorName(clazz.getName(), argumentTypes),
                    e.getCause()), // this provides the message of the ITE cause, which is also important!
                e.getCause()); // this provides stack trace of the ITE cause
        }

        // Cast
        try {
            return expectedType.cast(obj);
        } catch (final ClassCastException cce) {
            // Reconstruct so we get some useful information
            throw new ClassCastException("Unable to instantiate " + expectedType.getName()
                + " instance. Constructed object was type of " + obj.getClass().getName()
                + ", which is not compatible.");
        }
    }

    /**
     * Obtains the Constructor specified from the given Class and argument types
     *
     * @param clazz
     * @param argumentTypes
     * @return
     * @throws NoSuchMethodException
     */
    private static Constructor<?> getConstructor(final Class<?> clazz, final Class<?>... argumentTypes)
        throws NoSuchMethodException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<Constructor<?>>() {
                public Constructor<?> run() throws NoSuchMethodException {
                    return clazz.getDeclaredConstructor(argumentTypes);
                }
            });
        }
        // Unwrap
        catch (final PrivilegedActionException pae) {
            final Throwable t = pae.getCause();
            // Rethrow
            if (t instanceof NoSuchMethodException) {
                throw (NoSuchMethodException) t;
            } else {
                // No other checked Exception thrown by Class.getConstructor
                try {
                    throw (RuntimeException) t;
                }
                // Just in case we've really messed up
                catch (final ClassCastException cce) {
                    throw new RuntimeException("Obtained unchecked Exception; this code should never be reached", t);
                }
            }
        }
    }

    private static String getConstructorName(String className, final Class<?>[] argumentTypes) {
        StringBuilder constructor = new StringBuilder(className).append("(");
        for (Class<?> arg : argumentTypes) {
            constructor.append(arg.getSimpleName()).append(",");
        }
        if (constructor.charAt(constructor.length() - 1) == ',') {
            constructor.deleteCharAt(constructor.length() - 1);
        }
        constructor.append(")");
        return constructor.toString();
    }

    // -------------------------------------------------------------------------------||
    // Inner Classes
    // ----------------------------------------------------------------||
    // -------------------------------------------------------------------------------||

    /**
     * Single instance to get the TCCL
     */
    private enum GetTcclAction implements PrivilegedAction<ClassLoader> {
        INSTANCE;

        public ClassLoader run() {
            return Thread.currentThread().getContextClassLoader();
        }

    }

}