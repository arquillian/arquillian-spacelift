package org.arquillian.nativeplatform.process.impl;

import java.io.IOException;
import java.io.OutputStream;

import org.arquillian.nativeplatform.process.Answer;
import org.arquillian.nativeplatform.process.ProcessExecution;

/**
 * Represents an answer that causes process to be treated as finished
 *
 * @see ProcessExecution#isMarkedAsFinished()
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public enum TerminateAnswer implements Answer {

    INSTANCE;

    @Override
    public int length() {
        return 0;
    }

    @Override
    public char charAt(int index) {
        return 0;
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return null;
    }

    @Override
    public void reply(ProcessExecution execution) throws IOException {
        // mark process as finished just in case something might go wrong while working with streams
        execution.markAsFinished();
        OutputStream ostream = execution.getStdin();
        ostream.flush();
        ostream.close();
    }

}
