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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.arquillian.recorder.reporter.ReportFrequency;
import org.arquillian.recorder.reporter.ReportMessage;
import org.arquillian.recorder.reporter.Reporter;
import org.arquillian.recorder.reporter.ReporterConfiguration;
import org.arquillian.recorder.reporter.ReporterCursor;
import org.arquillian.recorder.reporter.event.ExportReport;
import org.arquillian.recorder.reporter.event.InTestResourceReport;
import org.arquillian.recorder.reporter.event.PropertyReportEvent;
import org.arquillian.recorder.reporter.model.ContainerReport;
import org.arquillian.recorder.reporter.model.DeploymentReport;
import org.arquillian.recorder.reporter.model.ExtensionReport;
import org.arquillian.recorder.reporter.model.TestClassReport;
import org.arquillian.recorder.reporter.model.TestMethodReport;
import org.arquillian.recorder.reporter.model.TestSuiteReport;
import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.config.descriptor.api.ExtensionDef;
import org.jboss.arquillian.container.spi.Container;
import org.jboss.arquillian.container.spi.client.deployment.DeploymentDescription;
import org.jboss.arquillian.container.spi.event.container.BeforeDeploy;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.TestResult;
import org.jboss.arquillian.test.spi.TestResult.Status;
import org.jboss.arquillian.test.spi.event.suite.After;
import org.jboss.arquillian.test.spi.event.suite.AfterClass;
import org.jboss.arquillian.test.spi.event.suite.AfterSuite;
import org.jboss.arquillian.test.spi.event.suite.AfterTestLifecycleEvent;
import org.jboss.arquillian.test.spi.event.suite.BeforeClass;
import org.jboss.arquillian.test.spi.event.suite.BeforeSuite;
import org.jboss.arquillian.test.spi.event.suite.BeforeTestLifecycleEvent;

