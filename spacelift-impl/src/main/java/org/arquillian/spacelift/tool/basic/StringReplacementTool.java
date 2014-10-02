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

import org.arquillian.spacelift.execution.Tasks;
import org.arquillian.spacelift.task.ReplacementTuple;
import org.arquillian.spacelift.task.StringReplacementTask;
import org.arquillian.spacelift.task.io.FileReader;
import org.arquillian.spacelift.task.io.FileWriter;
import org.arquillian.spacelift.task.selector.FileSelector;
import org.arquillian.spacelift.tool.Tool;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Mimics sed tool.
 *
 * @author <a href="mailto:smikloso@redhat.com">Stefan Miklosovic</a>
 */
public class StringReplacementTool extends Tool<Object, List<File>> {

    private Charset charset = StandardCharsets.UTF_8;
    private List<File> files = new ArrayList<File>();
    private List<ReplacementTuple<?>> replacements = new ArrayList<ReplacementTuple<?>>();


    public StringReplacementTool in(File... file) {
        Collections.addAll(files, file);
        return this;
    }

    public StringReplacementTool in(String file) {
        return in(new File(file));
    }

    public ReplacementTuple<StringReplacementTool> replace(String regex) {
        ReplacementTuple<StringReplacementTool> replacement = new ReplacementTuple<StringReplacementTool>(this, regex);
        replacements.add(replacement);
        return replacement;
    }

    public StringReplacementTool charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    @Override
    protected Collection<String> aliases() {
        return Arrays.asList("sed");
    }

    @Override
    protected List<File> process(Object input) throws Exception {
        return Tasks.prepare(FileSelector.class)
                .select(files)
                .then(FileReader.class)
                .charset(charset)
                .then(StringReplacementTask.class)
                .replace(replacements)
                .then(FileWriter.class)
                .charset(charset)
                .execute().await();
    }

    private void validate(File file) {
        if (file == null) {
            throw new IllegalStateException("You have to specify a file to operate on.");
        }

        if (!file.exists()) {
            throw new IllegalStateException("You have specified file to operate on which does not exist: " + file
                    .getAbsolutePath());
        }

        if (!file.canWrite()) {
            throw new IllegalStateException("You do not have write permissions to a file you specify: " + file
                    .getAbsolutePath());
        }
    }
}
