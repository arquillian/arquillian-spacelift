package org.arquillian.spacelift.process.impl;

import org.arquillian.spacelift.execution.ExecutionException;

/**
 * Holder of executed process that can be filled later on
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
class ProcessReference {

    private volatile Process process;

    private final String processName;

    public ProcessReference(String processName) {
        this.processName = processName;
    }

    public boolean isInitialized() {
        return process != null;
    }

    public void setProcess(Process process) {
        if (process == null) {
            throw new ExecutionException("Unable to SET SET process object for {0}. This is a bug in Arquillian Spacelift.",
                processName);
        }
        this.process = process;
    }

    public synchronized Process getProcess() throws ExecutionException {

        if (!isInitialized()) {
            throw new ExecutionException("Unable to get {0} process identifier in ref {1}. This is a bug in Arquillian Spacelift.",
                processName,
                hashCode());
        }

        return process;
    }
}
