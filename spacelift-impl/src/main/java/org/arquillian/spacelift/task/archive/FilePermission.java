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

/**
 * File permission
 *
 * @author <a href="asotobu@gmail.com">Alex Soto</a>
 */
public class FilePermission {

    private boolean ownerCanRead;
    private boolean ownerCanWrite;
    private boolean ownerCanExecute;

    private boolean groupCanRead;
    private boolean groupCanWrite;
    private boolean groupCanExecute;

    private boolean othersCanRead;
    private boolean othersCanWrite;
    private boolean othersCanExecute;

    public boolean isOwnerCanRead() {
        return ownerCanRead;
    }

    public void setOwnerCanRead(boolean ownerCanRead) {
        this.ownerCanRead = ownerCanRead;
    }

    public boolean isOwnerCanWrite() {
        return ownerCanWrite;
    }

    public void setOwnerCanWrite(boolean ownerCanWrite) {
        this.ownerCanWrite = ownerCanWrite;
    }

    public boolean isOwnerCanExecute() {
        return ownerCanExecute;
    }

    public void setOwnerCanExecute(boolean ownerCanExecute) {
        this.ownerCanExecute = ownerCanExecute;
    }

    public boolean isGroupCanRead() {
        return groupCanRead;
    }

    public void setGroupCanRead(boolean groupCanRead) {
        this.groupCanRead = groupCanRead;
    }

    public boolean isGroupCanWrite() {
        return groupCanWrite;
    }

    public void setGroupCanWrite(boolean groupCanWrite) {
        this.groupCanWrite = groupCanWrite;
    }

    public boolean isGroupCanExecute() {
        return groupCanExecute;
    }

    public void setGroupCanExecute(boolean groupCanExecute) {
        this.groupCanExecute = groupCanExecute;
    }

    public boolean isOthersCanRead() {
        return othersCanRead;
    }

    public void setOthersCanRead(boolean othersCanRead) {
        this.othersCanRead = othersCanRead;
    }

    public boolean isOthersCanWrite() {
        return othersCanWrite;
    }

    public void setOthersCanWrite(boolean othersCanWrite) {
        this.othersCanWrite = othersCanWrite;
    }

    public boolean isOthersCanExecute() {
        return othersCanExecute;
    }

    public void setOthersCanExecute(boolean othersCanExecute) {
        this.othersCanExecute = othersCanExecute;
    }
}
