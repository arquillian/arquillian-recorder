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
package org.arquillian.recorder.reporter.impl.test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.arquillian.extension.recorder.When;
import org.arquillian.recorder.reporter.Exporter;
import org.arquillian.recorder.reporter.Reporter;
import org.arquillian.recorder.reporter.ReporterConfiguration;
import org.arquillian.recorder.reporter.exporter.impl.AsciiDocExporter;
import org.arquillian.recorder.reporter.impl.ReporterImpl;
import org.arquillian.recorder.reporter.model.ContainerReport;
import org.arquillian.recorder.reporter.model.DeploymentReport;
import org.arquillian.recorder.reporter.model.TestClassReport;
import org.arquillian.recorder.reporter.model.TestMethodReport;
import org.arquillian.recorder.reporter.model.TestSuiteReport;
import org.arquillian.recorder.reporter.model.entry.FileEntry;
import org.arquillian.recorder.reporter.model.entry.KeyValueEntry;
import org.arquillian.recorder.reporter.model.entry.ScreenshotEntry;
import org.arquillian.recorder.reporter.model.entry.VideoEntry;
import org.arquillian.recorder.reporter.model.entry.table.TableCellEntry;
import org.arquillian.recorder.reporter.model.entry.table.TableEntry;
import org.arquillian.recorder.reporter.model.entry.table.TableRowEntry;
import org.jboss.arquillian.test.spi.TestResult;
import org.junit.Test;

public class AsciiDocReporterTestCase {

    @Test
    public void shouldExportReportOnAsciiDocCompliantFormatIfConfigured() throws Exception {

        ReporterConfiguration configuration = new ReporterConfiguration();
        Map<String, String> configMap = new HashMap<String, String>();
        configMap.put("report", "asciidoc");
        configMap.put("file", "report");
        configMap.put("asciidocStandardCompliant", "true");
        configuration.setConfiguration(configMap);
        configuration.validate();

        Reporter reporter = new ReporterImpl();
        reporter.setConfiguration(configuration);

        KeyValueEntry kve3 = new KeyValueEntry();
        kve3.setKey("key3");
        kve3.setValue("value3");

        reporter.getReporterCursor().getCursor().getPropertyEntries().add(kve3);
        reporter.getReporterCursor().getCursor().getPropertyEntries().add(generateTable());

        TestSuiteReport testSuiteReport = new TestSuiteReport();
        reporter.getReport().getTestSuiteReports().add(testSuiteReport);
        reporter.setTestSuiteReport(testSuiteReport);

        KeyValueEntry kve = new KeyValueEntry();
        kve.setKey("key");
        kve.setValue("value");

        KeyValueEntry kve2 = new KeyValueEntry();
        kve2.setKey("key2");
        kve2.setValue("value2");

        FileEntry fe = new FileEntry();
        fe.setPath("somePath");
        fe.setSize("100MB");

        reporter.getReporterCursor().getCursor().getPropertyEntries().add(kve);
        reporter.getReporterCursor().getCursor().getPropertyEntries().add(kve2);
        reporter.getReporterCursor().getCursor().getPropertyEntries().add(fe);
        reporter.getReporterCursor().getCursor().getPropertyEntries().add(generateTable());

        // containers
        ContainerReport containerReport = new ContainerReport();
        containerReport.setQualifier("wildfly");
        containerReport.setConfiguration(new HashMap<String, String>());
        reporter.getLastTestSuiteReport().getContainerReports().add(containerReport);
        reporter.setContainerReport(containerReport);

        // deployment
        DeploymentReport deploymentReport = new DeploymentReport();

        deploymentReport.setArchiveName("some.war");
        deploymentReport.setName("deploymentName");
        deploymentReport.setOrder(1);
        deploymentReport.setProtocol("someProtocol");
        deploymentReport.setTarget("wildfly");

        reporter.getLastContainerReport().getDeploymentReports().add(deploymentReport);

        TestClassReport testClassReport = new TestClassReport();
        testClassReport.setTestClassName(FakeTestClass.class.getName());
        reporter.getLastTestSuiteReport().getTestClassReports().add(testClassReport);
        reporter.setTestClassReport(testClassReport);

        VideoEntry videoEntry = new VideoEntry();
        videoEntry.setPath(configuration.getRootDir().getAbsolutePath() + "/some/someVideo.mp4");
        videoEntry.setSize("54M");
        reporter.getReporterCursor().getCursor().getPropertyEntries().add(videoEntry);
        reporter.getReporterCursor().getCursor().getPropertyEntries().add(generateTable());

        TestMethodReport testMethodReport = new TestMethodReport();
        testMethodReport.setName("someTestMethod");
        TestResult testResult = TestResult.passed();
        testResult.setStart(System.currentTimeMillis());
        testResult.setEnd(testResult.getStart() + 1000);
        testMethodReport.setReportMessage("This test should be executed manually too.");
        testMethodReport.setStatus(testResult.getStatus());
        testMethodReport.setDuration(testResult.getEnd() - testResult.getStart());

        reporter.getLastTestClassReport().getTestMethodReports().add(testMethodReport);
        reporter.setTestMethodReport(testMethodReport);

        TestMethodReport testMethodReport2 = new TestMethodReport();
        testMethodReport2.setName("someTestMethod2");
        TestResult testResult2 = TestResult.failed(new IOException("Exception"));
        testResult2.setStart(System.currentTimeMillis());
        testResult2.setEnd(testResult2.getStart() + 2000);
        testMethodReport2.setStatus(testResult2.getStatus());
        testMethodReport2.setDuration(testResult2.getEnd() - testResult2.getStart());
        testMethodReport2.setException("some exception");

        reporter.getLastTestClassReport().getTestMethodReports().add(testMethodReport2);
        reporter.setTestMethodReport(testMethodReport2);

        ScreenshotEntry sce = new ScreenshotEntry();
        sce.setPath(configuration.getRootDir().getAbsolutePath() + "/niceScreenshot.jpg");
        sce.setSize("56kB");
        sce.setPhase(When.BEFORE);
        sce.setWidth(300);
        sce.setHeight(500);

        ScreenshotEntry sce2 = new ScreenshotEntry();
        sce2.setPath(configuration.getRootDir().getAbsolutePath() + "/niceScreenshotBefore.jpg");
        sce2.setPhase(When.BEFORE);
        sce2.setWidth(300);
        sce2.setHeight(500);

        reporter.getLastTestMethodReport().getPropertyEntries().add(sce);
        reporter.getLastTestMethodReport().getPropertyEntries().add(sce2);
        reporter.getLastTestMethodReport().getPropertyEntries().add(generateTable());

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        Exporter exporter = new AsciiDocExporter(byteArrayOutputStream);
        exporter.setConfiguration(configuration);

        exporter.export(reporter.getReport());

        // Not the best way to test it but for now it is enough
        String content = new String(byteArrayOutputStream.toByteArray()).trim();

        assertThat(content, containsString("Arquillian"));
        assertThat(content, containsString("Extensions"));
        assertThat(content, containsString("Test Result"));
        assertThat(content, containsString("some exception"));
        assertThat(content, not(containsString(":icons: font")));
        assertThat(content, not(containsString("icon:")));
    }

