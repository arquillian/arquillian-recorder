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
package org.arquillian.recorder.reporter.configuration;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.arquillian.extension.recorder.RecorderConfigurator;
import org.arquillian.recorder.reporter.ReporterConfiguration;
import org.arquillian.recorder.reporter.event.ReportingExtensionConfigured;
import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.config.descriptor.api.ExtensionDef;
import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;

/**
 * Parses configuration from Arquillian descriptor.<br>
 * <br>
 * Produces {@link ApplicationScoped}:
 * <ul>
 * <li>{@link ReporterConfiguration}</li>
 * </ul>
 * Fires:
 * <ul>
 * <li>{@link ReportingExtensionConfigured}</li>
 * </ul>
 *
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class ReporterConfigurator extends RecorderConfigurator<ReporterConfiguration> {

    private static final Logger LOGGER = Logger.getLogger(ReporterConfigurator.class.getName());

    private static final String EXTENSION_NAME = "reporter";

    @Inject
    @ApplicationScoped
    private InstanceProducer<ReporterConfiguration> configuration;

    @Inject
    private Event<ReportingExtensionConfigured> extensionConfigured;

    public void configureExtension(@Observes ArquillianDescriptor descriptor) {

        ReporterConfiguration configuration = new ReporterConfiguration();

        for (ExtensionDef extension : descriptor.getExtensions()) {
            if (extension.getExtensionName().equals(EXTENSION_NAME)) {
                configuration.setConfiguration(extension.getExtensionProperties());
                break;
            }
        }

        configuration.validate();

        this.configuration.set(configuration);

        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("Configuration of Arquillian Reporting extension:");
            LOGGER.info(this.configuration.get().toString());
        }

        extensionConfigured.fire(new ReportingExtensionConfigured());
    }
}
