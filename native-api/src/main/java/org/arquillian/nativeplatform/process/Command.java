/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.arquillian.nativeplatform.process;

import java.util.List;

/**
 * Command abstraction. Commands are built via {@link CommandBuilder}.
 *
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public interface Command {

    /**
     *
     * @return number of tokens stored in the command
     */
    public int size();

    /**
     *
     * @return command as a list
     */
    public List<String> getAsList();

    /**
     *
     * @return command as an array
     */
    public String[] getAsArray();

    /**
     * Returns token on i-th position
     *
     * @param i position of token we want to get
     * @return token on i-th position or null if out of bounds
     */
    public String get(int i);

    /**
     *
     * @return last token from the command or null if command is empty
     */
    public String getLast();

    /**
     *
     * @return first token from the command or null if command is empty
     */
    public String getFirst();

}
