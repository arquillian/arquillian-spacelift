/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the 
 * distribution for a full listing of individual contributors.
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;

import org.arquillian.spacelift.tool.Tool;

/**
 * Mimics sed tool.
 * 
 * @author <a href="mailto:smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class SedTool extends Tool<File, Void> {

    private File file;

    private String replace = "";

    private String replaceWith = "";

    public SedTool file(File file) {
        this.file = file;
        return this;
    }

    public SedTool replace(String replace) {
        this.replace = replace;
        return this;
    }

    public SedTool replaceWith(String replaceWith) {
        this.replaceWith = replaceWith;
        return this;
    }

    public SedTool file(String file) {
        return file(new File(file));
    }

    @Override
    protected Collection<String> aliases() {
        return Arrays.asList("sed");
    }

    @Override
    protected Void process(File input) throws Exception {
        if (file == null && input == null) {
            throw new IllegalStateException("Please chain or specify a file to operate on.");
        }

        if (file == null && input != null) {
            file = input;
        }

        validate(file);

        replace(file, replace, replaceWith);

        return null;
    }

    private void replace(File file, String searchPattern, String replacementPattern) throws IOException {
        String line = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        StringBuffer buffer = new StringBuffer();

        String lineSeparator = System.getProperty("line.separator");

        while ((line = reader.readLine()) != null) {
            String newline = line.replaceAll(searchPattern, replacementPattern);
            buffer.append(newline + lineSeparator);
        }

        reader.close();
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        out.write(buffer.toString());
        out.close();
    }

    private void validate(File file) {
        if (file == null) {
            throw new IllegalStateException("You have to specify a file to operate on.");
        }

        if (!file.exists()) {
            throw new IllegalStateException("You have specified file to operate on which does not exist: " + file.getAbsolutePath());
        }

        if (!file.canWrite()) {
            throw new IllegalStateException("You do not have write permissions to a file you specify: " + file.getAbsolutePath());
        }
    }
}
