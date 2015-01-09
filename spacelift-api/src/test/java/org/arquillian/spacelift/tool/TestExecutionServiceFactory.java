package org.arquillian.spacelift.tool;

import org.arquillian.spacelift.execution.ExecutionService;
import org.arquillian.spacelift.execution.ExecutionServiceFactory;

/**
 * A very simple {@link ExecutionServiceFactory} that is used to initialized Spacelift
 * for API tests.
 *
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class TestExecutionServiceFactory implements ExecutionServiceFactory {

    @Override
    public ExecutionService getExecutionServiceInstance() {
        return new TestExecutionService();
    }
}
