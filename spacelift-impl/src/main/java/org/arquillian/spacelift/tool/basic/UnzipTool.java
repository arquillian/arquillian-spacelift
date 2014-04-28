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

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.arquillian.spacelift.execution.ExecutionException;
import org.arquillian.spacelift.tool.Tool;

/**
 * File unzipper
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class UnzipTool extends Tool<File, File> {

    private File dest;

    @Override
    public Collection<String> aliases() {
        return Arrays.asList("unzip");
    }

    /**
     *
     * @param pathToDestination destination where to unzip a file
     * @return
     */
    public UnzipTool toDir(String pathToDestination) {
        return toDir(new File(pathToDestination));
    }

    /**
     *
     * @param destination destination where to unzip a file
     * @return
     */
    public UnzipTool toDir(File destination) {
        this.dest = destination;
        return this;
    }

    @Override
    protected File process(File input) throws Exception {
        try {
            ZipFile zipFile = new ZipFile(input);
            zipFile.extractAll(dest.getAbsolutePath());
        } catch (ZipException e) {
            throw new ExecutionException(e, "Unable to unzip {0} to {1}", input, dest);
        }
        return dest;
    }
}
