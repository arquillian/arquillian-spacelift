package org.arquillian.spacelift;

import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;

public class ArquillianSpaceliftBooter {

    @Inject
    private Event<SpaceliftBootstrap> spaceliftBootstrap;

    public void createProcessExecutor(@Observes ArquillianDescriptor event) {
        spaceliftBootstrap.fire(new SpaceliftBootstrap());
    }
}
