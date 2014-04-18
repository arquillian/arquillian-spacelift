package org.arquillian.spacelift.execution;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 * @param <OBJECT>
 */
public interface ExecutionCondition<OBJECT> {

    /**
     * Evaluates a condition using {@code object}
     *
     * @param object Object to be inspected
     * @return
     */
    boolean satisfiedBy(OBJECT object) throws ExecutionException;

    ExecutionCondition<OBJECT> timeLeft(long timeout, TimeUnit unit);
}
