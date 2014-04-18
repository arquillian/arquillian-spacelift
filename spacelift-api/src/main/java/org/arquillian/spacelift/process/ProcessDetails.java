package org.arquillian.spacelift.process;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.arquillian.spacelift.execution.Sentence;

public interface ProcessDetails {

    /**
     * Returns name of the process
     *
     * @return process name
     */
    String getProcessName();

    /**
     * Returns stdout and stderr of the process in the form of array
     *
     * @return current output of the process
     */
    List<String> getOutput();

    /**
     * Returns stdin of the process
     *
     * @return
     */
    OutputStream getStdin();

    /**
     * Returns stdout combined with stderr of the process
     *
     * @return
     */
    InputStream getStdoutAndStdErr();

    /**
     * Adds a line to output of this process
     *
     * @param line
     * @return this
     */
    ProcessDetails appendOutput(Sentence line);
}