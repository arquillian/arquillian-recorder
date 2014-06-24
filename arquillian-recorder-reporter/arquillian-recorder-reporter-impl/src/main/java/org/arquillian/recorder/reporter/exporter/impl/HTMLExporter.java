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

import java.io.File;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.arquillian.recorder.reporter.Exporter;
import org.arquillian.recorder.reporter.PropertyEntry;
import org.arquillian.recorder.reporter.ReportType;
import org.arquillian.recorder.reporter.Reportable;
import org.arquillian.recorder.reporter.ReporterConfiguration;
import org.arquillian.recorder.reporter.impl.type.HTMLReport;
import org.arquillian.recorder.reporter.model.Report;
import org.arquillian.recorder.reporter.model.TestClassReport;
import org.arquillian.recorder.reporter.model.TestMethodReport;
import org.arquillian.recorder.reporter.model.TestSuiteReport;
import org.arquillian.recorder.reporter.model.entry.ScreenshotEntry;
import org.arquillian.recorder.reporter.model.entry.VideoEntry;

/**
 * Exports reports to HTML file according to XSLT transformation. Template can be set in configuration.
 *
 * In case you want to override core xsl template (of name arquillian_reporter_template.xsl bundled in api jar), you need to
 * bundle file arquillian_reporter_template_base.xsl to target implementation. That one will be picked then instead of core one.
 *
 * @see HTMLReport
 *
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class HTMLExporter implements Exporter {

    /**
     * Default template directory where all templates are saved. Template for some language is saved under another dir, like
     * template/en/arquillian_reporter_template.xsl for English templates.
     */
    private static final String DEFAULT_TEMPLATE_DIR = "arquillian_reporter_templates/";

    /**
     * Core template which will be used when user do not use his own in configuration and there is not any template in
     * implementation itself.
     */
    private static final String DEFAULT_XSL_TEMPLATE = "arquillian_reporter_template.xsl";

    /**
     * This is base template which will be used when implementators of screenshooter or video recorder base implementations
     * implements its own target screenshooter so implementator can override core template and not force user to specify his own
     * in configuration.
     */
    private static final String DEFAULT_BASE_XSL_TEMPLATE = "arquillian_reporter_template_base.xsl";

    private ReporterConfiguration configuration;

    private JAXBContext context;

    private TransformerFactory transformerFactory;

    public HTMLExporter(JAXBContext context) {
        this.context = context;
        this.transformerFactory = TransformerFactory.newInstance();
    }

    @Override
    public File export(Reportable report) throws Exception {

        StreamSource xslt;

        InputStream is;

        String baseTemplatePath = DEFAULT_TEMPLATE_DIR + configuration.getLanguage() + "/" + DEFAULT_BASE_XSL_TEMPLATE;
        String coreTemplatePath = DEFAULT_TEMPLATE_DIR + configuration.getLanguage() + "/" + DEFAULT_XSL_TEMPLATE;

        if (configuration.getTemplate().exists()) {
            xslt = new StreamSource(configuration.getTemplate());
        } else if ((is = getClass().getClassLoader().getResourceAsStream(baseTemplatePath)) != null) {
            xslt = new StreamSource(is);
        } else {
            is = getClass().getClassLoader().getResourceAsStream(coreTemplatePath);
            if (is == null) {
                throw new IllegalStateException("Unable to load default " + coreTemplatePath);
            } else {
                xslt = new StreamSource(is);
            }
        }

        normalizeFilePaths(report);

        ((Report) report).getReportConfiguration().setMaxImageWidth(configuration.getMaxImageWidth());
        ((Report) report).getReportConfiguration().setImageWidth(configuration.getImageWidth());
        ((Report) report).getReportConfiguration().setImageHeight(configuration.getImageHeight());
        ((Report) report).getReportConfiguration().setTitle(configuration.getTitle());

        JAXBSource source = new JAXBSource(context, report);
        StreamResult result = new StreamResult(configuration.getFile());

        Transformer transformer = transformerFactory.newTransformer(xslt);
        transformer.transform(source, result);

        return configuration.getFile();
    }

    @Override
    public Class<? extends ReportType> getReportType() {
        return HTMLReport.class;
    }

    @Override
    public void setConfiguration(ReporterConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setContext(JAXBContext context) {
        this.context = context;
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
}
