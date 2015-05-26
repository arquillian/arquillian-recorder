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
package org.arquillian.extension.recorder.video;

import java.io.File;
import java.util.List;

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
import org.arquillian.extension.recorder.video.event.VideoExtensionConfigured;
import org.arquillian.extension.recorder.video.impl.DefaultVideoRecorderEnvironmentCleaner;
import org.arquillian.extension.recorder.video.impl.DefaultVideoStrategy;
import org.arquillian.extension.recorder.video.impl.VideoLifecycleObserver;
import org.arquillian.extension.recorder.video.impl.VideoRecorderExtensionInitializer;
import org.arquillian.extension.recorder.video.impl.VideoTaker;
import org.arquillian.recorder.reporter.ReporterConfiguration;
import org.arquillian.recorder.reporter.event.PropertyReportEvent;
import org.arquillian.recorder.reporter.impl.TakenResourceRegister;
import org.jboss.arquillian.core.api.Injector;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.spi.ServiceLoader;
import org.jboss.arquillian.test.spi.TestResult;
import org.jboss.arquillian.test.spi.annotation.TestScoped;
import org.jboss.arquillian.test.spi.event.suite.After;
import org.jboss.arquillian.test.spi.event.suite.AfterClass;
import org.jboss.arquillian.test.spi.event.suite.AfterSuite;
import org.jboss.arquillian.test.spi.event.suite.Before;
import org.jboss.arquillian.test.spi.event.suite.BeforeClass;
import org.jboss.arquillian.test.spi.event.suite.BeforeSuite;
import org.jboss.arquillian.test.test.AbstractTestTestBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author <a href="mailto:smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class RecorderLifecycleObserverTestCase extends AbstractTestTestBase {

    @Mock
    private ServiceLoader serviceLoader;

    @Mock
    private VideoConfiguration configuration = new VideoConfiguration(new ReporterConfiguration());

    @Mock
    private Recorder recorder;

    @Mock
    private VideoRecorderEnvironmentCleaner cleaner = new DefaultVideoRecorderEnvironmentCleaner();

    @Mock
    private Video video = new FakeVideo();

    private File videoFile;

    private VideoStrategy strategy = new DefaultVideoStrategy();

    private TakenResourceRegister takenResourceRegister = new TakenResourceRegister();

    @Inject
    private Instance<Injector> injector;

    @Override
    public void addExtensions(List<Class<?>> extensions) {
        extensions.add(VideoLifecycleObserver.class);
        extensions.add(VideoRecorderExtensionInitializer.class);
        extensions.add(VideoTaker.class);
    }

    @org.junit.Before
    public void setup() throws Exception {
        bind(ApplicationScoped.class, ServiceLoader.class, serviceLoader);
        bind(ApplicationScoped.class, VideoConfiguration.class, configuration);
        bind(ApplicationScoped.class, Recorder.class, recorder);
        bind(ApplicationScoped.class, TakenResourceRegister.class, takenResourceRegister);

        Mockito.when(recorder.getVideoType()).thenReturn(VideoType.MP4);

        Mockito.doNothing().when(cleaner).clean(configuration);

        Mockito.when(serviceLoader.onlyOne(VideoRecorderEnvironmentCleaner.class, DefaultVideoRecorderEnvironmentCleaner.class))
            .thenReturn(cleaner);

        Mockito.when(serviceLoader.onlyOne(VideoStrategy.class, DefaultVideoStrategy.class))
            .thenReturn(strategy);

        videoFile = File.createTempFile("fakeVideo", recorder.getVideoType().toString().toLowerCase());

        Mockito.when(configuration.getVideoName()).thenReturn("record");

        Mockito.when(video.getResource()).thenReturn(videoFile);
        Mockito.when(video.getResourceType()).thenReturn(VideoType.MP4);
        Mockito.when(video.getWidth()).thenReturn(100);
        Mockito.when(video.getHeight()).thenReturn(100);

        Mockito.when(recorder.stopRecording()).thenReturn(video);
    }

    @org.junit.After
    public void teardown() {
        if (videoFile != null && videoFile.exists()) {
            if (!videoFile.delete()) {
                throw new IllegalStateException("Unable to delete temporary file: " + videoFile.getAbsolutePath());
            }
        }
    }

    @Test
    public void defaultConfigurationTest() throws Exception {

        // by default, no videos are taken at all

        fire(new VideoExtensionConfigured());

        fire(new BeforeSuite());
        fire(new BeforeClass(DummyTestCase.class));
        fire(new Before(DummyTestCase.class, DummyTestCase.class.getMethod("test")));

        bind(TestScoped.class, TestResult.class, TestResult.passed());

        fire(new After(DummyTestCase.class, DummyTestCase.class.getMethod("test")));
        fire(new AfterClass(DummyTestCase.class));
        fire(new AfterSuite());

        assertEventFired(BeforeVideoStart.class, 0);
        assertEventFired(StartRecordVideo.class, 0);
        assertEventFired(StartRecordSuiteVideo.class, 0);
        assertEventFired(AfterVideoStart.class, 0);

        assertEventFired(BeforeVideoStop.class, 0);
        assertEventFired(StopRecordVideo.class, 0);
        assertEventFired(StopRecordSuiteVideo.class, 0);
        assertEventFired(AfterVideoStop.class, 0);
    }

    @Test
    public void startBeforeSuiteTrueTest() throws Exception {

        Mockito.when(configuration.getStartBeforeSuite()).thenReturn(true);

        fire(new VideoExtensionConfigured());

        fire(new BeforeSuite());
        fire(new BeforeClass(DummyTestCase.class));
        fire(new Before(DummyTestCase.class, DummyTestCase.class.getMethod("test")));

        bind(TestScoped.class, TestResult.class, TestResult.passed());

        fire(new After(DummyTestCase.class, DummyTestCase.class.getMethod("test")));
        fire(new AfterClass(DummyTestCase.class));
        fire(new AfterSuite());

        assertEventFired(BeforeVideoStart.class, 1);
        assertEventFired(StartRecordSuiteVideo.class, 1);
        assertEventFired(AfterVideoStart.class, 1);

        assertEventFired(BeforeVideoStop.class, 1);
        assertEventFired(StopRecordSuiteVideo.class, 1);
        assertEventFired(AfterVideoStop.class, 1);
    }

    @Test
    public void startBeforeClassTrueTest() throws Exception {

        Mockito.when(configuration.getStartBeforeClass()).thenReturn(true);

        fire(new VideoExtensionConfigured());

        fire(new BeforeSuite());
        fire(new BeforeClass(DummyTestCase.class));
        fire(new Before(DummyTestCase.class, DummyTestCase.class.getMethod("test")));

        bind(TestScoped.class, TestResult.class, TestResult.passed());

        fire(new After(DummyTestCase.class, DummyTestCase.class.getMethod("test")));
        fire(new AfterClass(DummyTestCase.class));
        fire(new AfterSuite());

        assertEventFired(BeforeVideoStart.class, 1);
        assertEventFired(StartRecordClassVideo.class, 1);
        assertEventFired(AfterVideoStart.class, 1);

        assertEventFired(BeforeVideoStop.class, 1);
        assertEventFired(StopRecordClassVideo.class, 1);
        assertEventFired(AfterVideoStop.class, 1);
    }

    @Test
    public void startBeforeTestTrueTest() throws Exception {

        Mockito.when(configuration.getStartBeforeTest()).thenReturn(true);

        fire(new VideoExtensionConfigured());

        fire(new BeforeSuite());
        fire(new BeforeClass(DummyTestCase.class));
        fire(new Before(DummyTestCase.class, DummyTestCase.class.getMethod("test")));

        bind(TestScoped.class, TestResult.class, TestResult.passed());

        fire(new After(DummyTestCase.class, DummyTestCase.class.getMethod("test")));
        fire(new AfterClass(DummyTestCase.class));
        fire(new AfterSuite());

        assertEventFired(BeforeVideoStart.class, 1);
        assertEventFired(StartRecordVideo.class, 1);
        assertEventFired(AfterVideoStart.class, 1);

        assertEventFired(BeforeVideoStop.class, 1);
        assertEventFired(StopRecordVideo.class, 1);
        assertEventFired(AfterVideoStop.class, 1);
    }

    @Test
    public void takeOnlyOnFailTestFailedTest() throws Exception {

        Mockito.when(configuration.getTakeOnlyOnFail()).thenReturn(true);

        fire(new VideoExtensionConfigured());

        fire(new BeforeSuite());
        fire(new BeforeClass(DummyTestCase.class));
        fire(new Before(DummyTestCase.class, DummyTestCase.class.getMethod("test")));

        bind(TestScoped.class, TestResult.class, TestResult.failed(new RuntimeException("some exception")));

        fire(new After(DummyTestCase.class, DummyTestCase.class.getMethod("test")));
        fire(new AfterClass(DummyTestCase.class));
        fire(new AfterSuite());

        assertEventFired(BeforeVideoStart.class, 1);
        assertEventFired(StartRecordVideo.class, 1);
        assertEventFired(AfterVideoStart.class, 1);

        assertEventFired(PropertyReportEvent.class, 1);

        assertEventFired(BeforeVideoStop.class, 1);
        assertEventFired(StopRecordVideo.class, 1);
        assertEventFired(AfterVideoStop.class, 1);
    }

    @Test
    public void takeOnlyOnFailTestPassedTest() throws Exception {

        Mockito.when(configuration.getTakeOnlyOnFail()).thenReturn(true);

        fire(new VideoExtensionConfigured());

        fire(new BeforeSuite());
        fire(new BeforeClass(DummyTestCase.class));
        fire(new Before(DummyTestCase.class, DummyTestCase.class.getMethod("test")));

        bind(TestScoped.class, TestResult.class, TestResult.passed());

        fire(new After(DummyTestCase.class, DummyTestCase.class.getMethod("test")));
        fire(new AfterClass(DummyTestCase.class));
        fire(new AfterSuite());

        assertEventFired(BeforeVideoStart.class, 1);
        assertEventFired(StartRecordVideo.class, 1);
        assertEventFired(AfterVideoStart.class, 1);

        assertEventFired(PropertyReportEvent.class, 0);

        assertEventFired(BeforeVideoStop.class, 1);
        assertEventFired(StopRecordVideo.class, 1);
        assertEventFired(AfterVideoStop.class, 1);
    }

    // helpers

    private static class DummyTestCase {

        public void test() {
        }

    }

    private class FakeVideo extends Video {

    }
}
