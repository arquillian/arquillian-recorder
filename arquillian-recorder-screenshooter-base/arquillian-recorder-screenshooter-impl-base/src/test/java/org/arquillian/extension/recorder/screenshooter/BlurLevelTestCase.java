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

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.arquillian.extension.recorder.RecorderStrategyRegister;
import org.arquillian.extension.recorder.screenshooter.api.Blur;
import org.arquillian.extension.recorder.screenshooter.api.BlurLevel;
import org.arquillian.extension.recorder.screenshooter.event.AfterScreenshotTaken;
import org.arquillian.extension.recorder.screenshooter.event.BeforeScreenshotTaken;
import org.arquillian.extension.recorder.screenshooter.event.ScreenshooterExtensionConfigured;
import org.arquillian.extension.recorder.screenshooter.event.TakeScreenshot;
import org.arquillian.extension.recorder.screenshooter.impl.BlurAfterScreenshotTakenObserver;
import org.arquillian.extension.recorder.screenshooter.impl.DefaultAnnotationScreenshootingStrategy;
import org.arquillian.extension.recorder.screenshooter.impl.DefaultScreenshooterEnvironmentCleaner;
import org.arquillian.extension.recorder.screenshooter.impl.DefaultScreenshootingStrategy;
import org.arquillian.extension.recorder.screenshooter.impl.ScreenshooterExtensionInitializer;
import org.arquillian.extension.recorder.screenshooter.impl.ScreenshooterLifecycleObserver;
import org.arquillian.recorder.reporter.ReporterConfiguration;
import org.jboss.arquillian.core.api.Injector;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.spi.ServiceLoader;
import org.jboss.arquillian.core.spi.context.ApplicationContext;
import org.jboss.arquillian.test.test.AbstractTestTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


/**
 * @author <a href="asotobu@gmail.com">Alex Soto</a>
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class BlurLevelTestCase extends AbstractTestTestBase {

    protected static File OUTPUT_FILE = null;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Mock
    private ServiceLoader serviceLoader;

    @Mock
    private ScreenshooterConfiguration configuration = new ScreenshooterConfiguration(new ReporterConfiguration());

    @Mock
    private Screenshooter screenshooter;

    @Mock
    private RecorderStrategyRegister recorderStrategyRegister = new RecorderStrategyRegister();

    @Mock
    private ScreenshooterEnvironmentCleaner cleaner = new DefaultScreenshooterEnvironmentCleaner();

    @Inject
    private Instance<Injector> injector;

    private File screenshotFile ;
    private int originalSize;

    @Before
    public void init() throws Exception {
        bind(ApplicationScoped.class, ServiceLoader.class, serviceLoader);
        bind(ApplicationScoped.class, ScreenshooterConfiguration.class, configuration);
        bind(ApplicationScoped.class, Screenshooter.class, screenshooter);
        bind(ApplicationScoped.class, RecorderStrategyRegister.class, recorderStrategyRegister);

        Mockito.when(screenshooter.getScreenshotType()).thenReturn(ScreenshotType.PNG);

        Mockito.doNothing().when(cleaner).clean(configuration);

        Mockito.when(serviceLoader.onlyOne(ScreenshooterEnvironmentCleaner.class, DefaultScreenshooterEnvironmentCleaner.class))
            .thenReturn(cleaner);

        screenshotFile = folder.newFile("screenshot.png");
        URL resource = BlurLevelTestCase.class.getResource("/arquillian_ui_success_256px.png");
        FileUtils.copyURLToFile(resource, screenshotFile);
        originalSize = (int) screenshotFile.length();
        OUTPUT_FILE = screenshotFile.getAbsoluteFile();
    }

    @Override
    public void addExtensions(List<Class<?>> extensions) {
        extensions.add(ScreenshooterLifecycleObserver.class);
        extensions.add(TakeScreenshotObserverStub.class);
        extensions.add(BlurAfterScreenshotTakenObserver.class);
        extensions.add(ScreenshooterExtensionInitializer.class);
    }

    @Test
    public void blurScreenshotIfAnnotationIsPresent() throws Exception {

        Mockito.when(configuration.getTakeBeforeTest()).thenReturn(true);

        fire(new ScreenshooterExtensionConfigured());

        initRecorderStrategyRegister();

        fire(new org.jboss.arquillian.test.spi.event.suite.Before(FakeTestClass.class, FakeTestClass.class.getMethod("fakeTest")));

        assertEventFired(BeforeScreenshotTaken.class, 1);
        assertEventFired(TakeScreenshot.class, 1);
        assertEventFired(AfterScreenshotTaken.class, 1);
        File blurredImage = new File(screenshotFile.getAbsolutePath());
        assertThat((int)blurredImage.length(), is(not(originalSize)));
    }

    private void initRecorderStrategyRegister() {
        RecorderStrategyRegister instance = getManager().getContext(ApplicationContext.class)
            .getObjectStore()
            .get(RecorderStrategyRegister.class);

        Assert.assertNotNull(instance);

        DefaultScreenshootingStrategy defaultStrategy = new DefaultScreenshootingStrategy();
        defaultStrategy.setConfiguration(configuration);

        DefaultAnnotationScreenshootingStrategy annotationStrategy = new DefaultAnnotationScreenshootingStrategy();
        annotationStrategy.setConfiguration(configuration);

        instance.add(defaultStrategy);
        instance.add(annotationStrategy);
    }

    private static class FakeTestClass {
        @Blur(BlurLevel.LOW)
        public void fakeTest() {

        }
    }
}