    @Test
    public void shouldExportReportInAsciiDocFormat() throws Exception {

        ReporterConfiguration configuration = new ReporterConfiguration();
        Map<String, String> configMap = new HashMap<String, String>();
        configMap.put("report", "asciidoc");
        configMap.put("file", "report");
        configuration.setConfiguration(configMap);
        configuration.validate();

        Reporter reporter = new ReporterImpl();
        reporter.setConfiguration(configuration);

        KeyValueEntry kve3 = new KeyValueEntry();
        kve3.setKey("key3");
        kve3.setValue("value3");

        reporter.getReporterCursor().getCursor().getPropertyEntries().add(kve3);

        TestSuiteReport testSuiteReport = new TestSuiteReport();
        reporter.getReport().getTestSuiteReports().add(testSuiteReport);
        reporter.setTestSuiteReport(testSuiteReport);

        KeyValueEntry kve = new KeyValueEntry();
        kve.setKey("key");
        kve.setValue("value");

        KeyValueEntry kve2 = new KeyValueEntry();
        kve2.setKey("key2");
        kve2.setValue("value2");

        FileEntry fe = new FileEntry();
        fe.setPath("somePath");
        fe.setSize("100MB");

        reporter.getReporterCursor().getCursor().getPropertyEntries().add(kve);
        reporter.getReporterCursor().getCursor().getPropertyEntries().add(kve2);
        reporter.getReporterCursor().getCursor().getPropertyEntries().add(fe);

        // containers
        ContainerReport containerReport = new ContainerReport();
        containerReport.setQualifier("wildfly");
        containerReport.setConfiguration(new HashMap<String, String>());
        reporter.getLastTestSuiteReport().getContainerReports().add(containerReport);
        reporter.setContainerReport(containerReport);

        // deployment
        DeploymentReport deploymentReport = new DeploymentReport();

        deploymentReport.setArchiveName("some.war");
        deploymentReport.setName("deploymentName");
        deploymentReport.setOrder(1);
        deploymentReport.setProtocol("someProtocol");
        deploymentReport.setTarget("wildfly");

        reporter.getLastContainerReport().getDeploymentReports().add(deploymentReport);

        TestClassReport testClassReport = new TestClassReport();
        testClassReport.setTestClassName(FakeTestClass.class.getName());
        reporter.getLastTestSuiteReport().getTestClassReports().add(testClassReport);
        reporter.setTestClassReport(testClassReport);

        VideoEntry videoEntry = new VideoEntry();
        videoEntry.setPath(configuration.getRootDir().getAbsolutePath() + "/some/someVideo.mp4");
        videoEntry.setSize("54M");
        reporter.getReporterCursor().getCursor().getPropertyEntries().add(videoEntry);

        TestMethodReport testMethodReport = new TestMethodReport();
        testMethodReport.setName("someTestMethod");
        TestResult testResult = TestResult.passed();
        testResult.setStart(System.currentTimeMillis());
        testResult.setEnd(testResult.getStart() + 1000);
        testMethodReport.setReportMessage("This test should be executed manually too.");
        testMethodReport.setStatus(testResult.getStatus());
        testMethodReport.setDuration(testResult.getEnd() - testResult.getStart());

        reporter.getLastTestClassReport().getTestMethodReports().add(testMethodReport);
        reporter.setTestMethodReport(testMethodReport);

        TestMethodReport testMethodReport2 = new TestMethodReport();
        testMethodReport2.setName("someTestMethod2");
        TestResult testResult2 = TestResult.failed(new IOException("Exception"));
        testResult2.setStart(System.currentTimeMillis());
        testResult2.setEnd(testResult2.getStart() + 2000);
        testMethodReport2.setStatus(testResult2.getStatus());
        testMethodReport2.setDuration(testResult2.getEnd() - testResult2.getStart());
        testMethodReport2.setException("some exception");

        reporter.getLastTestClassReport().getTestMethodReports().add(testMethodReport2);
        reporter.setTestMethodReport(testMethodReport2);

        ScreenshotEntry sce = new ScreenshotEntry();
        sce.setPath(configuration.getRootDir().getAbsolutePath() + "/niceScreenshot.jpg");
        sce.setSize("56kB");
        sce.setPhase(When.BEFORE);
        sce.setWidth(300);
        sce.setHeight(500);

        ScreenshotEntry sce2 = new ScreenshotEntry();
        sce2.setPath(configuration.getRootDir().getAbsolutePath() + "/niceScreenshotBefore.jpg");
        sce2.setPhase(When.BEFORE);
        sce2.setWidth(300);
        sce2.setHeight(500);

        reporter.getLastTestMethodReport().getPropertyEntries().add(sce);
        reporter.getLastTestMethodReport().getPropertyEntries().add(sce2);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        Exporter exporter = new AsciiDocExporter(byteArrayOutputStream);
        exporter.setConfiguration(configuration);

        exporter.export(reporter.getReport());

        // Not the best way to test it but for now it is enough
        String content = new String(byteArrayOutputStream.toByteArray()).trim();

        assertThat(content, containsString("Arquillian"));
        assertThat(content, containsString("Extensions"));
        assertThat(content, containsString("Test Result"));
        assertThat(content, containsString("some exception"));
    }

