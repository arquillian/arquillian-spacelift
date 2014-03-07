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
package org.arquillian.spacelift.process.impl;

import org.arquillian.spacelift.process.ProcessExecutor;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Matchers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProcessExecutorImplTest {

    private ProcessExecutor executor;
    private List<String> tempFiles;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        executor = new ProcessExecutorImpl();
        tempFiles = new ArrayList<String>();
    }

    @After
    public void tearDown() {
        executor = null;
        deleteTempFiles();
    }

    @Test
    public void testExistingWorkingDirectory() throws Exception {
        String existingDirectory = System.getProperty("user.dir");

        executor.setWorkingDirectory(existingDirectory);
    }

    @Test
    public void testNonexistentDirectory() throws Exception {
        String nonexistentDirectory = System.getProperty("user.dir") + "/" + UUID.randomUUID();

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("does not exist");
        executor.setWorkingDirectory(nonexistentDirectory);
    }

    @Test
    public void testFileAsWorkingDirectory() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("is not a directory");
        executor.setWorkingDirectory(createTempFile());
    }

    private String createTempFile() throws IOException, IllegalStateException {
        String tempFile = System.getProperty("java.io.tmpdir") + "/test_file.txt";
        FileWriter writer = new FileWriter(tempFile);
        writer.write("Lorem ipsum dolor sit amet ...");
        writer.flush();
        writer.close();
        tempFiles.add(tempFile);
        return tempFile;
    }

    private void deleteTempFiles() {
        for (String tempFile : tempFiles) {
            new File(tempFile).delete();
        }
    }

}
