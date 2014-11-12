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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import org.arquillian.recorder.reporter.PropertyEntry;
import org.arquillian.recorder.reporter.model.entry.table.TableEntry;

/**
 * Represents group entry which can hold:<br>
 * <br>
 * <ul>
 * <li>{@link KeyValueEntry}</li>
 * <li>{@link TableEntry}</li>
 * <li>{@link GroupEntry}</li>
 * </ul>
 *
 * By providing the possibility to put {@link GroupEntry} into properties, we are introducing recursive reporting.
 *
 * @author <a href="mailto:smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
@XmlRootElement(name = "groupEntry")
public class GroupEntry extends PropertyEntry {

    private String name;

    @XmlElements({
        @XmlElement(name = "property", type = KeyValueEntry.class),
        @XmlElement(name = "table", type = TableEntry.class),
        @XmlElement(name = "group", type = GroupEntry.class)
    })
    private final List<PropertyEntry> propertyEntries = new ArrayList<PropertyEntry>();

    public GroupEntry() {
    }

    public GroupEntry(String name) {
        setName(name);
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<PropertyEntry> getPropertyEntries() {
        return propertyEntries;
    }
}