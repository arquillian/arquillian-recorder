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
@XmlType(propOrder = { "tableName", "tableHead", "tableBody", "tableFoot" })
public class TableEntry extends PropertyEntry {

    private final TableHeadEntry tableHead = new TableHeadEntry();

    private final TableBodyEntry tableBody = new TableBodyEntry();

    private final TableFootEntry tableFoot = new TableFootEntry();

    private String tableName;

    @XmlElement(name = "tbody", required = true)
    public TableBodyEntry getTableBody() {
        return tableBody;
    }

    @XmlElement(name = "thead")
    public TableHeadEntry getTableHead() {
        return tableHead;
    }

    @XmlElement(name = "tfoot")
    public TableFootEntry getTableFoot() {
        return tableFoot;
    }

    @XmlAttribute(name = "tableName")
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getNumberOfColumns() {

        int n = 0;

        for (TableRowEntry row : tableBody.getRows()) {
            int i = row.getTotalColspan();
            if (i > n) {
                n = i;
            }
        }

        return n;
    }

    public int getNumberOfRows() {
        return tableBody.getRows().size();
    }

}
