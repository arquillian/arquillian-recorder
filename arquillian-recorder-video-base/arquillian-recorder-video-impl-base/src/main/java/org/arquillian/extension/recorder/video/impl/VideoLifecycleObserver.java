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
package org.arquillian.extension.recorder.video.impl;

import org.arquillian.extension.recorder.video.Recorder;
import org.arquillian.extension.recorder.video.VideoMetaData;
import org.arquillian.extension.recorder.video.VideoStrategy;
import org.arquillian.extension.recorder.video.VideoType;
import org.arquillian.extension.recorder.video.event.AfterVideoStart;
import org.arquillian.extension.recorder.video.event.AfterVideoStop;
import org.arquillian.extension.recorder.video.event.BeforeVideoStart;
import org.arquillian.extension.recorder.video.event.BeforeVideoStop;
import org.arquillian.extension.recorder.video.event.StartRecordClassVideo;
import org.arquillian.extension.recorder.video.event.StartRecordSuiteVideo;
import org.arquillian.extension.recorder.video.event.StartRecordVideo;
import org.arquillian.extension.recorder.video.event.StopRecordClassVideo;
import org.arquillian.extension.recorder.video.event.StopRecordSuiteVideo;
import org.arquillian.extension.recorder.video.event.StopRecordVideo;
import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.TestResult;
import org.jboss.arquillian.test.spi.event.suite.After;
import org.jboss.arquillian.test.spi.event.suite.AfterClass;
import org.jboss.arquillian.test.spi.event.suite.AfterSuite;
import org.jboss.arquillian.test.spi.event.suite.Before;
import org.jboss.arquillian.test.spi.event.suite.BeforeClass;
import org.jboss.arquillian.test.spi.event.suite.BeforeSuite;
import org.jboss.arquillian.test.spi.event.suite.ClassLifecycleEvent;
import org.jboss.arquillian.test.spi.event.suite.TestLifecycleEvent;

/**
 * @author <a href="mailto:smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class VideoLifecycleObserver {

    @Inject
    private Instance<TestResult> testResult;

    @Inject
    private Instance<VideoStrategy> strategy;

    @Inject
    private Instance<Recorder> recorder;

    @Inject
    private Event<BeforeVideoStart> beforeVideoStart;

    @Inject
    private Event<AfterVideoStart> afterVideoStart;

    @Inject
    private Event<BeforeVideoStop> beforeVideoStop;

    @Inject
    private Event<AfterVideoStop> afterVideoStop;

    @Inject
    private Event<StartRecordVideo> startRecordVideo;

    @Inject
    private Event<StopRecordVideo> stopRecordVideo;

    @Inject
    private Event<StartRecordClassVideo> startRecordClassVideo;

    @Inject
    private Event<StopRecordClassVideo> stopRecordClassVideo;

    @Inject
    private Event<StartRecordSuiteVideo> startRecordSuiteVideo;

    @Inject
    private Event<StopRecordSuiteVideo> stopRecordSuiteVideo;

    public void beforeSuite(@Observes BeforeSuite event) {
        if (strategy.get().isTakingAction(event)) {
            VideoMetaData suiteMetaData = getMetaData();
            VideoType videoType = getVideoType();

            beforeVideoStart.fire(new BeforeVideoStart(videoType, suiteMetaData));

            startRecordSuiteVideo.fire(new StartRecordSuiteVideo(videoType, suiteMetaData));

            afterVideoStart.fire(new AfterVideoStart(videoType, suiteMetaData));
        }
    }

    public void beforeClass(@Observes BeforeClass event) {
        if (strategy.get().isTakingAction(event)) {
            VideoMetaData classMetaData = getClassMetaData(event);
            VideoType videoType = getVideoType();

            beforeVideoStart.fire(new BeforeVideoStart(videoType, classMetaData));

            startRecordClassVideo.fire(new StartRecordClassVideo(videoType, classMetaData));

            afterVideoStart.fire(new AfterVideoStart(videoType, classMetaData));
        }
    }

    public void beforeTest(@Observes Before event) {
        if (strategy.get().isTakingAction(event)) {
            VideoMetaData testMetaData = getMetaData(event);
            VideoType videoType = getVideoType();

            beforeVideoStart.fire(new BeforeVideoStart(videoType, testMetaData));

            startRecordVideo.fire(new StartRecordVideo(videoType, testMetaData));

            afterVideoStart.fire(new AfterVideoStart(videoType, testMetaData));
        }
    }

    public void afterTest(@Observes After event) {
        if (strategy.get().isTakingAction(event, testResult.get())) {
            VideoMetaData metaData = getMetaData(event);
            metaData.setTestResult(testResult.get());
            VideoType videoType = getVideoType();

            beforeVideoStop.fire(new BeforeVideoStop(videoType, metaData));

            stopRecordVideo.fire(new StopRecordVideo(videoType, metaData));

            afterVideoStop.fire(new AfterVideoStop(videoType, metaData));
        }
    }

    public void afterClass(@Observes AfterClass event) {
        if (strategy.get().isTakingAction(event)) {
            VideoMetaData metaData = getClassMetaData(event);
            VideoType videoType = getVideoType();

            beforeVideoStop.fire(new BeforeVideoStop(videoType, metaData));

            stopRecordClassVideo.fire(new StopRecordClassVideo(videoType, metaData));

            afterVideoStop.fire(new AfterVideoStop(videoType, metaData));
        }
    }

    public void afterSuite(@Observes AfterSuite event) {
        if (strategy.get().isTakingAction(event)) {
            VideoMetaData metaData = getMetaData();
            VideoType videoType = getVideoType();

            beforeVideoStop.fire(new BeforeVideoStop(videoType, metaData));

            stopRecordSuiteVideo.fire(new StopRecordSuiteVideo(videoType, metaData));

            afterVideoStop.fire(new AfterVideoStop(videoType, metaData));
        }
    }

    private VideoMetaData getMetaData() {
        VideoMetaData metaData = new VideoMetaData();
        metaData.setTimeStamp(System.currentTimeMillis());
        return metaData;
    }

    private VideoMetaData getMetaData(TestLifecycleEvent event) {
        VideoMetaData metaData = getMetaData();
        metaData.setTestClass(event.getTestClass());
        metaData.setTestMethod(event.getTestMethod());
        return metaData;
    }

    private VideoMetaData getClassMetaData(ClassLifecycleEvent event) {
        VideoMetaData metaData = getMetaData();
        metaData.setTestClass(event.getTestClass());
        return metaData;
    }

    private VideoType getVideoType() {
        return recorder.get().getVideoType();
    }
}