/**
 * Observes events from Arquillian and delegates them to {@link Reporter} implementation.<br>
 * <br>
 * Fires:
 * <ul>
 * <li>{@link ExportReport} on observing {@link AfterSuite}</li>
 * </ul>
 *
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class ReporterLifecycleObserver {

    private static final Map<Method, Integer> lifecycleCountRegister = new HashMap<Method, Integer>();

    @Inject
    private Instance<Reporter> reporter;

    @Inject
    private Instance<ReporterConfiguration> configuration;

    @Inject
    private Instance<ArquillianDescriptor> descriptor;

    @Inject
    private Event<ExportReport> exportReportEvent;

    @Inject
    private Event<InTestResourceReport> inTestResourceReportEvent;

    public void observeBeforeSuite(@Observes(precedence = Integer.MAX_VALUE) BeforeSuite event) {
        TestSuiteReport testSuiteReport = new TestSuiteReport();

        reporter.get().getReport().getTestSuiteReports().add(testSuiteReport);
        reporter.get().setTestSuiteReport(testSuiteReport);
    }

    public void observeBeforeStart(@Observes Container event) {
        ContainerReport containerReport = new ContainerReport();

        containerReport.setQualifier(event.getName());
        containerReport.setConfiguration(event.getContainerConfiguration().getContainerProperties());

        reporter.get().getLastTestSuiteReport().getContainerReports().add(containerReport);
        reporter.get().setContainerReport(containerReport);

    }

    public void observeBeforeDeploy(@Observes BeforeDeploy event) {
        DeploymentReport deploymentReport = new DeploymentReport();

        DeploymentDescription description = event.getDeployment();

        deploymentReport.setArchiveName(description.getArchive().getName());
        deploymentReport.setName(description.getName());

        int order = description.getOrder();
        if (order > 0) {
            deploymentReport.setOrder(order);
        }

        String protocol = description.getProtocol().getName();
        if (!protocol.equals("_DEFAULT_")) {
            deploymentReport.setProtocol(protocol);
        } else {
            deploymentReport.setProtocol("_DEFAULT_");
        }

        deploymentReport.setTarget(description.getTarget().getName());

        boolean reported = false;

        for (ContainerReport containerReport : reporter.get().getLastTestSuiteReport().getContainerReports()) {
            if (containerReport.getQualifier().equals(deploymentReport.getTarget())) {
                containerReport.getDeploymentReports().add(deploymentReport);
                reported = true;
                break;
            }
        }

        if (!reported) {
            if (reporter.get().getLastTestSuiteReport().getContainerReports().size() == 1) {
                reporter.get().getLastTestSuiteReport().getContainerReports().get(0).getDeploymentReports().add(deploymentReport);
            }
        }

    }

    public void observeBeforeClass(@Observes(precedence = Integer.MAX_VALUE) BeforeClass event) {
        TestClassReport testClassReport = new TestClassReport();
        testClassReport.setTestClassName(event.getTestClass().getName());
        testClassReport.setRunAsClient(event.getTestClass().isAnnotationPresent(RunAsClient.class));
        testClassReport.setReportMessage(ReportMessageParser.parseTestClassReportMessage(event.getTestClass().getJavaClass()));

        reporter.get().getLastTestSuiteReport().getTestClassReports().add(testClassReport);
        reporter.get().setTestClassReport(testClassReport);
    }

    public void observeBeforeTest(@Observes(precedence = Integer.MAX_VALUE) BeforeTestLifecycleEvent event) {

        Integer c = lifecycleCountRegister.get(event.getTestMethod());
        int count = (c != null ? c.intValue() : 0);

        if (count == 0) {
            TestMethodReport testMethodReport = new TestMethodReport();
            testMethodReport.setName(event.getTestMethod().getName());

            if (event.getTestMethod().isAnnotationPresent(OperateOnDeployment.class)) {
                OperateOnDeployment ood = event.getTestMethod().getAnnotation(OperateOnDeployment.class);
                testMethodReport.setOperateOnDeployment(ood.value());
            } else {
                testMethodReport.setOperateOnDeployment("_DEFAULT_");
            }

            testMethodReport.setRunAsClient(event.getTestMethod().isAnnotationPresent(RunAsClient.class));

            reporter.get().getLastTestClassReport().getTestMethodReports().add(testMethodReport);
            reporter.get().setTestMethodReport(testMethodReport);
        }

        lifecycleCountRegister.put(event.getTestMethod(), ++count);
    }

    public void observeAfterTest(@Observes(precedence = Integer.MIN_VALUE) AfterTestLifecycleEvent event, TestResult result) {

        int count = lifecycleCountRegister.get(event.getTestMethod());

        lifecycleCountRegister.put(event.getTestMethod(), --count);

        if (lifecycleCountRegister.get(event.getTestMethod()) == 0) {
            TestMethodReport testMethodReport = reporter.get().getLastTestMethodReport();

            testMethodReport.setStatus(result.getStatus());
            testMethodReport.setDuration(result.getEnd() - result.getStart());
            testMethodReport.setReportMessage(ReportMessageParser.parseTestReportMessage(event.getTestMethod()));

            if (result.getStatus() == Status.FAILED && result.getThrowable() != null) {
                testMethodReport.setException(getStackTrace(result.getThrowable()));
            }

            inTestResourceReportEvent.fire(new InTestResourceReport());

            reporter.get().setReporterCursor(new ReporterCursor(reporter.get().getLastTestClassReport()));

            report(event, descriptor.get());

            lifecycleCountRegister.remove(event.getTestMethod());
        }
    }

    public void observeAfterClass(@Observes(precedence = Integer.MIN_VALUE) AfterClass event) {

        reporter.get().setReporterCursor(new ReporterCursor(reporter.get().getLastTestSuiteReport()));

        report(event, descriptor.get());
    }

    public void observeAfterSuite(@Observes(precedence = Integer.MIN_VALUE) AfterSuite event) {

        reporter.get().getLastTestClassReport().setStop(new Date(System.currentTimeMillis()));
        reporter.get().getLastTestSuiteReport().setStop(new Date(System.currentTimeMillis()));

        exportReportEvent.fire(new ExportReport(reporter.get().getReport()));
    }

    public void observeReportEvent(@Observes PropertyReportEvent event) {
        reporter.get().getReporterCursor().getCursor().getPropertyEntries().add(event.getPropertyEntry());
    }

    private void report(org.jboss.arquillian.core.spi.event.Event event, ArquillianDescriptor descriptor) {
        if (shouldReport(event, configuration.get().getReportAfterEvery())) {
            List<ExtensionReport> extensionReports = reporter.get().getReport().getExtensionReports();
            if (extensionReports.isEmpty()) {
                extensionReports.addAll(getExtensionReports(descriptor));
            }

            reporter.get().getLastTestClassReport().setStop(new Date(System.currentTimeMillis()));
            reporter.get().getLastTestSuiteReport().setStop(new Date(System.currentTimeMillis()));

            exportReportEvent.fire(new ExportReport(reporter.get().getReport()));
        }
    }

    private boolean shouldReport(org.jboss.arquillian.core.spi.event.Event event, String frequency) {
        if (event instanceof AfterClass && ReportFrequency.CLASS.toString().equals(frequency)) {
            return true;
        } else if (event instanceof After && ReportFrequency.METHOD.toString().equals(frequency)) {
            return true;
        }
        return false;
    }

    private Collection<? extends ExtensionReport> getExtensionReports(ArquillianDescriptor descriptor) {
        List<ExtensionReport> extensionReports = new ArrayList<ExtensionReport>();

        for (ExtensionDef extensionDef : descriptor.getExtensions()) {
            ExtensionReport extensionReport = new ExtensionReport();
            extensionReport.setQualifier(extensionDef.getExtensionName());
            extensionReport.setConfiguration(extensionDef.getExtensionProperties());
            extensionReports.add(extensionReport);
        }
        return extensionReports;
    }

    private String getStackTrace(Throwable aThrowable) {
        StringBuilder sb = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        sb.append(aThrowable.toString());
        sb.append(newLine);

        for (StackTraceElement element : aThrowable.getStackTrace()) {
            sb.append(element);
            sb.append(newLine);
        }
        return sb.toString();
    }

    private static final class ReportMessageParser {

        public static String parseTestReportMessage(Method testMethod) {
            return getReportMessage(testMethod.getAnnotations());
        }

        public static String parseTestClassReportMessage(Class<?> testClass) {
            return getReportMessage(testClass.getAnnotations());
        }

        private static String getReportMessage(Annotation[] annotations) {
            if (annotations != null) {
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType().isAssignableFrom(ReportMessage.class)) {
                        return ((ReportMessage) annotation).value();
                    }
                }
            }

            return null;
        }
    }

}
