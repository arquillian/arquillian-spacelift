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

import java.io.File;

import org.arquillian.spacelift.execution.ExecutionService;
import org.arquillian.spacelift.execution.Tasks;
import org.arquillian.spacelift.execution.impl.DefaultExecutionServiceFactory;
import org.arquillian.spacelift.execution.impl.ExecutionServiceImpl;
import org.arquillian.spacelift.tool.ToolRegistry;
import org.arquillian.spacelift.tool.impl.ToolRegistryImpl;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.notNullValue;

public class DownloadToolTest {

    static ToolRegistry registry;

    @BeforeClass
    public static void setup() {
        Tasks.setDefaultExecutionServiceFactory(new DefaultExecutionServiceFactory());
        registry = new ToolRegistryImpl();
        registry.register(DownloadTool.class);

    }

    @Test
    public void downloadFile() {

        File indexHtml = registry.find(DownloadTool.class)
            .from("http://www.arquillian.org")
            .to("target/index.html")
            .execute().waitFor();

        Assert.assertThat(indexHtml, notNullValue());
    }

}
