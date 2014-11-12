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
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.arquillian.recorder.reporter.PropertyEntry;
import org.arquillian.recorder.reporter.ReportEntry;
import org.arquillian.recorder.reporter.model.entry.FileEntry;
import org.arquillian.recorder.reporter.model.entry.GroupEntry;
import org.arquillian.recorder.reporter.model.entry.KeyValueEntry;
import org.arquillian.recorder.reporter.model.entry.VideoEntry;
import org.arquillian.recorder.reporter.model.entry.table.TableEntry;

/**
 * Reports test class in some {@link TestSuiteReport}
 *
 * Must hold:
 * <ul>
 * <li>class name</li>
 * <li>at least one {@link TestMethodReport}
 * <li>
 * </ul>
 * Can hold:
 * <ul>
 * <li>runAsClient</li>
 * <li>multiple {@link KeyValueEntry}</li>
 * <li>multiple {@link FileEntry}</li>
 * <li>multiple {@link VideoEntry}</li>
 * <li>multiple {@link TableEntry}</li>
 * <li>multiple {@link GroupEntry}</li>
 * </ul>
 *
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
@XmlRootElement(name = "class")
@XmlType(propOrder = { "testClassName", "reportMessage", "runAsClient", "stop", "duration", "propertyEntries", "testMethodReports" })
public class TestClassReport implements ReportEntry {

    private String testClassName;

    private String reportMessage;

    private boolean runAsClient;

    @XmlTransient
    private Date start = new Date(System.currentTimeMillis());

    @XmlTransient
    private Date stop = start;

    @XmlAttribute(required = true)
    private long duration = 0;

    @XmlElement(name = "method", required = true)
    private final List<TestMethodReport> testMethodReports = new ArrayList<TestMethodReport>();

    @XmlElements({
        @XmlElement(name = "property", type = KeyValueEntry.class),
        @XmlElement(name = "file", type = FileEntry.class),
        @XmlElement(name = "video", type = VideoEntry.class),
        @XmlElement(name = "table", type = TableEntry.class),
        @XmlElement(name = "group", type = GroupEntry.class)
    })
    private final List<PropertyEntry> propertyEntries = new ArrayList<PropertyEntry>();

    public String getTestClassName() {
        return testClassName;
    }

    @XmlAttribute(name = "name", required = true)
    public void setTestClassName(String testClassName) {
        this.testClassName = testClassName;
    }

    public boolean getRunAsClient() {
        return runAsClient;
    }

    @XmlAttribute(name = "runAsClient", required = false)
    public void setRunAsClient(boolean runsAsClient) {
        this.runAsClient = runsAsClient;
    }

    public void setStop(Date timestamp) {
        stop = timestamp;
        setDuration(stop.getTime() - start.getTime());
    }

    public List<TestMethodReport> getTestMethodReports() {
        return testMethodReports;
    }

    @Override
    public List<PropertyEntry> getPropertyEntries() {
        return propertyEntries;
    }

    private void setDuration(long duration) {
        this.duration = duration;
    }

    public Date getStop() {
        return stop;
    }

    public Date getStart() {
        return start;
    }

    public long getDuration() {
        return this.duration;
    }

    @XmlAttribute
    public void setReportMessage(String reportMessage) {
        this.reportMessage = reportMessage;
    }

    public String getReportMessage() {
        return reportMessage;
    }

}
