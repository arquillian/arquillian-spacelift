/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
package org.arquillian.spacelift;

/**
 * Utility capable of loading interface implementations available on classpath indirectly.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
public final class ImplementationLoader {

    private static final String CLASS_NAME_SERVICELOADER = "org.arquillian.spacelift.loader.ServiceLoader";
    private static final String CLASS_NAME_SPISERVICELOADER = "org.arquillian.spacelift.loader.SpiServiceLoader";
    private static final String CLASS_NAME_SERVICEREGISTRY = "org.arquillian.spacelift.loader.ServiceRegistry";
    private static final String METHOD_NAME_ONLY_ONE = "onlyOne";
    private static final String METHOD_NAME_REGISTER = "register";

    /**
     * Internal constructor; not to be called
     */
    private ImplementationLoader() {
        throw new UnsupportedOperationException("No instances permitted");
    }

    /**
     * Creates a new instance of the specified type using the {@link Thread} Context
     * {@link ClassLoader}. Will consult a configuration file visible to the {@link Thread} Context {@link ClassLoader}
     * named
     * "META-INF/services/$fullyQualfiedClassName" which should contain a fully qualified name of the implementation.
     * <p>
     * The implementation class name must have a no-arg constructor.
     *
     * @param interfaceClass
     *     interface to find implementation for
     *
     * @return Implementation of interface class
     *
     * @throws IllegalArgumentException
     *     if {@code interfaceClass} is null
     */
    static <INTERFACE> INTERFACE implementationOf(
        final Class<INTERFACE> interfaceClass) throws IllegalArgumentException {
        return implementationOf(interfaceClass, SecurityActions.getThreadContextClassLoader());
    }

    /**
     * Creates a new instance of the specified user view type using the specified {@link ClassLoader}.
     * Will consult a configuration file visible to the specified {@link ClassLoader} named
     * "META-INF/services/$fullyQualfiedClassName" which should contain a fully qualified name of the implementation.
     * <p>
     * The implementation class name must have a no-arg constructor.
     */
    static <INTERFACE> INTERFACE implementationOf(
        final Class<INTERFACE> factoryClass, final ClassLoader cl) {

        assert factoryClass != null : "user view class must be specified";
        assert cl != null : "ClassLoader must be specified";

        // get SPI service loader
        final Object spiServiceLoader = new Invokable(cl, CLASS_NAME_SPISERVICELOADER)
            .invokeConstructor(new Class[] {ClassLoader.class}, new Object[] {cl});

        // return service loader implementation
        final Object serviceLoader = new Invokable(cl, CLASS_NAME_SPISERVICELOADER).invokeMethod(METHOD_NAME_ONLY_ONE,
            new Class[] {Class.class, Class.class}, spiServiceLoader,
            new Object[] {Invokable.loadClass(cl, CLASS_NAME_SPISERVICELOADER), spiServiceLoader.getClass()});

        // get registry
        final Object serviceRegistry = new Invokable(cl, CLASS_NAME_SERVICEREGISTRY).invokeConstructor(
            new Class<?>[] {Invokable.loadClass(cl, CLASS_NAME_SERVICELOADER)},
            new Object[] {serviceLoader});

        // register itself
        new Invokable(cl, serviceRegistry.getClass()).invokeMethod(METHOD_NAME_REGISTER,
            new Class<?>[] {serviceRegistry.getClass()}, null, new Object[] {serviceRegistry});

        Object userViewObject = new Invokable(cl, serviceRegistry.getClass()).invokeMethod(METHOD_NAME_ONLY_ONE,
            new Class<?>[] {Class.class}, serviceRegistry, new Object[] {factoryClass});

        return factoryClass.cast(userViewObject);
    }
}
