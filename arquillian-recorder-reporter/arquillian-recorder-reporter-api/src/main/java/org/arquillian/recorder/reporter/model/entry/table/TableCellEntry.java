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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.arquillian.recorder.reporter.Reportable;

/**
 *
 * @author <a href="mailto:smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
@XmlRootElement(name = "cell")
@XmlType(propOrder = { "colspan", "rowspan", "content" })
public class TableCellEntry implements Reportable {

    private int colspan = 1;

    private int rowspan = 1;

    private String content = "";

    public TableCellEntry() {
    }

    public TableCellEntry(String content) {
        setContent(content);
    }

    public TableCellEntry(String content, int colspan, int rowspan) {
        setContent(content);
        setColspan(colspan);
        setRowspan(rowspan);
    }

    @XmlAttribute(name = "colspan")
    public int getColspan() {
        return colspan;
    }

    public void setColspan(int colspan) {
        if (colspan >= 1) {
            this.colspan = colspan;
        }
    }

    @XmlAttribute(name = "rowspan")
    public int getRowspan() {
        return rowspan;
    }

    public void setRowspan(int rowspan) {
        if (rowspan >= 1) {
            this.rowspan = rowspan;
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        if (content != null) {
            this.content = content;
        }
    }

}
