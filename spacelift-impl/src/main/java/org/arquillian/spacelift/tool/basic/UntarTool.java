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
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.arquillian.spacelift.execution.ExecutionException;

/**
 * Untar Tool
 *
 * @author <a href="asotobu@gmail.com">Alex Soto</a>
 *
 */
public class UntarTool extends UncompressTool {

    private enum Compression {
        GZIP {
            @Override
            public ArchiveInputStream wrap(BufferedInputStream input) {
                GzipCompressorInputStream gzIn;
                try {
                    gzIn = new GzipCompressorInputStream(input);
                    return new TarArchiveInputStream(gzIn);
                } catch (IOException e) {
                    throw new ExecutionException(e);
                }
            }
        },
        BZIP2 {
            @Override
            public ArchiveInputStream wrap(BufferedInputStream input) {
                BZip2CompressorInputStream bzip2;
                try {
                    bzip2 = new BZip2CompressorInputStream(input);
                    return new TarArchiveInputStream(bzip2);
                } catch (IOException e) {
                    throw new ExecutionException(e);
                }
            }
        },
        NONE {
            @Override
            public ArchiveInputStream wrap(BufferedInputStream input) {
                return new TarArchiveInputStream(input);
            }
        };

        public abstract ArchiveInputStream wrap(BufferedInputStream input);
    }

    private Compression compression = Compression.GZIP;

    public UntarTool gzip(boolean isGzip) {
        this.compression = (isGzip) ? Compression.GZIP : Compression.NONE;
        return this;
    }

    public UntarTool bzip2(boolean isBZip2) {
        this.compression = (isBZip2) ? Compression.BZIP2 : Compression.NONE;
        return this;
    }

    @Override
    protected ArchiveInputStream compressedInputStream(InputStream compressedFile) {

        BufferedInputStream in = new BufferedInputStream(compressedFile);

        return compression.wrap(in);
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
