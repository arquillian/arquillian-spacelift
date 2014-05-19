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

import java.util.Arrays;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;

import static org.junit.Assert.assertThat;

/**
 *
 * @author <a href="@mailto:smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class CommandTest {

    @Test
    public void constructCommandWithoutParametersTest() {

        CommandBuilder cb = new CommandBuilder("command");
        Command command = cb.build();
        Command command2 = cb.build();

        assertThat(command, not(sameInstance(command2)));

        assertThat(command, notNullValue());
        assertThat(command.getNumberOfParameters(), equalTo(0));
        assertThat(command.getParameter(1), nullValue());
    }

    @Test
    public void commandListSeparationTest() {

        CommandBuilder cb = new CommandBuilder("command");
        Command command = cb.build();
        Command command2 = cb.parameter("some").parameter("command").build();

        assertThat(command, not(sameInstance(command2)));
        assertThat(command.getNumberOfParameters(), equalTo(0));
        assertThat(command2.getNumberOfParameters(), equalTo(2));
    }

    @Test
    public void complexCommandTest() {
        Command command = new CommandBuilder("command")
            .parameters(new String[] { "some", "other", "complex", "command" })
            .build();

        assertThat(command.getNumberOfParameters(), equalTo(4));
        assertThat(command.getParameter(1), equalTo("some"));
        assertThat(command.getParameter(command.getNumberOfParameters()), equalTo("command"));
        assertThat(command.getParameter(command.getNumberOfParameters() + 5), nullValue());
    }

    @Test
    public void testDeleteTrailingSpaces() {
        String testString = " abcd   \"  a   \"  \"    c    d\" \"${HOME}\"";

        Command c = new CommandBuilder("command").splitToParameters(testString).build();
        assertThat(c.getParameters(), hasItems("abcd", "  a   ", "    c    d", "${HOME}"));
    }

    @Test
    public void testAddingStringBuilder() {
        StringBuilder sb = new StringBuilder("some");
        Command command = new CommandBuilder("command").parameter(sb).build();

        assertThat(command.getNumberOfParameters(), equalTo(1));
        assertThat(command.getParameter(1), equalTo("some"));
    }

    @Test
    public void testAddingStringBuilders() {
        StringBuilder sb = new StringBuilder("some");
        StringBuilder sb2 = new StringBuilder("someother");
        Command command = new CommandBuilder("command").parameters(Arrays.asList(sb, sb2)).build();

        assertThat(command.getNumberOfParameters(), equalTo(2));
        assertThat(command.getParameter(1), equalTo("some"));
        assertThat(command.getParameter(2), equalTo("someother"));
    }

    @Test
    public void testAddingStringBuilderTokenized() {
        StringBuilder sb = new StringBuilder("some").append(" ").append(" someother");
        Command command = new CommandBuilder("command").splitToParameters(sb).build();

        assertThat(command.getNumberOfParameters(), equalTo(2));
        assertThat(command.getParameter(1), equalTo("some"));
        assertThat(command.getParameter(2), equalTo("someother"));
    }

    @Test
    public void testOutputTokenized() {
        Command command = new CommandBuilder("command").splitToParameters("foo bar").build();
        assertThat(command.toString(), equalTo("command foo bar"));
    }

    @Test
    public void testOutputEscaped() {
        Command command = new CommandBuilder("foo bar").build();
        assertThat(command.toString(), equalTo("\"foo bar\""));
    }

    @Test
    public void testOutputCombined() {
        Command command = new CommandBuilder("java")
            .parameter("/path/to/my dir with spaces")
            .parameter("foo bar")
            .splitToParameters("-Dbar=foo -Dfoo=bar")
            .build();
        assertThat(command.toString(), equalTo("java \"/path/to/my dir with spaces\" \"foo bar\" -Dbar=foo -Dfoo=bar"));
    }

    @Test
    public void testBothConstructionApproachesAreEqual() {
        Command command = new CommandBuilder("java", "-foo", "-bar").build();
        Command command2 = new CommandBuilder("java").parameters("-foo", "-bar").build();

        assertThat(command.toString(), equalTo(command2.toString()));
    }

    @Test
    public void cloneBuilders() {
        CommandBuilder command = new CommandBuilder("java", "-foo", "-bar");
        CommandBuilder command2 = new CommandBuilder(command);

        assertThat(command.toString(), equalTo(command2.toString()));

        command.parameter("-baz");

        assertThat(command.toString(), not(equalTo(command2.toString())));

    }

}
