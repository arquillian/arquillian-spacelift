package org.arquillian.spacelift.execution;

import org.arquillian.spacelift.process.Command;
import org.arquillian.spacelift.process.CommandBuilder;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by kpiwko on 07/08/15.
 */
public class ExecutionExceptionTest {

    @Test
    public void prependMessage() {
        ExecutionException e = new ExecutionException("{0}", "test");
        e = e.prependMessage("foobar");
        assertThat(e.getMessage(), is("foobar. test"));
    }

    @Test
    public void prependMessageWithParams() {
        ExecutionException e = new ExecutionException("{0}", "test");
        e = e.prependMessage("{0}, {1}", "foo", "bar");
        assertThat(e.getMessage(), is("foo, bar. test"));
    }

    @Test
    public void prependMessageWithCurlyBraces() {
        ExecutionException e = new ExecutionException("{0}", "test");
        e = e.prependMessage("{0}", "{ ERR: err }");
        assertThat(e.getMessage(), is("{ ERR: err }. test"));
    }

    @Test
    public void multiplePrependMessage() {
        ExecutionException e = new ExecutionException("{0}", "test");
        for(int i=0; i < 5; i++) {
            ExecutionException e2 = e.prependMessage("{0}", "{ ERR: err }");
            assertThat(e2.getMessage(), is("{ ERR: err }. " + e.getMessage()));
            e = e2;
        }
    }
}
