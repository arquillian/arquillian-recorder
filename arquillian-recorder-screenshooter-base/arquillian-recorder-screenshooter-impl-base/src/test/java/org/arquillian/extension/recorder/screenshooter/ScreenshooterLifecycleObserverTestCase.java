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
package org.arquillian.extension.recorder.screenshooter;

import java.util.List;

import org.arquillian.extension.recorder.screenshooter.event.AfterScreenshotTaken;
import org.arquillian.extension.recorder.screenshooter.event.BeforeScreenshotTaken;
import org.arquillian.extension.recorder.screenshooter.event.ScreenshooterExtensionConfigured;
import org.arquillian.extension.recorder.screenshooter.event.TakeScreenshot;
import org.arquillian.extension.recorder.screenshooter.impl.DefaultScreenshooterEnvironmentCleaner;
import org.arquillian.extension.recorder.screenshooter.impl.DefaultScreenshootingStrategy;
import org.arquillian.extension.recorder.screenshooter.impl.ScreenshooterExtensionInitializer;
import org.arquillian.extension.recorder.screenshooter.impl.ScreenshooterLifecycleObserver;
import org.arquillian.recorder.reporter.ReporterConfiguration;
import org.jboss.arquillian.config.descriptor.impl.ArquillianDescriptorImpl;
import org.jboss.arquillian.core.api.Injector;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.spi.ServiceLoader;
import org.jboss.arquillian.core.spi.context.ApplicationContext;
import org.jboss.arquillian.test.spi.TestResult;
import org.jboss.arquillian.test.spi.annotation.TestScoped;
import org.jboss.arquillian.test.spi.event.suite.After;
import org.jboss.arquillian.test.spi.event.suite.Before;
import org.jboss.arquillian.test.test.AbstractTestTestBase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ScreenshooterLifecycleObserverTestCase extends AbstractTestTestBase {

    @Mock
    private ServiceLoader serviceLoader;

    @Mock
    private ScreenshooterConfiguration configuration = new ScreenshooterConfiguration(new ReporterConfiguration());

    @Mock
    private Screenshooter screenshooter;

    @Mock
    private ScreenshooterEnvironmentCleaner cleaner = new DefaultScreenshooterEnvironmentCleaner();

    @Inject
    private Instance<Injector> injector;

    @Override
    public void addExtensions(List<Class<?>> extensions) {
        extensions.add(ScreenshooterLifecycleObserver.class);
        extensions.add(ScreenshooterExtensionInitializer.class);
    }

    @org.junit.Before
    public void setMocks() throws Exception {
        bind(ApplicationScoped.class, ServiceLoader.class, serviceLoader);
        bind(ApplicationScoped.class, ScreenshooterConfiguration.class, configuration);
        bind(ApplicationScoped.class, Screenshooter.class, screenshooter);

        Mockito.when(screenshooter.getScreenshotType()).thenReturn(ScreenshotType.PNG);

        Mockito.doNothing().when(cleaner).clean(configuration);

        Mockito.when(serviceLoader.onlyOne(ScreenshootingStrategy.class, DefaultScreenshootingStrategy.class))
            .thenReturn(new DefaultScreenshootingStrategy());

        Mockito.when(serviceLoader.onlyOne(ScreenshooterEnvironmentCleaner.class, DefaultScreenshooterEnvironmentCleaner.class))
            .thenReturn(cleaner);
    }

    @Test
    public void strategyCreatorTest() {
        fire(new ArquillianDescriptorImpl("arquillian.xml"));
        fire(new ScreenshooterExtensionConfigured());

        ScreenshooterConfiguration configuration = getManager().getContext(ApplicationContext.class)
            .getObjectStore()
            .get(ScreenshooterConfiguration.class);

        configuration.validate();

        Assert.assertNotNull(configuration);

        ScreenshooterEnvironmentCleaner cleaner = getManager().getContext(ApplicationContext.class)
            .getObjectStore()
            .get(ScreenshooterEnvironmentCleaner.class);
        Assert.assertNotNull(cleaner);

        ScreenshootingStrategy instance = getManager().getContext(ApplicationContext.class)
            .getObjectStore()
            .get(ScreenshootingStrategy.class);
        Assert.assertNotNull(instance);
    }

    @Test
    public void beforeTestEventTakeBeforeTestFalse() throws Exception {

        Mockito.when(configuration.getTakeBeforeTest()).thenReturn(false);

        fire(new ScreenshooterExtensionConfigured());
        fire(new Before(FakeTestClass.class, FakeTestClass.class.getMethod("fakeTest")));

        assertEventFired(BeforeScreenshotTaken.class, 0);
        assertEventFired(TakeScreenshot.class, 0);
        assertEventFired(AfterScreenshotTaken.class, 0);
    }

    @Test
    public void beforeTestEventTakeBeforeTestTrue() throws Exception {

        Mockito.when(configuration.getTakeBeforeTest()).thenReturn(true);
        Mockito.when(configuration.getScreenshotType()).thenReturn("PNG");

        fire(new ScreenshooterExtensionConfigured());
        fire(new Before(FakeTestClass.class, FakeTestClass.class.getMethod("fakeTest")));

        assertEventFired(BeforeScreenshotTaken.class, 1);
        assertEventFired(TakeScreenshot.class, 1);
        assertEventFired(AfterScreenshotTaken.class, 1);
    }

    @Test
    public void afterTestEventTakeAfterTestTrue() throws Exception {

        Mockito.when(configuration.getTakeAfterTest()).thenReturn(true);
        Mockito.when(configuration.getScreenshotType()).thenReturn("PNG");

        fire(new ScreenshooterExtensionConfigured());
        fire(new Before(FakeTestClass.class, FakeTestClass.class.getMethod("fakeTest")));

        bind(TestScoped.class, TestResult.class, TestResult.passed());

        fire(new After(FakeTestClass.class, FakeTestClass.class.getMethod("fakeTest")));

        assertEventFired(BeforeScreenshotTaken.class, 1);
        assertEventFired(TakeScreenshot.class, 1);
        assertEventFired(AfterScreenshotTaken.class, 1);
    }

    @Test
    public void afterTestEventTestStatusFailedTakeAfterTestTrueTakeWhenFailedFalse() throws Exception {

        Mockito.when(configuration.getTakeAfterTest()).thenReturn(true);
        Mockito.when(configuration.getScreenshotType()).thenReturn("PNG");
        Mockito.when(configuration.getTakeWhenTestFailed()).thenReturn(false);

        fire(new ScreenshooterExtensionConfigured());
        fire(new Before(FakeTestClass.class, FakeTestClass.class.getMethod("fakeTest")));

        bind(TestScoped.class, TestResult.class, TestResult.failed(new RuntimeException()));

        fire(new After(FakeTestClass.class, FakeTestClass.class.getMethod("fakeTest")));

        assertEventFired(BeforeScreenshotTaken.class, 1);
        assertEventFired(TakeScreenshot.class, 1);
        assertEventFired(AfterScreenshotTaken.class, 1);
    }

    @Test
    public void afterTestEventTestStatusFailedTakeAfterTestTrueTakeWhenFailedTrue() throws Exception {

        Mockito.when(configuration.getTakeAfterTest()).thenReturn(true);
        Mockito.when(configuration.getScreenshotType()).thenReturn("PNG");
        Mockito.when(configuration.getTakeWhenTestFailed()).thenReturn(true);

        fire(new ScreenshooterExtensionConfigured());
        fire(new Before(FakeTestClass.class, FakeTestClass.class.getMethod("fakeTest")));

        bind(TestScoped.class, TestResult.class, TestResult.failed(new RuntimeException()));

        fire(new After(FakeTestClass.class, FakeTestClass.class.getMethod("fakeTest")));

        assertEventFired(BeforeScreenshotTaken.class, 1);
        assertEventFired(TakeScreenshot.class, 1);
        assertEventFired(AfterScreenshotTaken.class, 1);
    }

    @Test
    public void afterTestEventTestStatusPassed() throws Exception {

        Mockito.when(configuration.getTakeAfterTest()).thenReturn(true);
        Mockito.when(configuration.getScreenshotType()).thenReturn("PNG");

        fire(new ScreenshooterExtensionConfigured());
        fire(new Before(FakeTestClass.class, FakeTestClass.class.getMethod("fakeTest")));

        bind(TestScoped.class, TestResult.class, TestResult.passed());

        fire(new After(FakeTestClass.class, FakeTestClass.class.getMethod("fakeTest")));

        assertEventFired(BeforeScreenshotTaken.class, 1);
        assertEventFired(TakeScreenshot.class, 1);
        assertEventFired(AfterScreenshotTaken.class, 1);
    }

    private static class FakeTestClass {
        public void fakeTest() {

        }
    }

}
