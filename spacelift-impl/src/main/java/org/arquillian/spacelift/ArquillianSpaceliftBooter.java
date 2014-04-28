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
package org.arquillian.spacelift;

import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;

/**
 * Entry point for Arquillian Spacelift extension.<br>
 * <br>
 * Observes:
 * <ul>
 * <li>{@link ArquillianDescriptor}</li>
 * </ul>
 * Fires:
 * <ul>
 * <li>{@link SpaceliftBootstrap}</li>
 * </ul>
 *
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
 *
 */
public class ArquillianSpaceliftBooter {

    @Inject
    private Event<SpaceliftBootstrap> spaceliftBootstrap;

    public void onArquillianDescriptor(@Observes ArquillianDescriptor event) {
        spaceliftBootstrap.fire(new SpaceliftBootstrap());
    }
}
