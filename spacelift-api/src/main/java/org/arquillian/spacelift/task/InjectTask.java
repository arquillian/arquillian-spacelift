package org.arquillian.spacelift.task;

public class InjectTask<NEXT_IN> extends Task<Object, NEXT_IN> {

    private NEXT_IN nextIn;

    public InjectTask<NEXT_IN> passToNext(NEXT_IN next) {
        this.nextIn = next;
        return this;
    }

    @Override
    protected NEXT_IN process(Object input) throws Exception {
        return nextIn;
    }
}
