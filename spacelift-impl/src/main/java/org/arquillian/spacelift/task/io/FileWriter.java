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

import org.arquillian.spacelift.task.Task;
import org.arquillian.spacelift.util.CharsetUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Task for writing text to files. Its input is a {@link Map} of {@link File}s as keys and {@link String}s as values
 * and it outputs {@link List} of the files written.
 */
public class FileWriter extends Task<Map<File, String>, List<File>> {
    private Charset charset = CharsetUtil.getUtf8OrDefault();

    /**
     * Sets the charset which will be used to write all the files. The default charset is UTF8 or the system's default.
     *
     * @param charset The charset to be used.
     * @return The same instance of FileWriter.
     * @see CharsetUtil#getUtf8OrDefault()
     */
    public FileWriter charset(Charset charset) {
        this.charset = charset;
        return this;
    }

    @Override
    protected List<File> process(Map<File, String> input) throws Exception {
        List<File> files = new ArrayList<File>();
        for (File file : input.keySet()) {
            String content = input.get(file);

            OutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(file);

                byte[] contentBytes = content.getBytes(charset);

                outputStream.write(contentBytes, 0, contentBytes.length);
                outputStream.flush();

                files.add(file);
            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        }
        return files;
    }
}
