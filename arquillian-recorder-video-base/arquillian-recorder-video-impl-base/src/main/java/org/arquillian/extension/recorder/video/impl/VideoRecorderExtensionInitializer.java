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

import org.arquillian.extension.recorder.video.VideoConfiguration;
import org.arquillian.extension.recorder.video.VideoConfigurationException;
import org.arquillian.extension.recorder.video.VideoRecorderEnvironmentCleaner;
import org.arquillian.extension.recorder.video.VideoStrategy;
import org.arquillian.extension.recorder.video.event.VideoExtensionConfigured;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.ServiceLoader;

/**
 * @author <a href="mailto:smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class VideoRecorderExtensionInitializer {

    @Inject
    @ApplicationScoped
    private InstanceProducer<VideoStrategy> strategy;

    @Inject
    @ApplicationScoped
    private InstanceProducer<VideoRecorderEnvironmentCleaner> cleaner;

    @Inject
    private Instance<VideoConfiguration> configuration;

    @Inject
    private Instance<ServiceLoader> serviceLoader;

    public void afterExtensionConfigured(@Observes VideoExtensionConfigured event) {

        VideoStrategy strategy = serviceLoader.get().onlyOne(VideoStrategy.class, DefaultVideoStrategy.class);
        strategy.setConfiguration(configuration.get());

        VideoRecorderEnvironmentCleaner cleaner = serviceLoader.get().onlyOne(VideoRecorderEnvironmentCleaner.class,
            DefaultVideoRecorderEnvironmentCleaner.class);

        this.strategy.set(strategy);
        this.cleaner.set(cleaner);

        try {
            this.cleaner.get().clean(configuration.get());
        } catch (Exception e) {
            throw new VideoConfigurationException("Unable to clean before screenshooting extension gets to work.", e);
        }
    }
}
