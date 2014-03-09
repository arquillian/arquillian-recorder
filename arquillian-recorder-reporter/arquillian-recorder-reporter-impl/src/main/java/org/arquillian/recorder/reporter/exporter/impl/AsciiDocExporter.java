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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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

/**
 * Exports reports to AsciiDoc file.
 * 
 * The final output of document will depend on how you render the AsciiDoc
 * document.
 * 
 * @see {@link AsciiDocReport}
 * 
 * @author <a href="asotobu@gmail.com">Alex Soto</a>
 * 
 */
public class AsciiDocExporter implements Exporter {

    private static final int PASSED_INDEX = 0;
    private static final int FAILED_INDEX = 1;
    private static final int SKIPPED_INDEX = 2;

    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("MM.dd.yyyy '-' HH:mm:ss");

    private BufferedWriter writer = null;
    private OutputStream outputStream;

    private static final String FAILED_COLOR = "red";
    private static final String SUCCESS_COLOR = "green";
    private static final String WARNING_COLOR = "yellow";

    private static final String SUCCESS_STEP = "thumbs-up";
    private static final String FAIL_STEP = "thumbs-down";
    private static final String NOT_PERFORMED_STEP = "unlink";

    private ReporterConfiguration configuration;

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

        normalizeFilePaths(reportable);
        writeDocumentHeader();
        writePropertiesTable(report.getPropertyEntries());
        writeExtensions(report.getExtensionReports());
        writeTestSuite(report.getTestSuiteReports());

        this.writer.flush();
        this.writer.close();

        return this.configuration.getFile();
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

    private void writeTestSuite(List<TestSuiteReport> testSuiteReports) throws IOException {

        for (TestSuiteReport testSuiteReport : testSuiteReports) {

            writer.append("== ").append("Suite").append(NEW_LINE).append(NEW_LINE);
            writeTime(testSuiteReport.getStart(), testSuiteReport.getStop(), testSuiteReport.getDuration());
            writePropertiesTable(testSuiteReport.getPropertyEntries());
            writeMedia(testSuiteReport.getPropertyEntries());
            writeContainers(testSuiteReport.getContainerReports());
            writeTestResults(testSuiteReport.getTestClassReports());
        }

    }

    private void writeTime(Date start, Date stop, long duration) throws IOException {

        writer.append(".").append("Time").append(NEW_LINE);
        writer.append("****").append(NEW_LINE);
        writer.append("*").append("Start:").append("*").append(" ").append(SIMPLE_DATE_FORMAT.format(start))
                .append(NEW_LINE).append(NEW_LINE);
        writer.append("*").append("Stop:").append("*").append(" ").append(SIMPLE_DATE_FORMAT.format(stop))
                .append(NEW_LINE).append(NEW_LINE);
        writer.append("*").append("Duration:").append("*").append(" ")
                .append(convertDuration(duration, TimeUnit.MILLISECONDS, TimeUnit.SECONDS)).append("s")
                .append(NEW_LINE);
        writer.append("****").append(NEW_LINE).append(NEW_LINE);

    }

    private String convertDuration(long duration, TimeUnit fromTimeUnit, TimeUnit toTimeUnit) {
        return Long.toString(toTimeUnit.convert(duration, fromTimeUnit));
    }

    private void writeDocumentHeader() throws IOException {
        writer.append("= ").append("Arquillian test run report").append(NEW_LINE);
        writer.append(":icons: font").append(NEW_LINE).append(NEW_LINE);
    }

