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
package org.arquillian.spacelift.task.io;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.task.Task;

public class WriteToFileTool extends Task<Object, List<File>> {

    private List<WriteFileContentsTuple> fileContents = new ArrayList<WriteFileContentsTuple>();

    public WriteFileContentsTuple write(String content) {
        WriteFileContentsTuple contentsTuple = new WriteFileContentsTuple(content);
        fileContents.add(contentsTuple);
        return contentsTuple;
    }

    @Override
    protected List<File> process(Object input) throws Exception {
        Map<File, String> contents = new HashMap<File, String>();
        for (WriteFileContentsTuple fileContent : fileContents) {
            if(fileContent.target == null) {
                continue;
            }
            contents.put(fileContent.target, fileContent.content);
        }
        return Spacelift.task(contents, FileWriter.class).execute().await();
    }

    public class WriteFileContentsTuple {
        private String content;
        private File target;

        private WriteFileContentsTuple(String content) {
            this.content = content;
        }

        public WriteToFileTool to(File file) {
            target = file;
            return WriteToFileTool.this;
        }

        public WriteToFileTool to(String path) {
            return to(new File(path));
        }
    }
}
