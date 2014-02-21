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

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.arquillian.recorder.reporter.PropertyEntry;
import org.arquillian.recorder.reporter.ReportEntry;

/**
 * Reports deployment being deployed to some container.<br>
 * <br>
 * Must hold:
 * <ul>
 * <li>deployment name</li>
 * </ul>
 * Can hold:
 * <ul>
 * <li>name of archive</li>
 * <li>protocol name</li>
 * <li>deployment order</li>
 * </ul>
 * Can not hold:
 * <ul>
 * <li>any {@link PropertyEntry}</li>
 * </ul>
 *
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
@XmlRootElement(name = "deployment")
@XmlType(propOrder = { "name", "archiveName", "protocol", "order" })
public class DeploymentReport implements ReportEntry {

    private String name;

    private String archiveName;

    private int order;

    private String protocol;

    private String target;

    public void setName(String name) {
        this.name = name;
    }

    public void setArchiveName(String archiveName) {
        this.archiveName = archiveName;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @XmlAttribute(required = true)
    public String getName() {
        return name;
    }

    @XmlElement(name = "archive")
    public String getArchiveName() {
        return archiveName;
    }

    @XmlElement
    public int getOrder() {
        return order;
    }

    @XmlElement
    public String getProtocol() {
        return protocol;
    }

    @XmlTransient
    public String getTarget() {
        return target;
    }

    @Override
    public List<PropertyEntry> getPropertyEntries() {
        throw new UnsupportedOperationException("It is not possible to add any properties to deployment report.");
    }

}
