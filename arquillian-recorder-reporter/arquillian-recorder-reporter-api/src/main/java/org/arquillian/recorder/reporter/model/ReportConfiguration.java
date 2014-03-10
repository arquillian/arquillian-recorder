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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.arquillian.recorder.reporter.PropertyEntry;
import org.arquillian.recorder.reporter.ReportEntry;

/**
 * Contains all report specific configuration properties, mostly serving for marshalling purposes.
 *
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
@XmlRootElement(name = "reportConfiguration")
public class ReportConfiguration implements ReportEntry {

    private String maxImageWidth;

    @XmlElement
    public String getMaxImageWidth() {
        return maxImageWidth;
    }

    /**
     * String has to be given as an integer number bigger than 0.
     *
     * @param maxImageWidth
     */
    public void setMaxImageWidth(String maxImageWidth) {
        try {
            if (Integer.parseInt(maxImageWidth) > 0) {
                this.maxImageWidth = maxImageWidth;
            }
        } catch (NumberFormatException ex) {
            // intentionally empty
        }
    }

    @Override
    public List<PropertyEntry> getPropertyEntries() {
        throw new UnsupportedOperationException("It is not possible to add any property entries to reporter configuration.");
    }

}
