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
package org.arquillian.recorder.reporter.exporter.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.arquillian.recorder.reporter.Exporter;
import org.arquillian.recorder.reporter.PropertyEntry;
import org.arquillian.recorder.reporter.ReportType;
import org.arquillian.recorder.reporter.Reportable;
import org.arquillian.recorder.reporter.ReporterConfiguration;
import org.arquillian.recorder.reporter.impl.type.AsciiDocReport;
import org.arquillian.recorder.reporter.model.ContainerReport;
import org.arquillian.recorder.reporter.model.DeploymentReport;
import org.arquillian.recorder.reporter.model.ExtensionReport;
import org.arquillian.recorder.reporter.model.Report;
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

/**
 * Exports reports to AsciiDoc file.
 *
 * The final output of document will depend on how you render the AsciiDoc document.
 *
 * This class can be extended to override any of the methods used to write the output so you can implement your own AsciiDoc
 * report. Keep in mind that if method is not overridden the default output is generated.
 *
 * @see AsciiDocReport
 *
 * @author <a href="asotobu@gmail.com">Alex Soto</a>
 *
 */
public class AsciiDocExporter implements Exporter {

    protected static final String NEW_LINE = System.getProperty("line.separator");
    protected static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("MM.dd.yyyy '-' HH:mm:ss");

    protected static final int PASSED_INDEX = 0;
    protected static final int FAILED_INDEX = 1;
    protected static final int SKIPPED_INDEX = 2;

    protected static final String FAILED_COLOR = "red";
    protected static final String SUCCESS_COLOR = "green";
    protected static final String WARNING_COLOR = "yellow";

    protected static final String SUCCESS_STEP = "thumbs-up";
    protected static final String FAIL_STEP = "thumbs-down";
    protected static final String NOT_PERFORMED_STEP = "unlink";

    protected BufferedWriter writer = null;
    protected OutputStream outputStream;
    protected ReporterConfiguration configuration;

    protected ResourceBundle resourceBundle;

    public AsciiDocExporter() {
        super();
    }

