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
package org.arquillian.spacelift.process;

import static org.junit.Assert.*;

import static org.hamcrest.CoreMatchers.*;

import java.util.Arrays;
import java.util.Collections;

import org.arquillian.spacelift.process.Command;
import org.arquillian.spacelift.process.CommandBuilder;
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
        assertThat(command.size(), equalTo(0));
        assertThat(command.get(0), nullValue());
    }

    @Test
    public void commandListSeparationTest() {

        CommandBuilder cb = new CommandBuilder();
        Command command = cb.build();
        Command command2 = cb.add("some").add("command").build();

        assertThat(command, not(sameInstance(command2)));
        assertThat(command.size(), equalTo(0));
        assertThat(command2.size(), equalTo(2));
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

        assertThat(command.size(), equalTo(3));
        assertThat(command.getFirst(), equalTo("some"));
        assertThat(command.getLast(), equalTo("command"));
        assertThat(command.get(command.size() + 5), nullValue());
    }

    @Test
    public void testDeleteTrailingSpaces() {
        String testString = " abcd   \"  a   \"  \"    c    d\" \"${HOME}\"";

        Command c = new CommandBuilder().addTokenized(testString).build();
        assertThat(c.getAsList(), hasItems("abcd", "  a   ", "    c    d", "${HOME}"));
    }

    @Test
    public void testAddingStringBuilder() {
        StringBuilder sb = new StringBuilder("some");
        Command command = new CommandBuilder().add(sb).build();

        assertThat(command.size(), equalTo(1));
        assertThat(command.getFirst(), equalTo("some"));
    }

    @Test
    public void testAddingStringBuilders() {
        StringBuilder sb = new StringBuilder("some");
        StringBuilder sb2 = new StringBuilder("someother");
        Command command = new CommandBuilder().add(Arrays.asList(sb, sb2)).build();

        assertThat(command.size(), equalTo(2));
        assertThat(command.getFirst(), equalTo("some"));
        assertThat(command.getLast(), equalTo("someother"));
    }

    @Test
    public void testAddingStringBuilderTokenized() {
        StringBuilder sb = new StringBuilder("some").append(" ").append(" someother");
        Command command = new CommandBuilder().addTokenized(sb).build();

        assertThat(command.size(), equalTo(2));
        assertThat(command.getFirst(), equalTo("some"));
        assertThat(command.getLast(), equalTo("someother"));
    }

}
