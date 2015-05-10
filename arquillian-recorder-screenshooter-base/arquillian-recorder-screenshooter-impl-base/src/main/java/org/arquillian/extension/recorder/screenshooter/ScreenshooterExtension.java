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
package org.arquillian.extension.recorder.screenshooter;

import org.arquillian.extension.recorder.screenshooter.impl.BlurAfterScreenshotTakenObserver;
import org.arquillian.extension.recorder.screenshooter.impl.DefaultAnnotationScreenshootingStrategy;
import org.arquillian.extension.recorder.screenshooter.impl.DefaultScreenshootingStrategy;
import org.arquillian.extension.recorder.screenshooter.impl.InTestScreenshotResourceReportObserver;
import org.arquillian.extension.recorder.screenshooter.impl.ScreenshooterExtensionInitializer;
import org.arquillian.extension.recorder.screenshooter.impl.ScreenshooterProvider;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

/**
 * @author <a href="mailto:smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class ScreenshooterExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        builder.observer(ScreenshooterExtensionInitializer.class);
        builder.service(ResourceProvider.class, ScreenshooterProvider.class);
        builder.observer(InTestScreenshotResourceReportObserver.class);
        builder.observer(BlurAfterScreenshotTakenObserver.class);
        builder.service(ScreenshootingStrategy.class, DefaultScreenshootingStrategy.class);
        builder.service(ScreenshootingStrategy.class, DefaultAnnotationScreenshootingStrategy.class);
    }
}
