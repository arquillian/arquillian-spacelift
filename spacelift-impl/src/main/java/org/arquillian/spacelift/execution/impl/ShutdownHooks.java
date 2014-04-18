package org.arquillian.spacelift.execution.impl;

import org.arquillian.spacelift.execution.Execution;

public class ShutdownHooks {

    public static <RETURNTYPE> void addHookFor(final Execution<RETURNTYPE> execution) {

        Thread shutdownThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (execution != null) {
                    if (!execution.isFinished() && !execution.isMarkedAsFinished()) {
                        execution.terminate();
                    }
                }
            }
        });
        Runtime.getRuntime().addShutdownHook(shutdownThread);
    }
}
