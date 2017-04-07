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
package org.arquillian.spacelift.task.archive;

import java.io.File;
import org.arquillian.spacelift.execution.ExecutionService;
import org.arquillian.spacelift.task.TaskRegistry;
import org.arquillian.spacelift.task.impl.TaskRegistryImpl;
import org.arquillian.spacelift.task.net.DownloadTool;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;

public class DownloadUncompressToolTest {

    static TaskRegistry registry;

    static ExecutionService service;

    @BeforeClass
    public static void setup() {
        registry = new TaskRegistryImpl();
        registry.register(DownloadTool.class);
        registry.register(UnzipTool.class);
    }

    @Test
    public void downloadFileAndExtract() {

        File jsonSmartExtracted = registry.find(DownloadTool.class)
            .from("http://search.maven.org/remotecontent?filepath=net/minidev/json-smart/1.2/json-smart-1.2.jar")
            .to("target/json-smart-1.2.jar")
            .then(UnzipTool.class)
            .toDir("target/json-smart-extracted")
            .execute().await();

        Assert.assertThat(jsonSmartExtracted, notNullValue());
    }
}
