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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.arquillian.recorder.reporter.PropertyEntry;

/**
 * Entry which models table as a property, with header, rows and cells.
 *
 * @author <a href="mailto:smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
@XmlRootElement(name = "table")
@XmlType(propOrder = { "header", "numberOfCells", "rows" })
public class TableEntry extends PropertyEntry {

    private String header;

    private String numberOfCells;

    private final List<TableRowEntry> rows = new ArrayList<TableRowEntry>();

    @XmlElement(name = "row", required = true)
    public List<TableRowEntry> getRows() {
        return rows;
    }

    public String getHeader() {
        return header;
    }

    @XmlAttribute
    public void setHeader(String header) {
        this.header = header;
    }

    @XmlAttribute
    public int getNumberOfCells() {
        int count = 0;

        for (TableRowEntry row : rows) {
            int countTemp = row.getCells().size();
            if (countTemp > 0) {
                count = countTemp;
            }
        }

        return count;
    }

}
