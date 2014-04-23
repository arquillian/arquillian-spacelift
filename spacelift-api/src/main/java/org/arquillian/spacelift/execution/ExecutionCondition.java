package org.arquillian.spacelift.execution;

/**
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 * @param <RESULT>
 */
public interface ExecutionCondition<RESULT> {

    /**
     * Evaluates a condition using {@code object}
     *
     * @param object Object to be inspected
     * @return
     */
    boolean satisfiedBy(RESULT object) throws ExecutionException;

}
