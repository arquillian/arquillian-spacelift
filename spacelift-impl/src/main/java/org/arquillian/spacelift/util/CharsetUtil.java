/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
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
package org.arquillian.spacelift.util;

import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

/**
 * An utility class for resolving charsets on Java lower than 1.7.
 */
public class CharsetUtil {

    public static final String UTF8_NAME = "utf-8";

    /**
     * Simply returns the value of {@link Charset#forName(String)} with value of {@link #UTF8_NAME}.
     *
     * @throws UnsupportedCharsetException
     */
    public static Charset getUtf8() throws UnsupportedCharsetException {
        return Charset.forName(UTF8_NAME);
    }

    /**
     * Returns the value of {@link #getUtf8()} if UTF8 is supported. Returns {@link Charset#defaultCharset()} otherwise.
     */
    public static Charset getUtf8OrDefault() {
        if (Charset.isSupported(UTF8_NAME)) {
            return getUtf8();
        } else {
            return Charset.defaultCharset();
        }
    }
}
