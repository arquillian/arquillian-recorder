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
package org.arquillian.recorder.reporter.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.arquillian.recorder.reporter.PropertyEntry;
import org.arquillian.recorder.reporter.ReportEntry;
import org.arquillian.recorder.reporter.model.entry.FileEntry;
import org.arquillian.recorder.reporter.model.entry.GroupEntry;
import org.arquillian.recorder.reporter.model.entry.KeyValueEntry;
import org.arquillian.recorder.reporter.model.entry.ScreenshotEntry;
import org.arquillian.recorder.reporter.model.entry.VideoEntry;
import org.arquillian.recorder.reporter.model.entry.table.TableEntry;
import org.jboss.arquillian.test.spi.TestResult.Status;

/**
 * Reports test method which belongs to some {@link TestClassReport}<br>
 * <br>
 * Must hold:
 * <ul>
 * <li>name</li>
 * <li>result status</li>
 * <li>runAsClient</li>
 * <li>operateOnDeployment</li>
 * </ul>
 * Can hold:
 * <ul>
 * <li>report message</li>
 * <li>exception message</li>
 * <li>duration</li>
 * <li>list of {@link KeyValueEntry}</li>
 * <li>list of {@link FileEntry}</li>
 * <li>list of {@link VideoEntry}</li>
 * <li>list of {@link ScreenshotEntry}</li>
 * <li>list of {@link TableEntry}</li>
 * <li>list of {@link GroupEntry}</li>
 * </ul>
 *
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
@XmlRootElement(name = "method")
@XmlType(propOrder = { "name", "status", "reportMessage", "duration", "operateOnDeployment", "runAsClient", "exception", "propertyEntries" })
public class TestMethodReport implements ReportEntry {

    private String name;

    private Status status;

    private long duration = 0;

    private String exception;

    private String operateOnDeployment;

    private boolean runAsClient;

    private String reportMessage;

    @XmlElements({
        @XmlElement(name = "property", type = KeyValueEntry.class),
        @XmlElement(name = "file", type = FileEntry.class),
        @XmlElement(name = "video", type = VideoEntry.class),
        @XmlElement(name = "screenshot", type = ScreenshotEntry.class),
        @XmlElement(name = "table", type = TableEntry.class),
        @XmlElement(name = "group", type = GroupEntry.class)
    })
    private final List<PropertyEntry> propertyEntries = new ArrayList<PropertyEntry>();

    public String getName() {
        return name;
    }

    @XmlAttribute(required = true)
    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    @XmlAttribute(name = "result", required = true)
    public void setStatus(Status status) {
        this.status = status;
    }

    public long getDuration() {
        return duration;
    }

    @XmlAttribute(required = false)
    public void setDuration(long duration) {
        if (duration > 0) {
            this.duration = duration;
        }
    }

    @XmlElement(required = false)
    public void setException(String exception) {
        this.exception = exception;
    }

    public String getException() {
        return exception;
    }

    @XmlAttribute(required = true)
    public void setOperateOnDeployment(String operateOnDeployment) {
        this.operateOnDeployment = operateOnDeployment;
    }

    public String getOperateOnDeployment() {
        return operateOnDeployment;
    }

    @Override
    public List<PropertyEntry> getPropertyEntries() {
        return propertyEntries;
    }

    @XmlAttribute(required = true)
    public void setRunAsClient(boolean runAsClient) {
        this.runAsClient = runAsClient;
    }

    public boolean getRunAsClient() {
        return runAsClient;
    }

    public String getReportMessage() {
        return reportMessage;
    }

    @XmlAttribute
    public void setReportMessage(String reportMessage) {
        this.reportMessage = reportMessage;
    }

}
