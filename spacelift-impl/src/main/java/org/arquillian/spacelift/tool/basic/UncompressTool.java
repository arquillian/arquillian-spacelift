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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.arquillian.spacelift.tool.Tool;

/**
 * Uncompress Tool
 *
 * @author <a href="asotobu@gmail.com">Alex Soto</a>
 *
 */
public abstract class UncompressTool extends Tool<File, File> {

    private static final int BUFFER = 2048;

    private File dest;

    protected abstract ArchiveInputStream compressedInputStream(InputStream compressedFile);

    protected abstract int permissionsMode(ArchiveEntry archiveEntry);

    /**
     *
     * @param pathToDestination
     *            destination where to unzip a file
     * @return
     */
    public UncompressTool toDir(String pathToDestination) {
        return toDir(new File(pathToDestination));
    }

    /**
     *
     * @param destination
     *            destination where to unzip a file
     * @return
     */
    public UncompressTool toDir(File destination) {
        this.dest = destination;
        return this;
    }

    @Override
    protected File process(File input) throws Exception {
        ArchiveEntry entry = null;

        /** Read the tar entries using the getNextEntry method **/

        ArchiveInputStream compressedInputStream = compressedInputStream(new FileInputStream(input));

        while ((entry = compressedInputStream.getNextEntry()) != null) {

            File file = new File(this.dest, entry.getName());

            if (entry.isDirectory()) {
                File f = file;
                f.mkdirs();
            } else {

                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                int count;
                byte data[] = new byte[BUFFER];

                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
                while ((count = compressedInputStream.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, count);
                }
                dest.close();

                int permissionsMode = permissionsMode(entry);
                if (permissionsMode != 0) {
                    FilePermission filePermission = PermissionsUtil.toFilePermission(permissionsMode);
                    PermissionsUtil.applyPermission(file, filePermission);
                }
            }
        }

        compressedInputStream.close();

        return this.dest;
    }

}
