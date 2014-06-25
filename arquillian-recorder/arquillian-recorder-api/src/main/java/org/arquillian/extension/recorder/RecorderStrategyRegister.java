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
package org.arquillian.extension.recorder;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author <a href="mailto:smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class RecorderStrategyRegister {

    private final Set<RecorderStrategy<?>> recorderStrategies = new TreeSet<RecorderStrategy<?>>(new RecorderStrategyComparator());

    public void add(RecorderStrategy<?> recorderStrategy) {
        this.recorderStrategies.add(recorderStrategy);
    }

    public void addAll(Set<RecorderStrategy<?>> recorderStrategies) {
        this.recorderStrategies.addAll(recorderStrategies);
    }

    public void clear() {
        recorderStrategies.clear();
    }

    public void size() {
        recorderStrategies.size();
    }

    public RecorderStrategy<?> get(int precedence) {
        for (final RecorderStrategy<?> strategy : recorderStrategies) {
            if (strategy.precedence() == precedence) {
                return strategy;
            }
        }

        return null;
    }

    public Set<RecorderStrategy<?>> getAll() {
        return Collections.unmodifiableSet(recorderStrategies);
    }
}
