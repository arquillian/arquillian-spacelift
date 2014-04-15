package org.arquillian.spacelift.tool;

import java.util.concurrent.Callable;

import org.arquillian.spacelift.process.ProcessExecutor;

/**
 * Representation of the tool that does not require an external command to do the work, that is
 * it is able to do all the work using Java calls
 *
 * @see ProcessExecutor
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 * @param <TOOLTYPE> Type of the tool
 *
 */
public interface InternalTool<TOOLTYPE extends InternalTool<TOOLTYPE, RESULTTYPE>, RESULTTYPE> extends Tool<TOOLTYPE> {

    /**
     * Returns callable representing this internal call
     *
     * @return
     */
    Callable<RESULTTYPE> getCallable();
}
