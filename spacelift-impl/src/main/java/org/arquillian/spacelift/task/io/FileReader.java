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

import org.arquillian.spacelift.execution.Task;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileReader extends Task<List<File>, Map<File, String>> {

    private Charset charset = StandardCharsets.UTF_8;

    public FileReader charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    @Override
    protected Map<File, String> process(List<File> input) throws Exception {
        Map<File, String> fileContents = new HashMap<File, String>();
        for (File file : input) {
            String content = readFile(file);
            fileContents.put(file, content);
        }
        return fileContents;
    }

    private String readFile(File file) throws IOException {
        InputStream inputStream = null;
        StringBuilder builder;
        ByteArrayOutputStream contentStream = new ByteArrayOutputStream();
        try {
            inputStream = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer, 0, buffer.length)) != -1) {
                contentStream.write(buffer, 0, read);
            }
            contentStream.flush();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return new String(contentStream.toByteArray(), charset);
    }

}
