/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.arquillian.nativeplatform.process;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

/**
 *
 * @author <a href="@mailto:smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class CommandTestCase {

    @Test
    public void constructEmptyCommandTest() {

        CommandBuilder cb = new CommandBuilder();
        Command command = cb.build();
        Command command2 = cb.build();

        assertThat(command, not(sameInstance(command2)));

        assertThat(command, notNullValue());
        assertEquals(0, command.size());
        assertThat(command.get(0), nullValue());
    }

    @Test
    public void commandListSeparationTest() {

        CommandBuilder cb = new CommandBuilder();
        Command command = cb.build();
        Command command2 = cb.add("some").add("command").build();

        assertThat(command, not(sameInstance(command2)));
        assertEquals(0, command.size());
        assertEquals(2, command2.size());
    }

    @Test
    public void complexCommandTest() {
        Command command = new CommandBuilder()
            .add(Collections.<String> emptyList())
            .add("some")
            .add("command")
            .clear()
            .add(new String[] { "some", "other", "complex", "command" })
            .remove("complex")
            .build();

        assertEquals(3, command.size());
        assertEquals("some", command.getFirst());
        assertEquals("command", command.getLast());
        assertThat(command.get(command.size() + 5), nullValue());
    }

    @Test
    public void testDeleteTrailingSpaces() {
        String testString = " abcd   \"  a   \"  \"    c    d\" \"${HOME}\"";

        List<String> list = new ArrayList<String>();
        list.add("abcd");
        list.add("  a   ");
        list.add("    c    d");
        list.add("${HOME}");

        assertTrue(listsAreSame(list, new CommandBuilder().addTokenized(testString).build().getAsList()));
    }

    private boolean listsAreSame(List<String> list1, List<String> list2) {

        if (list1 == null && list2 == null) {
            return true;
        }

        if (list1 != null && list2 != null) {
            if (list1.size() != list2.size()) {
                return false;
            }
            for (int i = 0; i < list1.size(); i++) {
                if (!list1.get(i).equals(list2.get(i))) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }
}
