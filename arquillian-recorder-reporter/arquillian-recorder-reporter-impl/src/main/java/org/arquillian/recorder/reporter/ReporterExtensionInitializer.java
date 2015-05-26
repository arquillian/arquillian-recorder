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
package org.arquillian.recorder.reporter;

import org.arquillian.recorder.reporter.event.ExporterRegisterCreated;
import org.arquillian.recorder.reporter.event.ReportingExtensionConfigured;
import org.arquillian.recorder.reporter.exporter.DefaultExporterRegisterFactory;
import org.arquillian.recorder.reporter.impl.ReportTypeRegister;
import org.arquillian.recorder.reporter.impl.ReporterImpl;
import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.ServiceLoader;

/**
 * Initializes reporting extension after it is configured from Arquillian descriptor.<br>
 * <br>
 * Produces {@link ApplicationScoped}:
 * <ul>
 * <li>{@link ExporterRegister}</li>
 * <li>{@link ReportTypeRegister}</li>
 * <li>{@link Exporter}</li>
 * <li>{@link Reporter}</li>
 * </ul>
 * Fires:
 * <ul>
 * <li>{@link ExporterRegisterCreated}</li>
 * </ul>
 *
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class ReporterExtensionInitializer {

    @Inject
    @ApplicationScoped
    private InstanceProducer<ExporterRegister> exporterRegister;

    @Inject
    @ApplicationScoped
    private InstanceProducer<ReportTypeRegister> reportTypeRegister;

    @Inject
    @ApplicationScoped
    private InstanceProducer<Exporter> exporter;

    @Inject
    @ApplicationScoped
    private InstanceProducer<Reporter> reporter;

    @Inject
    private Instance<ServiceLoader> serviceLoader;

    @Inject
    private Instance<ReporterConfiguration> configuration;

    @Inject
    private Event<ExporterRegisterCreated> exporterRegisterCreatedEvent;

    /**
     * Produces {@link ReportTypeRegister} and default reporter ({@link ReporterImpl}) when another is not present on class
     * path. Creates default exporter register factory when another is not present on class path. After that,
     * {@link ExporterRegisterCreated} is fired in order to be able to register all exporters even from 3rd party extension. In
     * the end, report type from configuration is mapped to supported exporter and when found, it is produced.
     *
     * @param event event fired from configurator when reporter extension is fully configured
     */
    public void onExtensionConfigured(@Observes ReportingExtensionConfigured event) {

        // produce report type register

        reportTypeRegister.set(new ReportTypeRegister());

        // produce default reporter

        Reporter reporter = serviceLoader.get().onlyOne(Reporter.class, ReporterImpl.class);
        reporter.setConfiguration(configuration.get());
        this.reporter.set(reporter);

        // produce default exporter

        ExporterRegisterFactory registerFactory = serviceLoader.get().onlyOne(ExporterRegisterFactory.class,
            DefaultExporterRegisterFactory.class);

        ExporterRegister register = registerFactory.getExporterRegisterInstance();
        this.exporterRegister.set(register);

        // place to listen to where hooking of exporters will occur

        exporterRegisterCreatedEvent.fire(new ExporterRegisterCreated());

        // match reporter type from configuration to registered exporters according to its report types

        String report = configuration.get().getReport();

        ReportType reportType = reportTypeRegister.get().get(report);

        if (reportType != null) {
            configuration.get().setFileName(reportType.getTypes()[0]);
            Exporter exporterToUse = exporterRegister.get().get(reportType.getClass());
            if (exporterToUse != null) {
                exporterToUse.setConfiguration(configuration.get());
                this.exporter.set(exporterToUse);
            } else {
                throw new ReporterConfigurationException("Unable to match required reporter type from configuration to some registered exporter.");
            }
        }
    }
}
