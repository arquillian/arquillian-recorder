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
package org.arquillian.recorder.reporter.impl;

import java.util.ArrayList;
import java.util.List;

import org.arquillian.recorder.reporter.ReportType;

/**
 * Registers all supported report types.
 *
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class ReportTypeRegister {

    private final List<ReportType> reportTypes = new ArrayList<ReportType>();

    public ReportTypeRegister add(ReportType reportType) {
        reportTypes.add(reportType);
        return this;
    }

    public ReportType get(String report) {
        for (ReportType reportType : reportTypes) {
            for (String type : reportType.getTypes()) {
                if (type.equalsIgnoreCase(report)) {
                    return reportType;
                }
            }
        }
        return null;
    }

}
