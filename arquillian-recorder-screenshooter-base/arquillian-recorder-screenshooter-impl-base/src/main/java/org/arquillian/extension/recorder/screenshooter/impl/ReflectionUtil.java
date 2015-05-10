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
package org.arquillian.extension.recorder.screenshooter.impl;

import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:asotobu@gmail.com">Alex Soto</a>
 *
 */
public class ReflectionUtil {

public static Class<?> getClassWithAnnotation(final Class<?> source, final Class<? extends Annotation> annotationClass) {

        Class<?> nextSource = source;
        while (nextSource != Object.class) {
            if(nextSource.isAnnotationPresent(annotationClass)) {
                return nextSource;
            } else {
                nextSource = nextSource.getSuperclass();
            }
        }

        return null;
    }

}
