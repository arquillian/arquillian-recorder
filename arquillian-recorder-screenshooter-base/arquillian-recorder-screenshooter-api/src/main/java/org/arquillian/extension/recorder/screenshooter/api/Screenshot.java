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
package org.arquillian.extension.recorder.screenshooter.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Put this annotation on a test method in order to override global configuration in arquillian.xml locally just for that method
 * you have put this annotation on.
 *
 * @author <a href="mailto:smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Screenshot {

    /**
     * false by default
     *
     * @return true if screenshot should be taken on every action of a browser, false otherwise
     */
    boolean takeOnEveryAction() default false;

    /**
     * false by default
     *
     * @return true if screenshot should be taken before this test method, false otherwise
     */
    boolean takeBeforeTest() default false;

    /**
     * false by default
     *
     * @return true if screenshot should be taken after this test method, false otherwise
     */
    boolean takeAfterTest() default false;

    /**
     * true by default
     *
     * @return true if screenshot should be taken if test method fails, false otherwise
     */
    boolean takeWhenTestFailed() default true;
}
