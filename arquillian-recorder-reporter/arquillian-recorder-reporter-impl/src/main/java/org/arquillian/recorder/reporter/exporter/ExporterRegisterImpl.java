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

import java.util.ArrayList;
import java.util.List;

import org.arquillian.recorder.reporter.Exporter;
import org.arquillian.recorder.reporter.ExporterRegister;
import org.arquillian.recorder.reporter.ReportType;

/**
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class ExporterRegisterImpl implements ExporterRegister {

    private final List<Exporter> exporters = new ArrayList<Exporter>();

    @Override
    public ExporterRegister add(Exporter exporter) {
        if (get(exporter.getReportType()) == null) {
            exporters.add(exporter);
        }
        return this;
    }

    @Override
    public Exporter get(Class<? extends ReportType> reportType) {

        Exporter found = null;

        for (Exporter exporter : exporters) {
            if (exporter.getReportType() == reportType) {
                found = exporter;
                break;
            }
        }

        return found;
    }

    @Override
    public void clear() {
        exporters.clear();
    }

    @Override
    public boolean isSupported(Class<? extends ReportType> reportType) {
        for (Exporter exporter : exporters) {
            if (exporter.getReportType() == reportType) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Exporter> getAll() {
        return exporters;
    }

}