    @Test
    public void shouldAddAttributesOnHeaderExportReportInAsciiDocFormat() throws Exception {

        ReporterConfiguration configuration = new ReporterConfiguration();
        Map<String, String> configMap = new HashMap<String, String>();
        configMap.put("report", "asciidoc");
        configMap.put("file", "report");
        configMap.put("asciiDocAttributesFile", "attributes.adoc");
        configuration.setConfiguration(configMap);
        configuration.validate();

        Reporter reporter = new ReporterImpl();
        reporter.setConfiguration(configuration);

        KeyValueEntry kve3 = new KeyValueEntry();
        kve3.setKey("key3");
        kve3.setValue("value3");

        reporter.getReporterCursor().getCursor().getPropertyEntries().add(kve3);

        TestSuiteReport testSuiteReport = new TestSuiteReport();
        reporter.getReport().getTestSuiteReports().add(testSuiteReport);
        reporter.setTestSuiteReport(testSuiteReport);

        KeyValueEntry kve = new KeyValueEntry();
        kve.setKey("key");
        kve.setValue("value");

        KeyValueEntry kve2 = new KeyValueEntry();
        kve2.setKey("key2");
        kve2.setValue("value2");

        FileEntry fe = new FileEntry();
        fe.setPath("somePath");
        fe.setSize("100MB");

        reporter.getReporterCursor().getCursor().getPropertyEntries().add(kve);
        reporter.getReporterCursor().getCursor().getPropertyEntries().add(kve2);
        reporter.getReporterCursor().getCursor().getPropertyEntries().add(fe);

        // containers
        ContainerReport containerReport = new ContainerReport();
        containerReport.setQualifier("wildfly");
        containerReport.setConfiguration(new HashMap<String, String>());
        reporter.getLastTestSuiteReport().getContainerReports().add(containerReport);
        reporter.setContainerReport(containerReport);

        // deployment
        DeploymentReport deploymentReport = new DeploymentReport();

        deploymentReport.setArchiveName("some.war");
        deploymentReport.setName("deploymentName");
        deploymentReport.setOrder(1);
        deploymentReport.setProtocol("someProtocol");
        deploymentReport.setTarget("wildfly");

        reporter.getLastContainerReport().getDeploymentReports().add(deploymentReport);

        TestClassReport testClassReport = new TestClassReport();
        testClassReport.setTestClassName(FakeTestClass.class.getName());
        reporter.getLastTestSuiteReport().getTestClassReports().add(testClassReport);
        reporter.setTestClassReport(testClassReport);

        VideoEntry videoEntry = new VideoEntry();
        videoEntry.setPath(configuration.getRootDir().getAbsolutePath() + "/some/someVideo.mp4");
        videoEntry.setSize("54M");
        reporter.getReporterCursor().getCursor().getPropertyEntries().add(videoEntry);

        TestMethodReport testMethodReport = new TestMethodReport();
        testMethodReport.setName("someTestMethod");
        TestResult testResult = TestResult.passed();
        testResult.setStart(System.currentTimeMillis());
        testResult.setEnd(testResult.getStart() + 1000);
        testMethodReport.setReportMessage("This test should be executed manually too.");
        testMethodReport.setStatus(testResult.getStatus());
        testMethodReport.setDuration(testResult.getEnd() - testResult.getStart());

        reporter.getLastTestClassReport().getTestMethodReports().add(testMethodReport);
        reporter.setTestMethodReport(testMethodReport);

        TestMethodReport testMethodReport2 = new TestMethodReport();
        testMethodReport2.setName("someTestMethod2");
        TestResult testResult2 = TestResult.failed(new IOException("Exception"));
        testResult2.setStart(System.currentTimeMillis());
        testResult2.setEnd(testResult2.getStart() + 2000);
        testMethodReport2.setStatus(testResult2.getStatus());
        testMethodReport2.setDuration(testResult2.getEnd() - testResult2.getStart());
        testMethodReport2.setException("some exception");

        reporter.getLastTestClassReport().getTestMethodReports().add(testMethodReport2);
        reporter.setTestMethodReport(testMethodReport2);

        ScreenshotEntry sce = new ScreenshotEntry();
        sce.setPath(configuration.getRootDir().getAbsolutePath() + "/niceScreenshot.jpg");
        sce.setSize("56kB");
        sce.setPhase(When.BEFORE);
        sce.setWidth(300);
        sce.setHeight(500);

        ScreenshotEntry sce2 = new ScreenshotEntry();
        sce2.setPath(configuration.getRootDir().getAbsolutePath() + "/niceScreenshotBefore.jpg");
        sce2.setPhase(When.BEFORE);
        sce2.setWidth(300);
        sce2.setHeight(500);

        reporter.getLastTestMethodReport().getPropertyEntries().add(sce);
        reporter.getLastTestMethodReport().getPropertyEntries().add(sce2);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        Exporter exporter = new AsciiDocExporter(byteArrayOutputStream);
        exporter.setConfiguration(configuration);

        exporter.export(reporter.getReport());

        // Not the best way to test it but for now it is enough
        String content = new String(byteArrayOutputStream.toByteArray()).trim();

        assertThat(content, containsString("Arquillian"));
        assertThat(content, containsString("Extensions"));
        assertThat(content, containsString("Test Result"));
        assertThat(content, containsString("some exception"));
        assertThat(content, containsString("include::attributes.adoc[]"));
    }

    private TableEntry generateTable() {
        TableEntry tableEntry = new TableEntry();

        tableEntry.getTableHead().getRow().addCells(new TableCellEntry("header1"));

        tableEntry.getTableBody().addRows(generateTableRow(), generateTableRow());

        tableEntry.setTableName("some table name");

        return tableEntry;
    }

    private TableRowEntry generateTableRow() {
        TableRowEntry row = new TableRowEntry();

        row.addCells(new TableCellEntry("cell1"), new TableCellEntry("cell2"));

        return row;
    }

}
