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
import java.util.logging.Logger;

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

    private static final Logger logger = Logger.getLogger(ReportConfiguration.class.getName());

    private static final int MAX_HEIGHT_IN_PERCENT = 100;

    private String maxImageWidth;

    private String title;

    // in percents

    private String imageWidth = "100";

    private String imageHeight = "100";

    @XmlElement
    public String getMaxImageWidth() {
        return maxImageWidth;
    }

    /**
     * String has to be given as an integer number bigger than 0.
     *
     * @param maxImageWidth maximum image width for image to be displayed fully, otherwise it is displayed as a link
     */
    public void setMaxImageWidth(String maxImageWidth) {
        try {
            if (Integer.parseInt(maxImageWidth) > 0) {
                this.maxImageWidth = maxImageWidth;
            }
        } catch (NumberFormatException ex) {
            logger.info(String.format("You are trying to parse '%s' as a number for maxImageWidth.", maxImageWidth));
        }
    }

    @XmlElement
    public String getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(String imageWidth) {
        try {
            int parsedInt = Integer.parseInt(imageWidth);
            if (parsedInt > 0 && parsedInt <= MAX_HEIGHT_IN_PERCENT) {
                this.imageWidth = imageWidth;
            }
        } catch (NumberFormatException ex) {
            logger.info(String.format("You are trying to parse '%s' as a number for imageWidth.", imageWidth));
        }
    }

    @XmlElement
    public String getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(String imageHeight) {
        try {
            int parsedInt = Integer.parseInt(imageHeight);
            if (parsedInt > 0 && parsedInt <= MAX_HEIGHT_IN_PERCENT) {
                this.imageHeight = imageHeight;
            }
        } catch (NumberFormatException ex) {
            logger.info(String.format("You are trying to parse '%s' as a number for imageHeight.", imageHeight));
        }
    }

    @XmlElement
    public String getTitle() {
        return title;
    }

    /**
     * Title has to be non-null and non-empty string.
     *
     * @param title title of the whole Arquillian report
     */
    public void setTitle(String title) {
        if (title == null || title.isEmpty()) {
            return;
        }
        this.title = title;
    }

    @Override
    public List<PropertyEntry> getPropertyEntries() {
        throw new UnsupportedOperationException("It is not possible to add any property entries to reporter configuration.");
    }

}
