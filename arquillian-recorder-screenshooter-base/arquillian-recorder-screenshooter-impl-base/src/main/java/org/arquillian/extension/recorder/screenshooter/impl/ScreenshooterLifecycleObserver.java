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

import org.arquillian.extension.recorder.DefaultFileNameBuilder;
import org.arquillian.extension.recorder.When;
import org.arquillian.extension.recorder.screenshooter.Screenshooter;
import org.arquillian.extension.recorder.screenshooter.ScreenshooterConfiguration;
import org.arquillian.extension.recorder.screenshooter.ScreenshootingStrategy;
import org.arquillian.extension.recorder.screenshooter.ScreenshotMetaData;
import org.arquillian.extension.recorder.screenshooter.ScreenshotType;
import org.arquillian.extension.recorder.screenshooter.event.AfterScreenshotTaken;
import org.arquillian.extension.recorder.screenshooter.event.BeforeScreenshotTaken;
import org.arquillian.extension.recorder.screenshooter.event.TakeScreenshot;
import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.TestResult;
import org.jboss.arquillian.test.spi.event.suite.After;
import org.jboss.arquillian.test.spi.event.suite.Before;
import org.jboss.arquillian.test.spi.event.suite.TestLifecycleEvent;

/**
 * @author <a href="mailto:smikloso@redhat.com">Stefan Miklosovic</a>
 * @author <a href="mailto:jhuska@redhat.com">Juraj Huska</a>
 */
public class ScreenshooterLifecycleObserver {

    @Inject
    private Instance<ScreenshootingStrategy> strategy;

    @Inject
    private Instance<ScreenshooterConfiguration> configuration;

    @Inject
    private Event<BeforeScreenshotTaken> beforeScreenshotTaken;

    @Inject
    private Event<TakeScreenshot> takeScreenshot;

    @Inject
    private Event<AfterScreenshotTaken> afterScreenshotTaken;

    @Inject
    private Instance<Screenshooter> screenshooter;

    public void beforeTest(@Observes Before event) {
        if (strategy.get().isTakingAction(event)) {
            ScreenshotMetaData metaData = getMetaData(event);
            metaData.setResourceType(getScreenshotType());

            DefaultFileNameBuilder nameBuilder = DefaultFileNameBuilder.getInstance();
            String screenshotName = nameBuilder
                .withMetaData(metaData)
                .withStage(When.BEFORE)
                .build();

            beforeScreenshotTaken.fire(new BeforeScreenshotTaken(metaData));

            takeScreenshot.fire(new TakeScreenshot(screenshotName, metaData, When.BEFORE));

            afterScreenshotTaken.fire(new AfterScreenshotTaken(metaData));
        }
    }

    public void afterTest(@Observes After event, TestResult result) {
        if (strategy.get().isTakingAction(event, result)) {
            ScreenshotMetaData metaData = getMetaData(event);
            metaData.setTestResult(result);
            metaData.setResourceType(getScreenshotType());

            beforeScreenshotTaken.fire(new BeforeScreenshotTaken(metaData));

            When when = result.getStatus() == TestResult.Status.FAILED ? When.FAILED : When.AFTER;

            DefaultFileNameBuilder nameBuilder = DefaultFileNameBuilder.getInstance();
            String screenshotName = nameBuilder
                .withMetaData(metaData)
                .withStage(when)
                .build();

            takeScreenshot.fire(new TakeScreenshot(screenshotName, metaData, when));

            afterScreenshotTaken.fire(new AfterScreenshotTaken(metaData));
        }
    }

    private ScreenshotMetaData getMetaData(TestLifecycleEvent event) {
        ScreenshotMetaData metaData = new ScreenshotMetaData();

        metaData.setTestClass(event.getTestClass());
        metaData.setTestMethod(event.getTestMethod());
        metaData.setTimeStamp(System.currentTimeMillis());

        return metaData;
    }

    private ScreenshotType getScreenshotType() {
        return screenshooter.get().getScreenshotType();
    }

}
