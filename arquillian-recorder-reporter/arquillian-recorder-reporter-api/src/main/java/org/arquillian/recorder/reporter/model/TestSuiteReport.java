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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.arquillian.recorder.reporter.PropertyEntry;
import org.arquillian.recorder.reporter.ReportEntry;
import org.arquillian.recorder.reporter.model.entry.FileEntry;
import org.arquillian.recorder.reporter.model.entry.GroupEntry;
import org.arquillian.recorder.reporter.model.entry.KeyValueEntry;
import org.arquillian.recorder.reporter.model.entry.VideoEntry;
import org.arquillian.recorder.reporter.model.entry.table.TableEntry;

/**
 * Reports test suite for some {@link Report} <br>
 * <br> Must hold:
 * <ul>
 * <li>at least one {@link ContainerReport}</li>
 * <li>at least one {@link TestClassReport}</li>
 * </ul>
 * Can hold:
 * <ul>
 * <li>list of {@link KeyValueEntry}</li>
 * <li>list of {@link FileEntry}</li>
 * <li>list of {@link VideoEntry}</li>
 * <li>list of {@link TableEntry}</li>
 * <li>list of {@link GroupEntry}</li>
 * </ul>
 *
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
@XmlRootElement(name = "suite")
@XmlType(propOrder = { "propertyEntries", "containerReports", "testClassReports" })
public class TestSuiteReport implements ReportEntry {

    @XmlAttribute(required = true)
    @XmlSchemaType(name = "dateTime")
    private Date start = new Date(System.currentTimeMillis());

    @XmlAttribute(required = true)
    @XmlSchemaType(name = "dateTime")
    private Date stop = start;

    @XmlAttribute(required = true)
    @XmlSchemaType(name = "time")
    private long duration = 0;

    @XmlElement(name = "container", required = true)
    private final List<ContainerReport> containerReports = new ArrayList<ContainerReport>();

    @XmlElement(name = "class", required = true)
    private final List<TestClassReport> testClassReports = new ArrayList<TestClassReport>();

    @XmlElements({
        @XmlElement(name = "property", type = KeyValueEntry.class),
        @XmlElement(name = "file", type = FileEntry.class),
        @XmlElement(name = "video", type = VideoEntry.class),
        @XmlElement(name = "table", type = TableEntry.class),
        @XmlElement(name = "group", type = GroupEntry.class)
    })
    private final List<PropertyEntry> propertyEntries = new ArrayList<PropertyEntry>();

    public Date getStart() {
        return start;
    }

    public Date getStop() {
        return stop;
    }

    public long getDuration() {
        return duration;
    }

    public List<ContainerReport> getContainerReports() {
        return containerReports;
    }

    public List<TestClassReport> getTestClassReports() {
        return testClassReports;
    }

    @Override
    public List<PropertyEntry> getPropertyEntries() {
        return propertyEntries;
    }

    public void setStop(Date timestamp) {
        stop = timestamp;
        setDuration(stop.getTime() - start.getTime());
    }

    private void setDuration(long duration) {
        this.duration = duration;
    }

}
