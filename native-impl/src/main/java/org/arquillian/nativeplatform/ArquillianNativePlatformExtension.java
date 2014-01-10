/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.arquillian.nativeplatform;

import org.arquillian.nativeplatform.process.ProcessExecutorFactory;
import org.arquillian.nativeplatform.process.enricher.ProcessExecutorResourceProvider;
import org.arquillian.nativeplatform.process.impl.DefaultProcessExecutorFactory;
import org.arquillian.nativeplatform.process.impl.ProcessExecutorCreator;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

/**
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class ArquillianNativePlatformExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder extension) {

        extension.service(ProcessExecutorFactory.class, DefaultProcessExecutorFactory.class);
        extension.service(ResourceProvider.class, ProcessExecutorResourceProvider.class);
        extension.observer(ProcessExecutorCreator.class);
    }

}