    public AsciiDocExporter(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public File export(Reportable reportable) throws Exception {

        Report report = (Report) reportable;
        createWriter();
        resolveResourceBundle();

        normalizeFilePaths(reportable);
        writeDocumentHeader();
        writeProperties(report.getPropertyEntries());
        writeExtensions(report.getExtensionReports());
        writeTestSuite(report.getTestSuiteReports());

        this.writer.flush();
        this.writer.close();

        return this.configuration.getFile();
    }

    private void resolveResourceBundle() {

        String language = this.configuration.getLanguage();

        if (language != null) {
            this.resourceBundle = ResourceBundle.getBundle("AsciiDocResources", new Locale(language));
        } else {
            this.resourceBundle = ResourceBundle.getBundle("AsciiDocResources");
        }

    }

    @Override
    public Class<? extends ReportType> getReportType() {
        return AsciiDocReport.class;
    }

    @Override
    public void setConfiguration(ReporterConfiguration configuration) {
        this.configuration = configuration;
    }

    private void createWriter() throws IOException {
        if (this.outputStream != null) {
            this.writer = new BufferedWriter(new OutputStreamWriter(this.outputStream));
        } else {
            this.writer = new BufferedWriter(new FileWriter(this.configuration.getFile()));
        }
    }

    /**
     * Method that is called to write properties to AsciiDoc document. Case of not override it, this method calls next methods
     * in order (writeTime, writeProperties, writeMedia, writeContainers, writeTestResults).
     *
     * @param testSuiteReports list;
     */
    protected void writeTestSuite(List<TestSuiteReport> testSuiteReports) throws IOException {

        for (TestSuiteReport testSuiteReport : testSuiteReports) {

            writer.append("== ").append(this.resourceBundle.getString("asciidoc.reporter.suite")).append(NEW_LINE)
                .append(NEW_LINE);
            writeTime(testSuiteReport.getStart(), testSuiteReport.getStop(), testSuiteReport.getDuration());
            writeProperties(testSuiteReport.getPropertyEntries());
            writeMedia(testSuiteReport.getPropertyEntries());
            writeContainers(testSuiteReport.getContainerReports());
            writeTestResults(testSuiteReport.getTestClassReports());
        }

    }

    /**
     * Method that is called to write test suite time to AsciiDoc document.
     *
     * @param start time.
     * @param stop end time.
     * @param duration in millis.
     */
    protected void writeTime(Date start, Date stop, long duration) throws IOException {

        writer.append(".").append(this.resourceBundle.getString("asciidoc.reporter.time")).append(NEW_LINE);
        writer.append("****").append(NEW_LINE);
        writer.append("*").append(this.resourceBundle.getString("asciidoc.reporter.start")).append("*").append(" ")
            .append(SIMPLE_DATE_FORMAT.format(start)).append(NEW_LINE).append(NEW_LINE);
        writer.append("*").append(this.resourceBundle.getString("asciidoc.reporter.stop")).append("*").append(" ")
            .append(SIMPLE_DATE_FORMAT.format(stop)).append(NEW_LINE).append(NEW_LINE);
        writer.append("*").append(this.resourceBundle.getString("asciidoc.reporter.duration")).append("*").append(" ")
            .append(convertDuration(duration, TimeUnit.MILLISECONDS, TimeUnit.SECONDS)).append("s")
            .append(NEW_LINE);
        writer.append("****").append(NEW_LINE).append(NEW_LINE);

    }

    private String convertDuration(long duration, TimeUnit fromTimeUnit, TimeUnit toTimeUnit) {
        return Long.toString(toTimeUnit.convert(duration, fromTimeUnit));
    }

    /**
     * First method called when report is being created. This method is used to set the document title and AsciiDoc document
     * attributes.
     *
     */
    protected void writeDocumentHeader() throws IOException {
        writer.append("= ").append(this.configuration.getTitle()).append(NEW_LINE);

        if(isAttributesFileSet()) {
            writer.append("include::").append(this.configuration.getAsciiDocAttributesFile()).append("[]").append(NEW_LINE);
        }
        if (isAsciidoctorOutput()) {
            writer.append(":icons: font").append(NEW_LINE);
        }

        writer.append(NEW_LINE);
    }

    private boolean isAttributesFileSet() {
        return !"".equals(this.configuration.getAsciiDocAttributesFile());
    }
    /**
     * Method that is called to write container properties to AsciiDoc document.
     *
     * @param properties container properties as a map
     */
    protected void writeContainerProperties(Map<String, String> properties) throws IOException {

        if (properties.size() > 0) {

            writer.append("[cols=\"2*\", options=\"header\"]").append(NEW_LINE);
            writer.append(".").append(this.resourceBundle.getString("asciidoc.reporter.properties")).append(NEW_LINE);
            writer.append("|===").append(NEW_LINE).append(NEW_LINE);
            writer.append("|KEY").append(NEW_LINE);
            writer.append("|VALUE").append(NEW_LINE).append(NEW_LINE);

            Set<Entry<String, String>> entrySet = properties.entrySet();

            for (Entry<String, String> entry : entrySet) {
                writer.append("|").append(entry.getKey()).append(NEW_LINE);
                writer.append("|").append(entry.getValue()).append(NEW_LINE).append(NEW_LINE);
            }

            writer.append("|===").append(NEW_LINE).append(NEW_LINE);
        }

    }

    /**
     * Method that is called to write properties to AsciiDoc document.
     *
     * @param propertyEntries list of properties to write to the report
     */
    protected void writeProperties(List<PropertyEntry> propertyEntries) throws IOException {

        if (containsAnyKeyValueEntryOrFileEntry(propertyEntries)) {

            writer.append("[cols=\"2*\", options=\"header\"]").append(NEW_LINE);
            writer.append(".").append(this.resourceBundle.getString("asciidoc.reporter.properties")).append(NEW_LINE);
            writer.append("|===").append(NEW_LINE).append(NEW_LINE);
            writer.append("|KEY").append(NEW_LINE);
            writer.append("|VALUE").append(NEW_LINE).append(NEW_LINE);

            writePropertiesRows(propertyEntries);

            writer.append("|===").append(NEW_LINE).append(NEW_LINE);
        }

        writeTables(propertyEntries);
    }

    private void writeTables(List<PropertyEntry> propertyEntries) throws IOException {
        List<TableEntry> tableEntries = filterTableEntries(propertyEntries);

        for (TableEntry tableEntry : tableEntries) {

            writer.append("[cols=\"" + tableEntry.getNumberOfColumns() + "*\"]").append(NEW_LINE);

            String tableName = tableEntry.getTableName();

            if (tableName != null && !tableName.isEmpty()) {
                writer.append("." + tableName).append(NEW_LINE);
            }

            writer.append("|===").append(NEW_LINE).append(NEW_LINE);

            for (TableRowEntry row : tableEntry.getTableBody().getRows()) {
                for (TableCellEntry cell : row.getCells()) {
                    writer.append("|").append(cell.getContent()).append(NEW_LINE);
                }
                writer.append(NEW_LINE);
            }

            writer.append("|===").append(NEW_LINE).append(NEW_LINE);

        }
    }

    private List<TableEntry> filterTableEntries(List<PropertyEntry> propertyEntries) {
        List<TableEntry> tableEntries = new ArrayList<TableEntry>();

        for (PropertyEntry propertyEntry : propertyEntries) {
            if (propertyEntry.getClass().isAssignableFrom(TableEntry.class)) {
                tableEntries.add((TableEntry) propertyEntry);
            }
        }

        return tableEntries;
    }

    private void writePropertiesRows(List<PropertyEntry> propertyEntries) throws IOException {
        for (PropertyEntry propertyEntry : propertyEntries) {
            if (propertyEntry instanceof KeyValueEntry) {
                KeyValueEntry keyValueEntry = (KeyValueEntry) propertyEntry;

                writer.append("|").append(keyValueEntry.getKey()).append(NEW_LINE);
                writer.append("|").append(keyValueEntry.getValue()).append(NEW_LINE).append(NEW_LINE);

            } else {

                if (propertyEntry instanceof FileEntry
                    && !(propertyEntry instanceof ScreenshotEntry || propertyEntry instanceof VideoEntry)) {
                    FileEntry fileEntry = (FileEntry) propertyEntry;
                    writer.append("2+|").append(fileEntry.getPath()).append(NEW_LINE).append(NEW_LINE);
                }

            }
        }
    }

    private void writeConfigurationRows(Map<String, String> configuration) throws IOException {
        for (Map.Entry<String, String> entry : configuration.entrySet()) {
            writer.append("|").append(entry.getKey()).append(NEW_LINE);
            writer.append("|").append(entry.getValue()).append(NEW_LINE).append(NEW_LINE);
        }
    }

    /**
     * Method that is called to write extensions to AsciiDoc document.
     *
     * @param extensionReports list.
     * @throws IOException
     */
    protected void writeExtensions(List<ExtensionReport> extensionReports) throws IOException {

        writer.append("== ").append(this.resourceBundle.getString("asciidoc.reporter.extensions")).append(NEW_LINE)
            .append(NEW_LINE);

        for (ExtensionReport extensionReport : extensionReports) {

            writer.append("[cols=\"2*\"]").append(NEW_LINE);
            writer.append(".").append(extensionReport.getQualifier()).append(NEW_LINE);
            writer.append("|===").append(NEW_LINE).append(NEW_LINE);

            writer.append("2+|").append(extensionReport.getQualifier()).append(NEW_LINE).append(NEW_LINE);

            writer.append("h|KEY").append(NEW_LINE);
            writer.append("h|VALUE").append(NEW_LINE).append(NEW_LINE);

            writeConfigurationRows(extensionReport.getConfiguration());

            writer.append("|===").append(NEW_LINE).append(NEW_LINE);

        }

    }

    /**
     * Method that is called to write media (screenshots and videos) to AsciiDoc document. Case of not override it, this method
     * calls next methods (writeVideo for videos and writeScreenshot for screenshots).
     *
     * @param propertyEntries list.
     * @throws IOException
     */
    protected void writeMedia(List<PropertyEntry> propertyEntries) throws IOException {

        for (PropertyEntry propertyEntry : propertyEntries) {
            if (propertyEntry instanceof VideoEntry) {
                VideoEntry videoEntry = (VideoEntry) propertyEntry;
                writeVideo(videoEntry);
            } else {
                if (propertyEntry instanceof ScreenshotEntry) {
                    ScreenshotEntry screenshotEntry = (ScreenshotEntry) propertyEntry;
                    writeScreenshot(screenshotEntry);
                }
            }
        }

    }

    /**
     * Method that is called to write video.
     *
     * @param videoEntry video entry to write
     */
    protected void writeVideo(VideoEntry videoEntry) throws IOException {

        writer.append("video::").append(videoEntry.getLink()).append("[]").append(NEW_LINE).append(NEW_LINE);

    }

    /**
     * Method that is called to write screenshot. This method is responsible to check the size of image.
     *
     * @param screenshotEntry screenshot entry to write
     * @throws IOException
     */
    protected void writeScreenshot(ScreenshotEntry screenshotEntry) throws IOException {

        int heigth = screenshotEntry.getHeight() * Integer.valueOf(this.configuration.getImageHeight()) / 100;
        int width = screenshotEntry.getWidth() * Integer.valueOf(this.configuration.getImageWidth()) / 100;

        boolean large = width > Integer.valueOf(this.configuration.getMaxImageWidth());

        if (large) {

            writer.append(".").append(screenshotEntry.getPhase().name()).append(NEW_LINE);
            writer.append(screenshotEntry.getLink()).append(" -> file://").append(screenshotEntry.getPath()).append("[")
            .append(this.resourceBundle.getString("asciidoc.reporter.screenshot"))
            .append("]").append(NEW_LINE).append(NEW_LINE);

        } else {

            writer.append(".").append(screenshotEntry.getPhase().name()).append(NEW_LINE);
            writer.append("image::").append(screenshotEntry.getLink())
                .append("[screenshot," + width + "," + heigth + "]")
                .append(NEW_LINE).append(NEW_LINE);
        }

    }

    /**
     *
     * Method that is called to write containers to AsciiDoc document. Case of not override it, this method calls next methods
     * in order (writeContainerProperties and writeDeployments).
     *
     * @param containerReports list.
     * @throws IOException
     */
    protected void writeContainers(List<ContainerReport> containerReports) throws IOException {

        writer.append("=== ").append(this.resourceBundle.getString("asciidoc.reporter.containers")).append(NEW_LINE)
            .append(NEW_LINE);

        for (ContainerReport containerReport : containerReports) {

            writer.append(".").append(containerReport.getQualifier()).append(NEW_LINE);
            writer.append("****").append(NEW_LINE);

            writeContainerProperties(containerReport.getConfiguration());
            writeDeployments(containerReport.getDeploymentReports());

            writer.append("****").append(NEW_LINE).append(NEW_LINE);
        }

    }

    /**
     * Method that is called to write deployment information to AsciiDoc document.
     *
     * @param deploymentReports list.
     * @throws IOException
     */
    protected void writeDeployments(List<DeploymentReport> deploymentReports) throws IOException {

        for (DeploymentReport deploymentReport : deploymentReports) {

            writer.append("[cols=\"3*\", options=\"header\"]").append(NEW_LINE);
            writer.append(".").append(this.resourceBundle.getString("asciidoc.reporter.deployment")).append(NEW_LINE);
            writer.append("|===").append(NEW_LINE).append(NEW_LINE);

            writer.append("|NAME").append(NEW_LINE);
            writer.append("|ARCHIVE").append(NEW_LINE);
            writer.append("|ORDER").append(NEW_LINE).append(NEW_LINE);

            writer.append("|").append(deploymentReport.getName()).append(NEW_LINE);
            writer.append("|").append(deploymentReport.getArchiveName()).append(NEW_LINE);
            writer.append("|").append(Integer.toString(deploymentReport.getOrder())).append(NEW_LINE).append(NEW_LINE);

            writer.append("|===").append(NEW_LINE).append(NEW_LINE);

            if (!"_DEFAULT_".equals(deploymentReport.getProtocol())) {
                writer.append("NOTE: ").append(deploymentReport.getProtocol()).append(NEW_LINE).append(NEW_LINE);
            }

        }

    }

    /**
     * Method that is called to write test results to AsciiDoc document. Case of not override it, this method calls next methods
     * in order (writeTestClassTitle, writeSummary, writeMedia and writeTestMethods).
     *
     * @param testClassReports list.
     * @throws IOException
     */
    protected void writeTestResults(List<TestClassReport> testClassReports) throws IOException {

        writer.append("=== ").append(this.resourceBundle.getString("asciidoc.reporter.tests")).append(NEW_LINE)
            .append(NEW_LINE);

        for (TestClassReport testClassReport : testClassReports) {

            writeTestClassTitle(testClassReport);
            writeSummary(testClassReport);
            writeMedia(testClassReport.getPropertyEntries());
            writeTestMethods(testClassReport);

        }

    }

    /**
     * Method that is called to write test class title to AsciiDoc document.
     *
     * @param testClassReport element.
     * @throws IOException
     */
    protected void writeTestClassTitle(TestClassReport testClassReport) throws IOException {

        writer.append("[[").append(testClassReport.getTestClassName()).append("]]").append(NEW_LINE);
        writer.append("==== ").append(testClassReport.getTestClassName()).append(" - ")
            .append(convertDuration(testClassReport.getDuration(), TimeUnit.MILLISECONDS, TimeUnit.SECONDS))
            .append("s");

        if (testClassReport.getRunAsClient()) {
            writer.append(" ").append("_").append(this.resourceBundle.getString("asciidoc.reporter.runAsClient")).append("_");
        }

        writer.append(NEW_LINE).append(NEW_LINE);
    }

    /**
     * Method that is called to write test summary (passed, failed tests) to AsciiDoc document.
     *
     * @param testClassReport element.
     * @throws IOException
     */
    protected void writeSummary(TestClassReport testClassReport) throws IOException {

        if (isReportMessage(testClassReport)) {

            writer.append("[IMPORTANT]").append(NEW_LINE);
            writer.append("====").append(NEW_LINE);
            writer.append(testClassReport.getReportMessage()).append(NEW_LINE);
            writer.append("====").append(NEW_LINE).append(NEW_LINE);

        }

        int[] results = countSummary(testClassReport.getTestMethodReports());

        writer.append(".").append(this.resourceBundle.getString("asciidoc.reporter.testResult")).append(NEW_LINE);
        writer.append("****").append(NEW_LINE);
        writer.append("*").append(this.resourceBundle.getString("asciidoc.reporter.passed")).append("*").append(" ").append(Integer.toString(results[PASSED_INDEX]))
            .append(NEW_LINE).append(NEW_LINE);
        writer.append("*").append(this.resourceBundle.getString("asciidoc.reporter.failed")).append("*").append(" ").append(Integer.toString(results[FAILED_INDEX]))
            .append(NEW_LINE).append(NEW_LINE);
        writer.append("****").append(NEW_LINE).append(NEW_LINE);

        writeProperties(testClassReport.getPropertyEntries());

    }

    private int[] countSummary(List<TestMethodReport> testMethodReports) {

        int[] summary = new int[] { 0, 0, 0 };

        for (TestMethodReport testMethodReport : testMethodReports) {
            switch (testMethodReport.getStatus()) {
                case PASSED:
                    summary[PASSED_INDEX]++;
                    break;
                case FAILED:
                    summary[FAILED_INDEX]++;
                    break;
                case SKIPPED:
                    summary[SKIPPED_INDEX]++;
                    break;
            }
        }

        return summary;

    }

    /**
     *
     * Method that is called to write all test method results of testClass report to AsciiDoc document. Case of not override it,
     * this method calls next methods in order for each method (writeTestMethodHeader, writeTestMethodProperties and
     * writeMedia).
     *
     * @param testClassReport test class report element for which test methods will be written
     */
    protected void writeTestMethods(TestClassReport testClassReport) throws IOException {

        List<TestMethodReport> testMethodReports = testClassReport.getTestMethodReports();

        for (TestMethodReport testMethodReport : testMethodReports) {

            writeTables(testMethodReport.getPropertyEntries());
            writeTestMethodHeader(testClassReport.getTestClassName(), testMethodReport);
            writeTestMethodProperties(testMethodReport);
            writeMedia(testMethodReport.getPropertyEntries());
        }

    }

    /**
     * Method that is called to write test method header (that is the title, result, stack trace, ...) to AsciiDoc document.
     *
     * @param testClassName name of the class to write test method header for
     * @param testMethodReport test method report to write
     */
    protected void writeTestMethodHeader(String testClassName, TestMethodReport testMethodReport) throws IOException {

        writer.append("[.lead]").append(NEW_LINE);
        writer.append(getIcon(testMethodReport)).append(" ").append(testMethodReport.getName()).append(" (")
            .append(convertDuration(testMethodReport.getDuration(), TimeUnit.MILLISECONDS, TimeUnit.SECONDS))
            .append("s").append(") -> ").append("<<").append(testClassName).append(", ")
            .append(this.resourceBundle.getString("asciidoc.reporter.goToTestClass")).append(">>").append(NEW_LINE).append(NEW_LINE);

        boolean isTestFailed = testMethodReport.getException() != null && !"".equals(testMethodReport.getException());

        if (isTestFailed) {
            writer.append("[WARNING]").append(NEW_LINE);
            writer.append("====").append(NEW_LINE);
            writer.append(testMethodReport.getException()).append(NEW_LINE);
            writer.append("====").append(NEW_LINE).append(NEW_LINE);
        }

    }

    private String getIcon(TestMethodReport testMethodReport) {

        switch (testMethodReport.getStatus()) {
            case PASSED: {
                if (isAsciidoctorOutput()) {
                    return getIcon(SUCCESS_STEP, SUCCESS_COLOR);
                } else {
                    return testMethodReport.getStatus().name();
                }
            }
            case FAILED: {
                if (isAsciidoctorOutput()) {
                    return getIcon(FAIL_STEP, FAILED_COLOR);
                } else {
                    return testMethodReport.getStatus().name();
                }
            }
            case SKIPPED: {
                if (isAsciidoctorOutput()) {
                    return getIcon(NOT_PERFORMED_STEP, WARNING_COLOR);
                } else {
                    return testMethodReport.getStatus().name();
                }
            }
            default:
                return "";
        }

    }

    private String getIcon(String iconName, String role) {
        return "icon:" + iconName + "[role=\"" + role + "\"]";
    }

    /**
     * Method that is called to write test method properties to AsciiDoc document.
     *
     * @param testMethodReport report of test method to write
     */
    protected void writeTestMethodProperties(TestMethodReport testMethodReport) throws IOException {

        if (isReportMessage(testMethodReport)) {

            writer.append("[IMPORTANT]").append(NEW_LINE);
            writer.append("====").append(NEW_LINE);
            writer.append(testMethodReport.getReportMessage()).append(NEW_LINE);
            writer.append("====").append(NEW_LINE).append(NEW_LINE);

        }

        if (containsAnyMethodProperty(testMethodReport)) {

            writer.append(".").append("Properties").append(NEW_LINE);
            writer.append("****").append(NEW_LINE).append(NEW_LINE);

            if (runAsClientOrOperateOnDeployment(testMethodReport)) {

                writer.append("[NOTE]").append(NEW_LINE);
                writer.append("====").append(NEW_LINE);
                writer.append("*").append(this.resourceBundle.getString("asciidoc.reporter.runAsClient")).append("* ")
                    .append(Boolean.toString(testMethodReport.getRunAsClient())).append(NEW_LINE).append(NEW_LINE);
                writer.append("*").append(this.resourceBundle.getString("asciidoc.reporter.operateOnDeployment")).append("* ")
                    .append(testMethodReport.getOperateOnDeployment()).append(NEW_LINE);
                writer.append("====").append(NEW_LINE).append(NEW_LINE);

            }

            writeProperties(testMethodReport.getPropertyEntries());

            writer.append("****").append(NEW_LINE).append(NEW_LINE);
        }

    }

    private boolean isReportMessage(TestClassReport testClassReport) {
        return testClassReport.getReportMessage() != null && !"".equals(testClassReport.getReportMessage().trim());
    }

    private boolean isReportMessage(TestMethodReport testMethodReport) {
        return testMethodReport.getReportMessage() != null && !"".equals(testMethodReport.getReportMessage().trim());
    }

    private boolean runAsClientOrOperateOnDeployment(TestMethodReport testMethodReport) {
        return testMethodReport.getRunAsClient()
            || (testMethodReport.getOperateOnDeployment() != null && !"_DEFAULT_".equals(testMethodReport
                .getOperateOnDeployment()));
    }

    private boolean containsAnyMethodProperty(TestMethodReport testMethodReport) {
        return containsAnyKeyValueEntryOrFileEntry(testMethodReport.getPropertyEntries())
            || runAsClientOrOperateOnDeployment(testMethodReport);
    }

    protected boolean containsAnyKeyValueEntryOrFileEntry(List<PropertyEntry> propertyEntries) {

        for (PropertyEntry propertyEntry : propertyEntries) {

            if (propertyEntries instanceof KeyValueEntry
                || (propertyEntry instanceof FileEntry && !(propertyEntry instanceof ScreenshotEntry || propertyEntry instanceof VideoEntry))) {
                return true;
            }

        }

        return false;

    }

    /**
     * In case of html exporter, all resources which have some file path in final html and are displayed to user (e.g. videos or
     * screenshots) have file paths in absolute form. This prevents html to be portable from one place to another (from one host
     * to another) since file paths do not match anymore. By normalization, screenshot and video directories are moved to the
     * same parent as the final html file is stored under and paths are relativized.
     *
     * Only screenshot and video entries are taken into account.
     *
     * @param report
     */
    private void normalizeFilePaths(Reportable report) {
        for (TestSuiteReport testSuiteReport : ((Report) report).getTestSuiteReports()) {
            for (PropertyEntry entry : testSuiteReport.getPropertyEntries()) {
                if (entry instanceof VideoEntry) {
                    VideoEntry e = (VideoEntry) entry;
                    e.setLink(e.getPath().substring(configuration.getRootDir().getAbsolutePath().length() + 1));
                }
            }
            for (TestClassReport testClassReport : testSuiteReport.getTestClassReports()) {
                for (PropertyEntry entry : testClassReport.getPropertyEntries()) {
                    if (entry instanceof VideoEntry) {
                        VideoEntry e = (VideoEntry) entry;
                        e.setLink(e.getPath().substring(configuration.getRootDir().getAbsolutePath().length() + 1));
                    }
                }
                for (TestMethodReport testMethodReport : testClassReport.getTestMethodReports()) {
                    for (PropertyEntry entry : testMethodReport.getPropertyEntries()) {
                        if (entry instanceof VideoEntry) {
                            VideoEntry e = (VideoEntry) entry;
                            e.setLink(e.getPath().substring(configuration.getRootDir().getAbsolutePath().length() + 1));
                        } else if (entry instanceof ScreenshotEntry) {
                            ScreenshotEntry e = (ScreenshotEntry) entry;
                            e.setLink(e.getPath().substring(configuration.getRootDir().getAbsolutePath().length() + 1));
                        }
                    }
                }
            }
        }
    }

    /**
     * Method that checks if user has configured if output should be compliant with AsciiDoc format or can use Asciidoctor
     * features.
     *
     * @return true if output can be generated using Asciidoctor features. False otherwise.
     */
    private boolean isAsciidoctorOutput() {
        return !this.configuration.isAsciiDocCompliant();
    }
}
