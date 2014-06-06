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
package org.arquillian.recorder.reporter.exporter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.arquillian.recorder.reporter.Exporter;
import org.arquillian.recorder.reporter.ExporterRegister;
import org.arquillian.recorder.reporter.JAXBContextFactory;
import org.arquillian.recorder.reporter.event.ExporterRegisterCreated;
import org.arquillian.recorder.reporter.exporter.impl.AsciiDocExporter;
import org.arquillian.recorder.reporter.exporter.impl.HTMLExporter;
import org.arquillian.recorder.reporter.exporter.impl.JSONExporter;
import org.arquillian.recorder.reporter.exporter.impl.XMLExporter;
import org.arquillian.recorder.reporter.impl.ReportTypeRegister;
import org.arquillian.recorder.reporter.impl.type.AsciiDocReport;
import org.arquillian.recorder.reporter.impl.type.HTMLReport;
import org.arquillian.recorder.reporter.impl.type.JSONReport;
import org.arquillian.recorder.reporter.impl.type.XMLReport;
import org.arquillian.recorder.reporter.model.Report;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;

/**
 * Handles the registration of default exporters and report types. When you want to override some exporter, you have to observe
 * {@link ExporterRegisterCreated} event before this class observes it and registers its own. Any positive precedence on 3rd
 * party extension which listens to {@link ExporterRegisterCreated} does the job.
 *
 * @see Exporter
 * @see ExporterRegister
 * @see ReportTypeRegister
 *
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class ExporterRegistrationHandler {

    @Inject
    private Instance<ExporterRegister> exporterRegister;

    @Inject
    private Instance<ReportTypeRegister> reportTypeRegister;

    public void onCreatedReporterRegister(@Observes ExporterRegisterCreated event) {

        JAXBContext context = getContext();

        exporterRegister.get()
            .add(new XMLExporter(context))
            .add(new JSONExporter(context))
            .add(new HTMLExporter(context))
            .add(new AsciiDocExporter());

        reportTypeRegister.get()
            .add(new XMLReport())
            .add(new JSONReport())
            .add(new HTMLReport())
            .add(new AsciiDocReport());
    }

    private JAXBContext getContext() {
        try {
            return JAXBContextFactory.initContext(Report.class);
        } catch (JAXBException ex) {
            throw new RuntimeException("Unable to initialize JAXBContext.", ex.getCause());
        }
    }
}
