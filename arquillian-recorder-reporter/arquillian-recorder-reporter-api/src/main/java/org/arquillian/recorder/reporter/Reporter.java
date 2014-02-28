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

import org.arquillian.extension.recorder.Configuration;
import org.arquillian.recorder.reporter.model.ContainerReport;
import org.arquillian.recorder.reporter.model.ExtensionReport;
import org.arquillian.recorder.reporter.model.Report;
import org.arquillian.recorder.reporter.model.TestClassReport;
import org.arquillian.recorder.reporter.model.TestMethodReport;
import org.arquillian.recorder.reporter.model.TestSuiteReport;

/**
 * Collects data from a test run to arbitrary structure.
 *
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public interface Reporter {

    /**
     * @return collected report
     */
    Report getReport();

    void setConfiguration(Configuration<?> configuration);

    void setTestSuiteReport(TestSuiteReport testSuiteReport);

    void setTestClassReport(TestClassReport testClassReport);

    void setTestMethodReport(TestMethodReport testMethodReport);

    void setContainerReport(ContainerReport containerReport);

    void setExtensionReport(ExtensionReport extensionReport);

    void setReporterCursor(ReporterCursor reporterCursor);

    TestSuiteReport getLastTestSuiteReport();

    TestClassReport getLastTestClassReport();

    TestMethodReport getLastTestMethodReport();

    ContainerReport getLastContainerReport();

    ExtensionReport getLastExtensionReport();

    /**
     * During test run, we can fire property from 3rd party extensions. That event is fired in some context (e.g. BeforeSuite,
     * BeforeClass and so on). In order to put this property into right reporter tree section, there is a cursor which holds the
     * latest context. <br>
     * <br>
     * As test proceeds, cursor moves with it, still holding the latest entry where properties would be hooked.
     *
     * @return cursor for current context.
     */
    ReporterCursor getReporterCursor();

}
