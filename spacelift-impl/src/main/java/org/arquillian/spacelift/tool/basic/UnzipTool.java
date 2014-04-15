/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat Middleware LLC, and individual contributors
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
package org.arquillian.spacelift.tool.basic;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Callable;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.arquillian.spacelift.tool.InternalTool;

/**
 * File unzipper
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class UnzipTool implements InternalTool<UnzipTool, File> {

    private File source;
    private File dest;

    @Override
    public Collection<String> aliases() {
        return Arrays.asList("unzip");
    }

    public UnzipTool from(String pathToFile) {
        return from(new File(pathToFile));
    }

    public UnzipTool from(File source) {
        this.source = source;
        return this;
    }

    public UnzipTool to(String pathToDestination) {
        return to(new File(pathToDestination));
    }

    public UnzipTool to(File destination) {
        this.dest = destination;
        return this;
    }

    @Override
    public Callable<File> getCallable() {

        return new Callable<File>() {

            @Override
            public File call() throws Exception {
                try {
                    ZipFile zipFile = new ZipFile(source);
                    zipFile.extractAll(dest.getAbsolutePath());
                } catch (ZipException e) {
                    e.printStackTrace();
                }
                return dest;
            }
        };
    }

}
