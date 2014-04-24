package org.arquillian.spacelift.process.impl;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.arquillian.spacelift.process.ProcessDetails;
import org.arquillian.spacelift.process.Sentence;

class ProcessDetailsImpl implements ProcessDetails {

    private final Process process;
    private final String processName;
    private final List<String> output;

    private final OutputStream ostream;
    private final InputStream istream;

    public ProcessDetailsImpl(Process process, String processName) {
        this.processName = processName;
        this.process = process;
        this.output = new ArrayList<String>();
        this.ostream = new BufferedOutputStream(process.getOutputStream());
        this.istream = process.getInputStream();
    }

    @Override
    public String getProcessName() {
        return processName;
    }

    @Override
    public List<String> getOutput() {
        return output;
    }

    @Override
    public OutputStream getStdin() {
        return ostream;
    }

    @Override
    public InputStream getStdoutAndStdErr() {
        return istream;
    }

    @Override
    public ProcessDetails appendOutput(Sentence line) {
        output.add(line.toString());
        return this;
    }

    @Override
    public Integer getExitValue() throws IllegalStateException {

        try {
            return process.exitValue();
        } catch (IllegalThreadStateException e) {
            throw new IllegalStateException("Process " + processName + " is not yet finished, cannot determine exit value.");
        }

    }
}