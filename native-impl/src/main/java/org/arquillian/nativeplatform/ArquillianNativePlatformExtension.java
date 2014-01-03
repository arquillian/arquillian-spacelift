package org.arquillian.nativeplatform;

import org.arquillian.nativeplatform.process.ProcessExecutorFactory;
import org.arquillian.nativeplatform.process.impl.DefaultProcessExecutionFactory;
import org.arquillian.nativeplatform.process.impl.ProcessExecutorCreator;
import org.jboss.arquillian.core.spi.LoadableExtension;

public class ArquillianNativePlatformExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder extension) {

        extension.service(ProcessExecutorFactory.class, DefaultProcessExecutionFactory.class);
        extension.observer(ProcessExecutorCreator.class);
    }

}
