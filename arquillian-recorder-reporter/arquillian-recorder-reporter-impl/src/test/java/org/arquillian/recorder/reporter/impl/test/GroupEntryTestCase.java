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
package org.arquillian.recorder.reporter.impl.test;

import java.util.HashMap;
import java.util.Map;

import org.arquillian.recorder.reporter.Exporter;
import org.arquillian.recorder.reporter.JAXBContextFactory;
import org.arquillian.recorder.reporter.Reporter;
import org.arquillian.recorder.reporter.ReporterConfiguration;
import org.arquillian.recorder.reporter.exporter.impl.HTMLExporter;
import org.arquillian.recorder.reporter.exporter.impl.XMLExporter;
import org.arquillian.recorder.reporter.impl.ReporterImpl;
import org.arquillian.recorder.reporter.model.Report;
import org.arquillian.recorder.reporter.model.entry.GroupEntry;
import org.arquillian.recorder.reporter.model.entry.KeyValueEntry;
import org.arquillian.recorder.reporter.model.entry.table.TableCellEntry;
import org.arquillian.recorder.reporter.model.entry.table.TableEntry;
import org.arquillian.recorder.reporter.model.entry.table.TableRowEntry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author <a href="mailto:smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
@RunWith(JUnit4.class)
public class GroupEntryTestCase {

    @Test
    public void testGroups() throws Exception {
        ReporterConfiguration htmlConfiguration = getHtmlConfig();
        ReporterConfiguration xmlConfiguration = getXmlConfig();

        Reporter reporter = new ReporterImpl();

        GroupEntry group1 = new GroupEntry("group1");
        GroupEntry group2 = new GroupEntry("group2");

        group1.getPropertyEntries().add(new KeyValueEntry("key1", "value1"));
        group1.getPropertyEntries().add(new KeyValueEntry("key2", "value2"));

        TableEntry table = generateTable();
        table.setTableName("some table name");

        group1.getPropertyEntries().add(table);

        group2.getPropertyEntries().add(new KeyValueEntry("key3", "value3"));

        group1.getPropertyEntries().add(group2);

        reporter.getReport().getPropertyEntries().add(group1);
        reporter.getReport().getPropertyEntries().add(new KeyValueEntry("topKey1", "topValue1"));
        reporter.getReport().getPropertyEntries().add(new KeyValueEntry("topKey2", "topValue2"));

        Exporter exporter1 = new XMLExporter(JAXBContextFactory.initContext(Report.class));
        Exporter exporter2 = new HTMLExporter(JAXBContextFactory.initContext(Report.class));
        exporter1.setConfiguration(xmlConfiguration);
        exporter2.setConfiguration(htmlConfiguration);

        exporter1.export(reporter.getReport());
        exporter2.export(reporter.getReport());
    }

    private TableEntry generateTable() {
        TableEntry tableEntry = new TableEntry();

        tableEntry.getTableHead().getRow().addCells(new TableCellEntry("header1"), new TableCellEntry("header2"));

        tableEntry.getTableBody().addRows(generateTableRow(), generateTableRow());

        tableEntry.getTableFoot().getRow().addCells(new TableCellEntry("foot1"), new TableCellEntry("foot2"));

        return tableEntry;
    }

    private TableRowEntry generateTableRow() {
        TableRowEntry row = new TableRowEntry();

        row.addCells(new TableCellEntry("cell1"), new TableCellEntry("cell2"));

        return row;
    }

    private ReporterConfiguration getHtmlConfig() {

        ReporterConfiguration configuration = new ReporterConfiguration();

        Map<String, String> configMap = new HashMap<String, String>();
        configMap.put("report", "html");
        configMap.put("file", "group_report");
        configuration.setConfiguration(configMap);

        configuration.validate();
        System.out.println(configuration.toString());

        return configuration;
    }

    private ReporterConfiguration getXmlConfig() {

        ReporterConfiguration configuration = new ReporterConfiguration();

        Map<String, String> configMap = new HashMap<String, String>();
        configMap.put("report", "xml");
        configMap.put("file", "group_report");
        configuration.setConfiguration(configMap);

        configuration.validate();
        System.out.println(configuration.toString());

        return configuration;
    }
}
