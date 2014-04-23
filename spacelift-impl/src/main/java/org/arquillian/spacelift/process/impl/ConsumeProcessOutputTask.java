package org.arquillian.spacelift.process.impl;

import org.arquillian.spacelift.execution.Task;
import org.arquillian.spacelift.process.ProcessDetails;

class ConsumeProcessOutputTask extends Task<Process, ProcessDetails> {

    @Override
    protected ProcessDetails process(Process input) throws Exception {

        System.out.println("EXIT VALUE " + input.exitValue());

        return null;
    }

}
