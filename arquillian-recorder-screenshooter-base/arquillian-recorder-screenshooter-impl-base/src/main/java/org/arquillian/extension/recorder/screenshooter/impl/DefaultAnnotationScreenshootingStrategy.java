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

import org.arquillian.extension.recorder.screenshooter.AnnotationScreenshootingStrategy;
import org.arquillian.extension.recorder.screenshooter.ScreenshooterConfiguration;
import org.arquillian.extension.recorder.screenshooter.api.Screenshot;
import org.jboss.arquillian.core.spi.Validate;
import org.jboss.arquillian.core.spi.event.Event;
import org.jboss.arquillian.test.spi.TestResult;
import org.jboss.arquillian.test.spi.TestResult.Status;
import org.jboss.arquillian.test.spi.event.suite.After;
import org.jboss.arquillian.test.spi.event.suite.Before;

/**
 * This strategy is treated after {@link DefaultScreenshootingStrategy} because of higher precedence. It overrides global
 * configuration by placing {@link Screenshot} annotation on a test method.
 *
 * @author <a href="mailto:smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class DefaultAnnotationScreenshootingStrategy implements AnnotationScreenshootingStrategy {

    private ScreenshooterConfiguration configuration;

    @Override
    public void setConfiguration(ScreenshooterConfiguration configuration) {
        Validate.notNull(configuration, "Screenshooter configuration is a null object!");
        this.configuration = configuration;
    }

    @Override
    public boolean isTakingAction(Event event, TestResult result) {
        if (event instanceof After) {
            Screenshot screenshotAnnotation = ScreenshotAnnotationScanner.getScreenshotAnnotation(((After) event).getTestMethod());

            if (screenshotAnnotation != null) {
                if (screenshotAnnotation.takeAfterTest()) {
                    return true;
                }
                if (result.getStatus() == Status.FAILED && screenshotAnnotation.takeWhenTestFailed()) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean isTakingAction(Event event) {

        if (event instanceof Before) {
            Screenshot screenshotAnnotation = ScreenshotAnnotationScanner.getScreenshotAnnotation(((Before) event).getTestMethod());

            if (screenshotAnnotation != null) {
                return screenshotAnnotation.takeBeforeTest();
            }
        }

        return false;
    }

    @Override
    public int precedence() {
        return 1;
    }

}
