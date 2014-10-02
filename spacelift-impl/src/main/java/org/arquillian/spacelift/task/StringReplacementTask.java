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
package org.arquillian.spacelift.task;

import org.arquillian.spacelift.execution.Task;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class StringReplacementTask extends Task<Map<File, String>, Map<File, String>> {
    private List<ReplacementTuple<?>> replacements = new ArrayList<ReplacementTuple<?>>();

    public ReplacementTuple<StringReplacementTask> replace(String regex) {
        ReplacementTuple<StringReplacementTask> replacement = new ReplacementTuple<StringReplacementTask>(this, regex);
        replacements.add(replacement);
        return replacement;
    }

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
