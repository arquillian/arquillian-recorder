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

import java.util.HashMap;
import java.util.Map;

import org.arquillian.extension.recorder.When;
import org.arquillian.recorder.reporter.Exporter;
import org.arquillian.recorder.reporter.JAXBContextFactory;
import org.arquillian.recorder.reporter.Reporter;
import org.arquillian.recorder.reporter.ReporterConfiguration;
import org.arquillian.recorder.reporter.exporter.impl.HTMLExporter;
import org.arquillian.recorder.reporter.exporter.impl.XMLExporter;
import org.arquillian.recorder.reporter.impl.ReporterImpl;
import org.arquillian.recorder.reporter.model.ContainerReport;
import org.arquillian.recorder.reporter.model.DeploymentReport;
import org.arquillian.recorder.reporter.model.ExtensionReport;
import org.arquillian.recorder.reporter.model.Report;
import org.arquillian.recorder.reporter.model.TestClassReport;
import org.arquillian.recorder.reporter.model.TestMethodReport;
import org.arquillian.recorder.reporter.model.TestSuiteReport;
import org.arquillian.recorder.reporter.model.entry.FileEntry;
import org.arquillian.recorder.reporter.model.entry.GroupEntry;
import org.arquillian.recorder.reporter.model.entry.KeyValueEntry;
import org.arquillian.recorder.reporter.model.entry.ScreenshotEntry;
import org.arquillian.recorder.reporter.model.entry.VideoEntry;
import org.arquillian.recorder.reporter.model.entry.table.TableCellEntry;
import org.arquillian.recorder.reporter.model.entry.table.TableEntry;
import org.arquillian.recorder.reporter.model.entry.table.TableRowEntry;
import org.jboss.arquillian.test.spi.TestResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
@RunWith(JUnit4.class)
public class ReporterTestCase {

    @Test
    public void configurationTest() {
        getHtmlConfig();
        getXmlConfig();
    }

    @Test
    public void testReporter() throws Exception {
        ReporterConfiguration htmlConfiguration = getHtmlConfig();
        ReporterConfiguration xmlConfiguration = getXmlConfig();

        Reporter reporter = new ReporterImpl();

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

        // extensions
        ExtensionReport extensionReport = new ExtensionReport();
        extensionReport.setQualifier("myExtension1");
        Map<String, String> extensionConfiguration = new HashMap<String, String>();
        extensionConfiguration.put("extensionKey1", "extensionKey1");
        extensionConfiguration.put("extensionKey2", "extensionKey2");
        extensionReport.setConfiguration(extensionConfiguration);
        reporter.getReport().getExtensionReports().add(extensionReport);

        // suite table
        reporter.getLastTestSuiteReport().getPropertyEntries().add(generateTable());

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
        // test class table
        reporter.getLastTestClassReport().getPropertyEntries().add(generateTable());

        VideoEntry videoEntry = new VideoEntry();
        videoEntry.setPath("some/someVideo.mp4");
        videoEntry.setSize("54M");
        // reporter.getReporterCursor().getCursor().getPropertyEntries().add(videoEntry);

        TestMethodReport testMethodReport = new TestMethodReport();
        testMethodReport.setName("someTestMethod");
        TestResult testResult = TestResult.passed();
        testResult.setStart(System.currentTimeMillis());
        testResult.setEnd(testResult.getStart() + 1000);
        testMethodReport.setStatus(testResult.getStatus());
        testMethodReport.setDuration(testResult.getEnd() - testResult.getStart());

        // method table

        testMethodReport.getPropertyEntries().add(generateTable());
        testMethodReport.getPropertyEntries().add(generateGroup());

        reporter.getLastTestClassReport().getTestMethodReports().add(testMethodReport);
        reporter.setTestMethodReport(testMethodReport);

        TestMethodReport testMethodReport2 = new TestMethodReport();
        testMethodReport2.setName("someTestMethod2");
        TestResult testResult2 = TestResult.failed(new RuntimeException());
        testResult2.setStart(System.currentTimeMillis());
        testResult2.setEnd(testResult2.getStart() + 2000);
        testMethodReport2.setStatus(testResult2.getStatus());
        testMethodReport2.setDuration(testResult2.getEnd() - testResult2.getStart());
        testMethodReport2.setException("some exception");

        reporter.getLastTestClassReport().getTestMethodReports().add(testMethodReport2);
        reporter.setTestMethodReport(testMethodReport2);

        ScreenshotEntry sce = new ScreenshotEntry();
        sce.setPath("niceScreenshot.jpg");
        sce.setSize("56kB");
        sce.setPhase(When.BEFORE);

        ScreenshotEntry sce2 = new ScreenshotEntry();
        sce2.setPath("niceScreenshotBefore.jpg");
        sce2.setPhase(When.BEFORE);

        // reporter.getReporterCursor().getCursor().getPropertyEntries().add(sce);
        // reporter.getReporterCursor().getCursor().getPropertyEntries().add(sce2);

        Exporter exporter1 = new XMLExporter(JAXBContextFactory.initContext(Report.class));
        Exporter exporter2 = new HTMLExporter(JAXBContextFactory.initContext(Report.class));
        exporter1.setConfiguration(xmlConfiguration);
        exporter2.setConfiguration(htmlConfiguration);

        exporter1.export(reporter.getReport());
        exporter2.export(reporter.getReport());
    }

    private TableEntry generateTable() {
        TableEntry tableEntry = new TableEntry();

        tableEntry.getTableHead().getRow().addCells(new TableCellEntry("header1"), new TableCellEntry("header2"));

        tableEntry.getTableBody().addRows(generateTableRow(), generateTableRow());

        tableEntry.getTableFoot().getRow().addCells(new TableCellEntry("foot1"), new TableCellEntry("foot2"));

        return tableEntry;
    }

    private TableRowEntry generateTableRow() {
        TableRowEntry row = new TableRowEntry();

        row.addCells(new TableCellEntry("cell1"), new TableCellEntry("cell2"));

        return row;
    }

    private ReporterConfiguration getHtmlConfig() {

        ReporterConfiguration configuration = new ReporterConfiguration();

        Map<String, String> configMap = new HashMap<String, String>();
        configMap.put("report", "html");
        configMap.put("file", "html_report");
        configuration.setConfiguration(configMap);

        configuration.validate();
        System.out.println(configuration.toString());

        return configuration;
    }

    private ReporterConfiguration getXmlConfig() {

        ReporterConfiguration configuration = new ReporterConfiguration();

        Map<String, String> configMap = new HashMap<String, String>();
        configMap.put("report", "xml");
        configMap.put("file", "xml_report");
        configuration.setConfiguration(configMap);

        configuration.validate();
        System.out.println(configuration.toString());

        return configuration;
    }

    private GroupEntry generateGroup() {
        GroupEntry group1 = new GroupEntry("group1");
        GroupEntry group2 = new GroupEntry("group2");

        group1.getPropertyEntries().add(new KeyValueEntry("key1", "value1"));
        group1.getPropertyEntries().add(new KeyValueEntry("key2", "value2"));
        group1.getPropertyEntries().add(generateTable());

        group2.getPropertyEntries().add(new KeyValueEntry("key3", "value3"));

        group1.getPropertyEntries().add(group2);

        return group1;
    }
}
