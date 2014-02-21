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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.arquillian.recorder.reporter.PropertyEntry;
import org.arquillian.recorder.reporter.ReportEntry;

/**
 * Reports used container. <br>
 * <br>
 * Must hold:
 * <ul>
 * <li>container qualifier as a String</li>
 * </ul>
 * Can hold:
 * <ul>
 * <li>container configuration as a String</li>
 * <li>list of {@link DeploymentReport}
 * </ul>
 * Can not hold:
 * <ul>
 * <li>any {@link PropertyEntry}</li>
 * </ul>
 *
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
@XmlRootElement(name = "container")
@XmlType(propOrder = { "configuration", "deploymentReports" })
public class ContainerReport implements ReportEntry {

    private String qualifier;

    private Map<String, String> configuration = new HashMap<String, String>();

    private List<DeploymentReport> deploymentReports = new ArrayList<DeploymentReport>();

    @XmlAttribute(required = true)
    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public String getQualifier() {
        return qualifier;
    }

    @XmlElement(name = "deployment")
    public void setDeploymentReports(List<DeploymentReport> deploymentReports) {
        this.deploymentReports = deploymentReports;
    }

    public List<DeploymentReport> getDeploymentReports() {
        return deploymentReports;
    }

    @XmlElement
    @XmlElementWrapper(name = "configuration")
    public void setConfiguration(Map<String, String> configuration) {
        this.configuration = configuration;
    }

    public Map<String, String> getConfiguration() {
        return configuration;
    }

    @Override
    public List<PropertyEntry> getPropertyEntries() {
        throw new UnsupportedOperationException("It is not possible to add any properties to container report.");
    }

}
