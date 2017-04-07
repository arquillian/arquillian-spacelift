package org.arquillian.spacelift;

import java.io.File;

/**
 * Spacelift configuration.
 * <p>
 * You can override defaults by providing different implementation and registering it via SPI.
 * <p>
 * Created by kpiwko on 19/07/15.
 */
public interface SpaceliftConfiguration {

    /**
     * Returns a directory where Spacelift performs all tasks.
     * <p>
     * By default, this is current directory.
     */
    File workspace();

    /**
     * Returns a directory where Spacelift caches task artifacts so they can be reused by all builds.
     * <p>
     * By default, this is $USER_DIR/.spacelift
     */
    File cache();

    /**
     * Returns a path in workspace
     *
     * @param path
     *     path in workspace
     *
     * @throws IllegalArgumentException
     *     if path is null
     */
    File workpath(String path) throws IllegalArgumentException;

    /**
     * Returns a path in cache directory
     *
     * @param path
     *     path in cache directory
     *
     * @throws IllegalArgumentException
     *     if path is null
     */
    File cachePath(String path) throws IllegalArgumentException;
}
