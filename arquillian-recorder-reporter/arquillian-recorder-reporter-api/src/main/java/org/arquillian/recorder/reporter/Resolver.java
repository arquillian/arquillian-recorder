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
package org.arquillian.recorder.reporter;

/**
 * Arbitrary resolver which follows "question-answer" model.
 *
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public interface Resolver {

    /**
     *
     * @param toResolve string to resolve to boolean
     * @return when you ask some question {@code toResolve}, it returns answer as boolean.
     */
    boolean resolveAsBoolean(String toResolve);

    /**
     *
     * @param toResolve string to resolve into another string
     * @return when you ask some question {@code toResolve}, it returns answer as String.
     */
    String resolveAsString(String toResolve);
}
