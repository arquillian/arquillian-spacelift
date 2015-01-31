/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
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
package org.arquillian.spacelift.task.text;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.arquillian.spacelift.task.Task;

/**
 * Task for string replacement for files. Its input and output type is a map of file to string. This is because it is
 * designed for string replacement in files. When no replacements are added, the input and output strings will be equal.
 *
 * @author <a href="mailto:smikloso@redhat.com">Stefan Miklosovic</a>
 * @author <a href="mailto:tkriz@redhat.com">Tadeas Kriz</a>
 */
public class StringReplacementTask extends Task<Map<File, String>, Map<File, String>> {
    private List<ReplacementTuple<?>> replacements = new ArrayList<ReplacementTuple<?>>();

    /**
     * Set a regular expression to be used for the replacement. You will then be able to specify the replacement string.
     * <p/>
     * NOTE: You can specify multiple replacements and they will all be evaluated when the task is run. The order of
     * their evaluation is the same as you add the replacements into this task.
     */
    public ReplacementTuple<StringReplacementTask> replace(String regex) {
        ReplacementTuple<StringReplacementTask> replacement = new ReplacementTuple<StringReplacementTask>(this, regex);
        replacements.add(replacement);
        return replacement;
    }

    /**
     * Add a collection of replacements, in case you do not want to use the fluent API.
     */
    public StringReplacementTask replace(Collection<ReplacementTuple<?>> replacements) {
        this.replacements.addAll(replacements);
        return this;
    }

    @Override
    protected Map<File, String> process(Map<File, String> input) throws Exception {
        for (File file : input.keySet()) {
            String content = input.get(file);
            for (ReplacementTuple<?> tuple : replacements) {
                if (tuple.getRegex() == null || tuple.getReplacement() == null) {
                    continue;
                }
                content = content.replaceAll(tuple.getRegex(), tuple.getReplacement());
            }
            input.put(file, content);
        }

        return input;
    }

}
