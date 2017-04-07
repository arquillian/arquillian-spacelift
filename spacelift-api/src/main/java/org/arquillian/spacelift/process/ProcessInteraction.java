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
package org.arquillian.spacelift.process;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * An object that encapsulates interaction with process.
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 */
public interface ProcessInteraction {

    /**
     * Returns text that is typed in after process is started, might be {@code null}
     */
    String textTypedIn();

    /**
     * Returns a map that defines what should be written to stdin of running process based on its stdout
     */
    Map<Pattern, String> replyMap();

    /**
     * Returns a list of patterns that are propagated to standard output of process running this process via Spacelift
     */
    List<Pattern> allowedOutput();

    /**
     * Returns a list of patterns that are propagated to error output of process running this process via Spacelift
     */
    List<Pattern> errorOutput();

    /**
     * Returns a list of patterns that cause process to terminate forcefully
     */
    List<Pattern> terminatingOutput();

    /**
     * Returns a transformer that can modify output printed to both standard and error output. Might be null to indicate
     * that
     * default {@link OutputTransformer} should be used
     */
    OutputTransformer transformer();
}
