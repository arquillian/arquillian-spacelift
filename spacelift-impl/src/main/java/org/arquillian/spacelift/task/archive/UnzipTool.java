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

import java.io.BufferedInputStream;
import java.io.InputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.zip.AsiExtraField;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipExtraField;
import org.arquillian.spacelift.execution.ExecutionException;

/**
 * Unzip Tool
 *
 * @author <a href="asotobu@gmail.com">Alex Soto</a>
 *
 */
public class UnzipTool extends UncompressTool {

    @Override
    protected ArchiveInputStream compressedInputStream(InputStream compressedFile) {
        BufferedInputStream in = new BufferedInputStream(compressedFile);
        return new ZipArchiveInputStream(in);
    }

    @Override
    protected int permissionsMode(ArchiveEntry archiveEntry) {
        if (archiveEntry instanceof ZipArchiveEntry) {
            ZipArchiveEntry zipArchiveEntry = (ZipArchiveEntry) archiveEntry;

            ZipExtraField[] extraFields = zipArchiveEntry.getExtraFields();
            for (ZipExtraField zipExtraField : extraFields) {
                if (zipExtraField instanceof AsiExtraField) {
                    AsiExtraField asiExtraField = (AsiExtraField) zipExtraField;
                    return asiExtraField.getMode();
                }
            }
        } else {
            throw new ExecutionException("No ZipEntry has been passed to a Unzip method.");
        }

        return 0;
    }
}