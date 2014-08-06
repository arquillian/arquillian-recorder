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
package org.arquillian.recorder.reporter.model.entry;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.arquillian.recorder.reporter.PropertyEntry;

/**
 * Represents a report of some file resource which could be created as a result of a testing process.<br>
 * <br>
 * Must hold:
 * <ul>
 * <li>path</li>
 * <li>type</li>
 * </ul>
 * Can hold:
 * <ul>
 * <li>size</li>
 * <li>message</li>
 * </ul>
 *
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
@XmlRootElement(name = "file")
@XmlType(propOrder = { "path", "size", "type", "message" })
public class FileEntry extends PropertyEntry {

    private String path;

    private String size;

    private String type;

    private String message;

    public String getPath() {
        return path;
    }

    @XmlAttribute(required = true)
    public void setPath(String path) {
        this.path = path;
    }

    public String getSize() {
        return size;
    }

    @XmlAttribute
    public void setSize(String size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    @XmlAttribute(required = true)
    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    @XmlAttribute
    public void setMessage(String message) {
        this.message = message;
    }
}
