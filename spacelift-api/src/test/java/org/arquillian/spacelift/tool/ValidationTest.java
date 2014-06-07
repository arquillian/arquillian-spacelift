/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.arquillian.spacelift.tool;

import org.arquillian.spacelift.execution.ExecutionService;
import org.arquillian.spacelift.execution.ExecutionServiceFactory;
import org.arquillian.spacelift.execution.ValidationException;
import org.arquillian.spacelift.execution.Task;
import org.arquillian.spacelift.execution.Tasks;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author <a href="mailto:smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class ValidationTest {

    private static final class FakeTask extends Task<Object, Void> {

        private String variable;

        public FakeTask variable(String variable) {
            this.variable = variable;
            return this;
        }

        @Override
        protected Void process(Object input) throws Exception {
            return null;
        }

        @Override
        protected void validate() throws ValidationException {
            if (variable == null) {
                throw new ValidationException("You have not set any value to variable.");
            }
        }

    }

    @BeforeClass
    public static void setup() {
        Tasks.setDefaultExecutionServiceFactory(new ExecutionServiceFactory() {
            @Override
            public ExecutionService getExecutionServiceInstance() {
                return new TestExecutionService();
            }
        });
    }

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void invalidTaskTest() {
        exception.expect(ValidationException.class);

        Tasks.prepare(FakeTask.class).execute().await();
    }

    @Test
    public void validTaskTest() {
        Tasks.prepare(FakeTask.class).variable("someValue").execute().await();
    }
}
