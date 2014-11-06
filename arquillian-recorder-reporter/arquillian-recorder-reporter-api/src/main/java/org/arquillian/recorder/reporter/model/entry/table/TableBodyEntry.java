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
package org.arquillian.recorder.reporter.model.entry.table;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.arquillian.recorder.reporter.Reportable;

/**
 * @author <a href="mailto:smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
@XmlRootElement(name = "tbody")
public class TableBodyEntry implements Reportable {

    private final List<TableRowEntry> rows = new ArrayList<TableRowEntry>();

    @XmlElement(name = "row", required = true)
    public List<TableRowEntry> getRows() {
        return rows;
    }

    @XmlTransient
    public void addRow(TableRowEntry row) {
        if (row != null) {
            rows.add(row);
        }
    }

    @XmlTransient
    public void addRows(TableRowEntry row, TableRowEntry... rows) {
        addRow(row);
        if (rows != null) {
            for (TableRowEntry r : rows) {
                addRow(r);
            }
        }
    }
}
