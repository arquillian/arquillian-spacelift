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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.arquillian.spacelift.execution.ExecutionException;

/**
 * Untar Tool
 *
 * @author <a href="asotobu@gmail.com">Alex Soto</a>
 *
 */
public class UntarTool extends UncompressTool {

    private boolean isGzipped = true;

    public UntarTool gzip(boolean isGzip) {
        this.isGzipped = isGzip;
        return this;
    }

    @Override
    protected ArchiveInputStream compressedInputStream(InputStream compressedFile) {

        BufferedInputStream in = new BufferedInputStream(compressedFile);
        TarArchiveInputStream tarIn = null;
        if (this.isGzipped) {
            GzipCompressorInputStream gzIn;
            try {
                gzIn = new GzipCompressorInputStream(in);
                tarIn = new TarArchiveInputStream(gzIn);
            } catch (IOException e) {
                throw new ExecutionException(e);
            }
        } else {
            tarIn = new TarArchiveInputStream(in);
        }

        return tarIn;

    }

    @Override
    protected int permissionsMode(ArchiveEntry archiveEntry) {
        if (archiveEntry instanceof TarArchiveEntry) {
            TarArchiveEntry tarArchiveEntry = (TarArchiveEntry) archiveEntry;
            return tarArchiveEntry.getMode();
        } else {
            throw new ExecutionException("No TarEntry has been passed to a Tar method.");
        }
    }

    @Override
    protected Collection<String> aliases() {
        return Arrays.asList("tar");
    }

}
