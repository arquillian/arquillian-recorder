/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
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
package org.arquillian.extension.recorder;

import org.jboss.arquillian.core.spi.event.Event;
import org.jboss.arquillian.test.spi.TestResult;

/**
 * Decides if some action should be taken or not according to some event and test result.
 *
 * @author <a href="mailto:smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public interface RecorderStrategy<T extends Configuration<T>> {

    /**
     *
     * @param configuration configuration to set to this recorder strategy
     */
    void setConfiguration(T configuration);

    /**
     * @param event Arquillian event to decide the action taking for
     * @param result result accompanied with {@code event}
     * @return true if some action should should be taken, false otherwise
     */
    boolean isTakingAction(Event event, TestResult result);

    /**
     *
     * @param event Arquillian event to decide the action taking for
     * @return true if some action should should be taken, false otherwise
     */
    boolean isTakingAction(Event event);

    /**
     * The lower the precedence is, the sooner this strategy is treated
     *
     * @return precedence of some strategy
     */
    int precedence();
}
