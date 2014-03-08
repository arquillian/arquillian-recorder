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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.arquillian.recorder.reporter.PropertyEntry;
import org.arquillian.recorder.reporter.ReportEntry;

/**
 * Reports extensions in arquillian.xml. It does not mean that these extensions are on classpath during of a test.<br>
 * <br>
 * Must hold:
 * <ul>
 * <li>extension qualifier as a String</li>
 * </ul>
 * Can hold:
 * <ul>
 * <li>extension configuration</li>
 * </ul>
 * Can not hold:
 * <ul>
 * <li>any {@link PropertyEntry}</li>
 * </ul>
 *
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
@XmlRootElement(name = "extension")
public class ExtensionReport implements ReportEntry {

    private String qualifier;

    private Map<String, String> configuration = new HashMap<String, String>();

    public String getQualifier() {
        return qualifier;
    }

    @XmlAttribute(required = true)
    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public Map<String, String> getConfiguration() {
        return configuration;
    }

    @XmlElement
    @XmlElementWrapper(name = "configuration")
    public void setConfiguration(Map<String, String> configuration) {
        this.configuration = configuration;
    }

    @Override
    public List<PropertyEntry> getPropertyEntries() {
        throw new UnsupportedOperationException("It is not possible to add any properties to extension report.");
    }
}
