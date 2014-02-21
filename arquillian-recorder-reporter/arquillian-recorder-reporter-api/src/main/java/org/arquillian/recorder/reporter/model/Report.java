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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import org.arquillian.recorder.reporter.PropertyEntry;
import org.arquillian.recorder.reporter.ReportEntry;
import org.arquillian.recorder.reporter.model.entry.FileEntry;
import org.arquillian.recorder.reporter.model.entry.KeyValueEntry;

/**
 * Root of the reporting structure. <br>
 * <br>
 * Must hold:
 * <ul>
 * <li>at least one {@link TestSuiteReport}</li>
 * </ul>
 * Can hold:
 * <ul>
 * <li>list of {@link KeyValueEntry}</li>
 * <li>list of {@link FileEntry}</li>
 * </ul>
 *
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
@XmlRootElement(name = "report")
@XmlType(propOrder = { "propertyEntries", "testSuiteReports" })
public class Report implements ReportEntry {

    @XmlAttribute(required = true)
    @XmlSchemaType(name = "date")
    private long start = System.currentTimeMillis();

    @XmlAttribute(required = true)
    @XmlSchemaType(name = "date")
    private long stop = start;

    @XmlAttribute(required = true)
    @XmlSchemaType(name = "time")
    private long duration = 0;

    @XmlElement(name = "suite", required = true)
    private final List<TestSuiteReport> testSuiteReports = new ArrayList<TestSuiteReport>();

    @XmlElements({
        @XmlElement(name = "property", type = KeyValueEntry.class),
        @XmlElement(name = "file", type = FileEntry.class)
    })
    private final List<PropertyEntry> propertyEntries = new ArrayList<PropertyEntry>();

    public List<TestSuiteReport> getTestSuiteReports() {
        return testSuiteReports;
    }

    public void setStop(long timestamp) {
        stop = timestamp;
        setDuration();
    }

    private void setDuration() {
        duration = stop - start;
    }

    @Override
    public List<PropertyEntry> getPropertyEntries() {
        return propertyEntries;
    }

}
