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
package org.arquillian.recorder.reporter.impl;

import java.util.Date;

import org.arquillian.extension.recorder.Configuration;
import org.arquillian.recorder.reporter.Reporter;
import org.arquillian.recorder.reporter.ReporterConfiguration;
import org.arquillian.recorder.reporter.ReporterCursor;
import org.arquillian.recorder.reporter.model.ContainerReport;
import org.arquillian.recorder.reporter.model.ExtensionReport;
import org.arquillian.recorder.reporter.model.Report;
import org.arquillian.recorder.reporter.model.TestClassReport;
import org.arquillian.recorder.reporter.model.TestMethodReport;
import org.arquillian.recorder.reporter.model.TestSuiteReport;

/**
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class ReporterImpl implements Reporter {

    private ReporterConfiguration configuration;

    private final Report report = new Report();

    private TestSuiteReport testSuiteReport;

    private TestClassReport testClassReport;

    private TestMethodReport testMethodReport;

    private ContainerReport containerReport;

    private ExtensionReport extensionReport;

    private ReporterCursor reporterCursor = new ReporterCursor();

    @Override
    public Report getReport() {
        return report;
    }

    public ReporterImpl() {
        reporterCursor.setCursor(report);
    }

    @Override
    public void setReporterCursor(ReporterCursor reporterCursor) {
        this.reporterCursor = reporterCursor;
    }

    @Override
    public ReporterCursor getReporterCursor() {
        return reporterCursor;
    }

    @Override
    public void setTestSuiteReport(TestSuiteReport testSuiteReport) {
        this.testSuiteReport = testSuiteReport;
        this.reporterCursor.setCursor(this.testSuiteReport);
        this.testSuiteReport.setStop(new Date(System.currentTimeMillis()));
    }

    @Override
    public void setTestClassReport(TestClassReport testClassReport) {
        this.testClassReport = testClassReport;
        this.reporterCursor.setCursor(this.testClassReport);
    }

    @Override
    public void setTestMethodReport(TestMethodReport testMethodReport) {
        this.testMethodReport = testMethodReport;
        this.reporterCursor.setCursor(this.testMethodReport);
    }

    @Override
    public void setContainerReport(ContainerReport containerReport) {
        this.containerReport = containerReport;
    }

    @Override
    public void setExtensionReport(ExtensionReport extensionReport) {
        this.extensionReport = extensionReport;
    }

    @Override
    public TestSuiteReport getLastTestSuiteReport() {
        return testSuiteReport;
    }

    @Override
    public TestClassReport getLastTestClassReport() {
        return testClassReport;
    }

    @Override
    public TestMethodReport getLastTestMethodReport() {
        return testMethodReport;
    }

    @Override
    public ContainerReport getLastContainerReport() {
        return containerReport;
    }

    @Override
    public ExtensionReport getLastExtensionReport() {
        return extensionReport;
    }

    @Override
    public void setConfiguration(Configuration<?> configuration) {
        this.configuration = (ReporterConfiguration) configuration;
    }

}
