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

import static org.hamcrest.CoreMatchers.notNullValue;

import java.io.File;

import org.arquillian.spacelift.execution.ExecutionService;
import org.arquillian.spacelift.execution.Tasks;
import org.arquillian.spacelift.execution.impl.DefaultExecutionServiceFactory;
import org.arquillian.spacelift.tool.ToolRegistry;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class UncompressToolTest {

    static ToolRegistry registry;

    static ExecutionService service;

    @BeforeClass
    public static void setup() {
        Tasks.setDefaultExecutionServiceFactory(new DefaultExecutionServiceFactory());
    }

    @Test
    public void extractZipFile() {
        File helloExtracted = Tasks.chain(new File("src/test/resources/hello.zip"), UnzipTool.class)
              .toDir("target/hellozip")
              .execute()
              .await();
        Assert.assertThat(helloExtracted, notNullValue());
    }

    @Test
    public void extractTarGzFile() {
        File helloExtracted = Tasks.chain(new File("src/test/resources/hello.tgz"), UntarTool.class)
              .toDir("target/hellotgz")
              .execute()
              .await();
        Assert.assertThat(helloExtracted, notNullValue());
    }
}
