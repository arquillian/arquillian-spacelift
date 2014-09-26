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
import org.arquillian.spacelift.util.CharsetUtil;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Tool for string replacement inside files. It has no input and outputs list of modified files. To select the files
 * to be processed, use {@link #in(File...)} or {@link #in(String)}. All the files you select for processing has to
 * be using the same charset. If you wish to replace files with different charsets,
 * you need to use this tool multiple times and use {@link #charset(Charset)} to set the charset of processed files.
 *
 * @author <a href="mailto:smikloso@redhat.com">Stefan Miklosovic</a>
 * @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
 */
public class StringReplacementTool extends Tool<Object, List<File>> {

    private Charset charset = CharsetUtil.getUtf8OrDefault();
    private List<File> files = new ArrayList<File>();
    private List<ReplacementTuple<?>> replacements = new ArrayList<ReplacementTuple<?>>();

    /**
     * Adds files to the list to be processed by the tool.
     *
     * @param file
     * @return
     */
    public StringReplacementTool in(File... file) {
        Collections.addAll(files, file);
        return this;
    }

    /**
     * Adds path as a file to be processed by the tool.
     *
     * @param file
     * @return
     */
    public StringReplacementTool in(String file) {
        return in(new File(file));
    }

    /**
     * Set a regular expression to be used for the replacement. You will then be able to specify the replacement string.
     * <p/>
     * NOTE: You can specify multiple replacements and they will all be evaluated when the task is run. The order of
     * their evaluation is the same as you add the replacements into this tool.
     */
    public ReplacementTuple<StringReplacementTool> replace(String regex) {
        ReplacementTuple<StringReplacementTool> replacement = new ReplacementTuple<StringReplacementTool>(this, regex);
        replacements.add(replacement);
        return replacement;
    }

    /**
     * Selects the charset which will be used for reading and writing the files.
     */
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
        // FIXME should we remove the processed files from #files list?
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
}
