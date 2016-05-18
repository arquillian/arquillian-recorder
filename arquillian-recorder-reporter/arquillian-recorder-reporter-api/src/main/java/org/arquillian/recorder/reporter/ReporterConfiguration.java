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

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arquillian.extension.recorder.Configuration;

/**
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class ReporterConfiguration extends Configuration<ReporterConfiguration> {

    private static final Logger logger = Logger.getLogger(ReporterConfiguration.class.getName());

    public static final String DEFAULT_TYPE = "xml";

    public static final String DEFAULT_LANGUAGE = "en";

    public static final String DEFAULT_MAX_IMAGE_WIDTH = "500";

    public static final String DEFAULT_ASCIIDOC_STANDARD_COMPLIANT = "false";

    // in percents

    public static final String DEFAULT_IMAGE_WIDTH = "100";

    public static final String DEFAULT_IMAGE_HEIGHT = "100";

    public static final String DEFAULT_TITLE = "Arquillian test run report";

    private String report = DEFAULT_TYPE;

    private String file = getFileDefaultFileName();

    private static final String ROOT_DIR = "target";

    private static final String TEMPLATE = "template.xsl";

    private final String reportAfterEvery = ReportFrequency.CLASS.toString();

    private static final String LANGUAGE = "en";

    private static final String MAX_IMAGE_WIDTH = DEFAULT_MAX_IMAGE_WIDTH;

    private static final String IMAGE_WIDTH = DEFAULT_IMAGE_WIDTH;

    private static final String IMAGE_HEIGHT = DEFAULT_IMAGE_HEIGHT;

    private static final String TITLE = DEFAULT_TITLE;

    private static final String ASCIIDOC_STANDARD_COMPLIANT = DEFAULT_ASCIIDOC_STANDARD_COMPLIANT;

    private static final String ASCII_DOC_ATTRIBUTES_FILE = "";

    public String getAsciiDocAttributesFile() {
        return getProperty("asciiDocAttributesFile", ASCII_DOC_ATTRIBUTES_FILE);
    }

    /**
     *
     * @return true if we want generated asciidoc document be compliant with standard format. False otherwise and output will be an AsciiDoc document to be rendered by Asciidoctor.
     */
    public boolean isAsciiDocCompliant() {
        return Boolean.parseBoolean(getProperty("asciidocStandardCompliant", ASCIIDOC_STANDARD_COMPLIANT).toLowerCase());
    }

    /**
     *
     * @return type of report we want to get, it defaults to "xml"
     */
    public String getReport() {
        return getProperty("report", report).toLowerCase();
    }

    /**
     *
     * @return file where to export a report
     */
    public File getFile() {
        return new File(getRootDir(), getProperty("file", file));
    }

    /**
     *
     * @return root directory which prepends {@link #getFile()}
     */
    public File getRootDir() {
        return new File(getProperty("rootDir", ROOT_DIR));
    }

    /**
     * XSL template for transforming XML to HTML when using HTML report type, defaults to "template.xsl". When this file is not
     * found, default system XSL template is used.
     *
     * @return xsl template file
     */
    public File getTemplate() {
        return new File(getProperty("template", TEMPLATE));
    }

    public String getReportAfterEvery() {
        return getProperty("reportAfterEvery", reportAfterEvery).toLowerCase();
    }

    /**
     * Language to use for resulting report. Defaults to "en" as English.
     *
     * @return language
     */
    public String getLanguage() {
        return getProperty("language", LANGUAGE).toLowerCase();
    }

    /**
     * Gets width for displayed images. When some image has its width lower than this number, it will be displayed as a link
     * instead as an image directly displayed on a resulting HTML page.
     *
     * @return maximum width of an image to be displayed
     */
    public String getMaxImageWidth() {
        return getProperty("maxImageWidth", MAX_IMAGE_WIDTH);
    }

    /**
     *
     * @return the width of all images in percents. If this number is smaller then 100, the width of all images will be resized
     *         from presentation point of view.
     */
    public String getImageWidth() {
        return getProperty("imageWidth", IMAGE_WIDTH);
    }

    /**
     *
     * @return the height of all images in percets. If this number is smaller then 100, the height of all images will be resized
     *         from presentation point of view.
     */
    public String getImageHeight() {
        return getProperty("imageHeight", IMAGE_HEIGHT);
    }

    public String getTitle() {
        return getProperty("title", TITLE);
    }

    private static String getFileDefaultFileName() {
        return new StringBuilder()
            .append("arquillian_report")
            .append(".")
            .append(DEFAULT_TYPE)
            .toString();
    }

    @Override
    public void validate() throws ReporterConfigurationException {
        if (report.isEmpty()) {
            logger.info("Report type can not be empty string! Choosing default type \"xml\"");
            report = DEFAULT_TYPE;
        }

        if (getTitle() == null || getTitle().isEmpty()) {
            setProperty("title", DEFAULT_TITLE);
        }

        try {
            int width = Integer.parseInt(getMaxImageWidth());
            if (width <= 0) {
                setProperty("maxImageWidth", DEFAULT_MAX_IMAGE_WIDTH);
            }
        } catch (NumberFormatException ex) {
            setProperty("maxImageWidth", DEFAULT_MAX_IMAGE_WIDTH);
        }

        try {
            int width = Integer.parseInt(getImageWidth());
            if (width <= 0 || width > 100) {
                setProperty("imageWidth", DEFAULT_IMAGE_WIDTH);
            }
        } catch (NumberFormatException ex) {
            setProperty("imageWidth", DEFAULT_IMAGE_WIDTH);
        }

        try {
            int height = Integer.parseInt(getImageHeight());
            if (height <= 0 || height > 100) {
                setProperty("imageHeight", DEFAULT_IMAGE_HEIGHT);
            }
        } catch (NumberFormatException ex) {
            setProperty("imageHeight", DEFAULT_IMAGE_HEIGHT);
        }

        try {
            ReportFrequency.valueOf(ReportFrequency.class, getReportAfterEvery().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ReporterConfigurationException(
                "Report frequency you specified in arquillian.xml is not valid. "
                        + "The configured frequency is: " + getReportAfterEvery().toUpperCase() + ". "
                        + "The supported frequencies are: " + ReportFrequency.getAll(), ex);
        }

        // we check language only for html output
        if (getProperty("report", report).startsWith("htm")) {
            LanguageResolver languageResolver = new LanguageResolver();

            List<String> supportedLanguages = languageResolver.getSupportedLanguages();

            if (!languageResolver.isLanguageSupported(getLanguage())) {
                logger.log(Level.INFO, "Language you set ({0}) for HTML report is not supported. It will default to "
                    + "\"{1}\". When you are executing this from IDE, put reporter api jar to build path among external "
                    + "jars in order to scan it.",
                    new Object[] { getLanguage(), DEFAULT_LANGUAGE, supportedLanguages });
                setProperty("language", DEFAULT_LANGUAGE);
            }
        }

        try {
            if (!getRootDir().exists()) {
                boolean created = getRootDir().mkdir();
                if (!created) {
                    throw new ReporterConfigurationException("Unable to create root directory " + getRootDir().getAbsolutePath());
                }
            } else {
                if (!getRootDir().isDirectory()) {
                    throw new ReporterConfigurationException("Root directory you specified is not a directory - "
                        + getRootDir().getAbsolutePath());
                }
                if (!getRootDir().canWrite()) {
                    throw new ReporterConfigurationException(
                        "You can not write to '" + getRootDir().getAbsolutePath() + "'.");
                }
            }
        } catch (SecurityException ex) {
            throw new ReporterConfigurationException(
                "You are not permitted to operate on specified resource: " + getRootDir().getAbsolutePath() + "'.", ex);
        }

        setFileName(getProperty("report", report));
    }

    public void setFileName(String reportType) {
        String fileProperty = getProperty("file", file);
        setProperty("report", reportType);
        String reportProperty = getProperty("report", reportType);

        if (!fileProperty.endsWith(reportProperty)) {
            StringBuilder sb = new StringBuilder();
            if (fileProperty.contains(".")) {
                sb.append(fileProperty.substring(0, fileProperty.lastIndexOf(".")));
            } else {
                sb.append(fileProperty);
            }
            sb.append(".");
            sb.append(reportProperty);
            file = sb.toString();
            setProperty("file", file);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-40s %s\n", "report", getReport()));
        sb.append(String.format("%-40s %s\n", "rootDir", getRootDir().getPath()));
        sb.append(String.format("%-40s %s\n", "file", getFile().getPath()));
        sb.append(String.format("%-40s %s\n", "template", getTemplate().getPath()));
        sb.append(String.format("%-40s %s\n", "reportAfterEvery", getReportAfterEvery()));
        sb.append(String.format("%-40s %s\n", "language", getLanguage()));
        sb.append(String.format("%-40s %s\n", "maxImageWidth", getMaxImageWidth()));
        sb.append(String.format("%-40s %s%%\n", "imageWidth", getImageWidth()));
        sb.append(String.format("%-40s %s%%\n", "imageHeight", getImageHeight()));
        return sb.toString();
    }

}