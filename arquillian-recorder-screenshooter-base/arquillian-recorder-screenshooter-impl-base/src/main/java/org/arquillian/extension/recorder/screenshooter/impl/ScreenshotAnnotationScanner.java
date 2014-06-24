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
package org.arquillian.extension.recorder.screenshooter.impl;

import java.lang.reflect.Method;

import org.arquillian.extension.recorder.screenshooter.api.Screenshot;

/**
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class ScreenshotAnnotationScanner {

    /**
     *
     * @param testMethod test method to get {@link Screenshot} annotation from
     * @return screenshot annotation or null if not put on the {@code testMethod}
     */
    public static Screenshot getScreenshotAnnotation(Method testMethod) {
        return testMethod.getAnnotation(Screenshot.class);
    }
}
