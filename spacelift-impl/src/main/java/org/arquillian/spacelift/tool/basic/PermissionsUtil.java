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

/**
 * File util
 *
 * @author <a href="asotobu@gmail.com">Alex Soto</a>
 *
 */
public class PermissionsUtil {

    private static final int OWNER_READ_FLAG = 0400;
    private static final int OWNER_WRITE_FLAG = 0200;
    private static final int OWNER_EXECUTE_FLAG = 0100;

    private static final int GROUP_READ_FLAG = 0040;
    private static final int GROUP_WRITE_FLAG = 0020;
    private static final int GROUP_EXECUTE_FLAG = 0010;

    private static final int OTHERS_READ_FLAG = 0004;
    private static final int OTHERS_WRITE_FLAG = 0002;
    private static final int OTHERS_EXECUTE_FLAG = 0001;

    public static FilePermission toFilePermission(int mode) {

        int maskedMode = mode & 0777;
        FilePermission filePermission = new FilePermission();

        if ((maskedMode & OWNER_READ_FLAG) > 0)
            filePermission.setOwnerCanRead(true);
        if ((maskedMode & OWNER_WRITE_FLAG) > 0)
            filePermission.setOwnerCanWrite(true);
        if ((maskedMode & OWNER_EXECUTE_FLAG) > 0)
            filePermission.setOwnerCanExecute(true);

        if ((maskedMode & GROUP_READ_FLAG) > 0)
            filePermission.setGroupCanRead(true);
        if ((maskedMode & GROUP_WRITE_FLAG) > 0)
            filePermission.setGroupCanWrite(true);
        if ((maskedMode & GROUP_EXECUTE_FLAG) > 0)
            filePermission.setGroupCanExecute(true);

        if ((maskedMode & OTHERS_READ_FLAG) > 0)
            filePermission.setOthersCanRead(true);
        if ((maskedMode & OTHERS_WRITE_FLAG) > 0)
            filePermission.setOthersCanWrite(true);
        if ((maskedMode & OTHERS_EXECUTE_FLAG) > 0)
            filePermission.setOthersCanExecute(true);

        return filePermission;

    }

    public static void applyPermission(File file, FilePermission permissions) {

        setExecutable(file, permissions.isOwnerCanExecute(),
                !permissions.isGroupCanExecute() && !permissions.isOthersCanExecute());
        setWritable(file, permissions.isOwnerCanWrite(),
                !permissions.isGroupCanWrite() && !permissions.isOthersCanWrite());
        setReadable(file, permissions.isOwnerCanRead(), !permissions.isGroupCanRead() && !permissions.isOthersCanRead());

    }

    private static void setReadable(File file, boolean ownerCanRead, boolean group) {
        file.setReadable(ownerCanRead, group);
    }

    private static void setWritable(File file, boolean ownerCanWrite, boolean group) {
        file.setWritable(ownerCanWrite, group);
    }

    private static void setExecutable(File file, boolean ownerCanExecute, boolean group) {
        file.setExecutable(ownerCanExecute, group);
    }

}
