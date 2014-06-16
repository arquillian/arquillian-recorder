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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.arquillian.extension.recorder.RecorderStrategy;
import org.arquillian.extension.recorder.RecorderStrategyRegister;
import org.arquillian.extension.recorder.screenshooter.ScreenshooterConfiguration;
import org.arquillian.extension.recorder.screenshooter.ScreenshooterConfigurationException;
import org.arquillian.extension.recorder.screenshooter.ScreenshooterEnvironmentCleaner;
import org.arquillian.extension.recorder.screenshooter.ScreenshootingStrategy;
import org.arquillian.extension.recorder.screenshooter.event.ScreenshooterExtensionConfigured;
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
public class ScreenshooterExtensionInitializer {

    @Inject
    @ApplicationScoped
    private InstanceProducer<ScreenshooterEnvironmentCleaner> cleaner;

    @Inject
    @ApplicationScoped
    private InstanceProducer<RecorderStrategyRegister> recorderStrategyRegister;

    @Inject
    private Instance<ScreenshooterConfiguration> configuration;

    @Inject
    private Instance<ServiceLoader> serviceLoader;

    public void afterExtensionConfigured(@Observes ScreenshooterExtensionConfigured event) {

        Collection<ScreenshootingStrategy> strategies = serviceLoader.get().all(ScreenshootingStrategy.class);

        for (ScreenshootingStrategy strategy : strategies) {
            strategy.setConfiguration(configuration.get());
        }

        Set<RecorderStrategy<?>> recorderStrategies = new HashSet<RecorderStrategy<?>>(strategies);

        recorderStrategyRegister.set(new RecorderStrategyRegister());
        recorderStrategyRegister.get().addAll(recorderStrategies);

        ScreenshooterEnvironmentCleaner cleaner = serviceLoader.get()
            .onlyOne(ScreenshooterEnvironmentCleaner.class, DefaultScreenshooterEnvironmentCleaner.class);

        this.cleaner.set(cleaner);

        try {
            this.cleaner.get().clean(configuration.get());
        } catch (Exception e) {
            throw new ScreenshooterConfigurationException("Unable to clean before screenshooting extension gets to work.", e);
        }
    }
}