    private void writePropertiesTable(Map<String, String> properties) throws IOException {

        if (properties.size() > 0) {

            writer.append("[cols=\"2*\", options=\"header\"]").append(NEW_LINE);
            writer.append(".").append("Properties").append(NEW_LINE);
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

    private void writePropertiesTable(List<PropertyEntry> propertyEntries) throws IOException {

        if (containsAnyKeyValueEntryOrFileEntry(propertyEntries) > 0) {

            writer.append("[cols=\"2*\", options=\"header\"]").append(NEW_LINE);
            writer.append(".").append("Properties").append(NEW_LINE);
            writer.append("|===").append(NEW_LINE).append(NEW_LINE);
            writer.append("|KEY").append(NEW_LINE);
            writer.append("|VALUE").append(NEW_LINE).append(NEW_LINE);

            writePropertiesRows(propertyEntries);

            writer.append("|===").append(NEW_LINE).append(NEW_LINE);
        }

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
                    writer.append("2x|").append(fileEntry.getPath()).append(NEW_LINE).append(NEW_LINE);
                }

            }
        }
    }

    private void writeExtensions(List<ExtensionReport> extensionReports) throws IOException {

        writer.append("== ").append("Extensions").append(NEW_LINE).append(NEW_LINE);

        for (ExtensionReport extensionReport : extensionReports) {

            writer.append("[cols=\"2*\"]").append(NEW_LINE);
            writer.append(".").append(extensionReport.getQualifier()).append(NEW_LINE);
            writer.append("|===").append(NEW_LINE).append(NEW_LINE);

            writer.append("2+|").append(extensionReport.getQualifier()).append(NEW_LINE).append(NEW_LINE);

            writer.append("h|KEY").append(NEW_LINE);
            writer.append("h|VALUE").append(NEW_LINE).append(NEW_LINE);

            writePropertiesRows(extensionReport.getPropertyEntries());

            writer.append("|===").append(NEW_LINE).append(NEW_LINE);

        }

    }

    private void writeMedia(List<PropertyEntry> propertyEntries) throws IOException {

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

    private void writeVideo(VideoEntry videoEntry) throws IOException {

        writer.append("video::").append(videoEntry.getPath()).append("[]").append(NEW_LINE).append(NEW_LINE);

    }

    private void writeScreenshot(ScreenshotEntry screenshotEntry) throws IOException {

        boolean large = screenshotEntry.getWidth() > 500;

        if (large) {

            writer.append(".").append(screenshotEntry.getPhase().name()).append(NEW_LINE);
            writer.append(screenshotEntry.getLink()).append("[").append("Screenshot").append(" - ")
                    .append(screenshotEntry.getPath()).append("]").append(NEW_LINE).append(NEW_LINE);

        } else {

            writer.append(".").append(screenshotEntry.getPhase().name()).append(NEW_LINE);
            writer.append("image::").append(screenshotEntry.getLink()).append("[]").append(NEW_LINE).append(NEW_LINE);

        }

    }

    private void writeContainers(List<ContainerReport> containerReports) throws IOException {

        writer.append("=== ").append("Containers").append(NEW_LINE).append(NEW_LINE);

        for (ContainerReport containerReport : containerReports) {

            writer.append(".").append(containerReport.getQualifier()).append(NEW_LINE);
            writer.append("****").append(NEW_LINE);

            writePropertiesTable(containerReport.getConfiguration());
            writeDeployments(containerReport.getDeploymentReports());

            writer.append("****").append(NEW_LINE).append(NEW_LINE);
        }

    }

    private void writeDeployments(List<DeploymentReport> deploymentReports) throws IOException {

        for (DeploymentReport deploymentReport : deploymentReports) {

            writer.append("[cols=\"3*\", options=\"header\"]").append(NEW_LINE);
            writer.append(".").append("Deployment").append(NEW_LINE);
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

    private void writeTestResults(List<TestClassReport> testClassReports) throws IOException {

        writer.append("=== ").append("Tests").append(NEW_LINE).append(NEW_LINE);

        for (TestClassReport testClassReport : testClassReports) {

            writeTestClassTitle(testClassReport);
            writeSummary(testClassReport);
            writeMedia(testClassReport.getPropertyEntries());
            writeTestMethods(testClassReport);

        }

    }

    private void writeTestClassTitle(TestClassReport testClassReport) throws IOException {

        writer.append("[[").append(testClassReport.getTestClassName()).append("]]").append(NEW_LINE);
        writer.append("==== ").append(testClassReport.getTestClassName()).append(" - ")
                .append(convertDuration(testClassReport.getDuration(), TimeUnit.MILLISECONDS, TimeUnit.SECONDS))
                .append("s");

        if (testClassReport.getRunAsClient()) {
            writer.append(" ").append("_").append("(run as client)").append("_");
        }

        writer.append(NEW_LINE).append(NEW_LINE);
    }

    private void writeSummary(TestClassReport testClassReport) throws IOException {

        int[] results = countSummary(testClassReport.getTestMethodReports());

        writer.append(".").append("Test Result").append(NEW_LINE);
        writer.append("****").append(NEW_LINE);
        writer.append("*").append("Passed:").append("*").append(" ").append(Integer.toString(results[PASSED_INDEX]))
                .append(NEW_LINE).append(NEW_LINE);
        writer.append("*").append("Failed:").append("*").append(" ").append(Integer.toString(results[FAILED_INDEX]))
                .append(NEW_LINE).append(NEW_LINE);
        writer.append("*").append("Skipped:").append("*").append(" ").append(Integer.toString(results[SKIPPED_INDEX]))
                .append(NEW_LINE).append(NEW_LINE);
        writer.append("****").append(NEW_LINE).append(NEW_LINE);

        writePropertiesTable(testClassReport.getPropertyEntries());

    }

    private int[] countSummary(List<TestMethodReport> testMethodReports) {

        int summary[] = new int[] { 0, 0, 0 };

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

    private void writeTestMethods(TestClassReport testClassReport) throws IOException {

        List<TestMethodReport> testMethodReports = testClassReport.getTestMethodReports();

        for (TestMethodReport testMethodReport : testMethodReports) {

            writeTestMethodTitle(testClassReport.getTestClassName(), testMethodReport);
            writeTestMethodProperties(testMethodReport);
            writeMedia(testMethodReport.getPropertyEntries());

        }

    }

    private void writeTestMethodTitle(String testClassName, TestMethodReport testMethodReport) throws IOException {

        writer.append("[.lead]").append(NEW_LINE);
        writer.append(getIcon(testMethodReport)).append(" ").append(testMethodReport.getName()).append(" (")
                .append(convertDuration(testMethodReport.getDuration(), TimeUnit.MILLISECONDS, TimeUnit.SECONDS))
                .append("s").append(") -> ").append("<<").append(testClassName).append(", ")
                .append("Go To Test Class>>").append(NEW_LINE).append(NEW_LINE);

        boolean isTestFailed = testMethodReport.getException() != null && !"".equals(testMethodReport.getException());

        if (isTestFailed) {
            writer.append("[IMPORTANT]").append(NEW_LINE);
            writer.append("====").append(NEW_LINE);
            writer.append(testMethodReport.getException()).append(NEW_LINE);
            writer.append("====").append(NEW_LINE).append(NEW_LINE);
        }

    }

    private String getIcon(TestMethodReport testMethodReport) {

        switch (testMethodReport.getStatus()) {
        case PASSED:
            return getIcon(SUCCESS_STEP, SUCCESS_COLOR);
        case FAILED:
            return getIcon(FAIL_STEP, FAILED_COLOR);
        case SKIPPED:
            return getIcon(NOT_PERFORMED_STEP, WARNING_COLOR);
        default:
            return "";
        }

    }

    private String getIcon(String iconName, String role) {
        return "icon:" + iconName + "[role=\"" + role + "\"]";
    }

    private void writeTestMethodProperties(TestMethodReport testMethodReport) throws IOException {

        if (containsAnyMethodProperty(testMethodReport)) {

            writer.append(".").append("Properties").append(NEW_LINE);
            writer.append("****").append(NEW_LINE).append(NEW_LINE);

            if (runAsClientOrOperateOnDeployment(testMethodReport)) {

                writer.append("[NOTE]").append(NEW_LINE);
                writer.append("====").append(NEW_LINE);
                writer.append("*").append("Run As Client ").append("*")
                        .append(Boolean.toString(testMethodReport.getRunAsClient())).append(NEW_LINE).append(NEW_LINE);
                writer.append("*").append("Operate On Deployment ").append("*")
                        .append(testMethodReport.getOperateOnDeployment()).append(NEW_LINE);
                writer.append("====").append(NEW_LINE).append(NEW_LINE);

            }

            writePropertiesTable(testMethodReport.getPropertyEntries());

            writer.append("****").append(NEW_LINE).append(NEW_LINE);
        }

    }

    private boolean runAsClientOrOperateOnDeployment(TestMethodReport testMethodReport) {
        return testMethodReport.getRunAsClient()
                || (testMethodReport.getOperateOnDeployment() != null && !"_DEFAULT_".equals(testMethodReport
                        .getOperateOnDeployment()));
    }

    private boolean containsAnyMethodProperty(TestMethodReport testMethodReport) {
        return containsAnyKeyValueEntryOrFileEntry(testMethodReport.getPropertyEntries()) > 0
                || runAsClientOrOperateOnDeployment(testMethodReport);
    }

    private int containsAnyKeyValueEntryOrFileEntry(List<PropertyEntry> propertyEntries) {

        int count = 0;

        for (PropertyEntry propertyEntry : propertyEntries) {

            if (propertyEntries instanceof KeyValueEntry
                    || (propertyEntry instanceof FileEntry && !(propertyEntry instanceof ScreenshotEntry || propertyEntry instanceof VideoEntry))) {
                count++;
            }

        }

        return count;

    }

    /**
     * In case of html exporter, all resources which have some file path in
     * final html and are displayed to user (e.g. videos or screenshots) have
     * file paths in absolute form. This prevents html to be portable from one
     * place to another (from one host to another) since file paths do not match
     * anymore. By normalization, screenshot and video directories are moved to
     * the same parent as the final html file is stored under and paths are
     * relativized.
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

}
