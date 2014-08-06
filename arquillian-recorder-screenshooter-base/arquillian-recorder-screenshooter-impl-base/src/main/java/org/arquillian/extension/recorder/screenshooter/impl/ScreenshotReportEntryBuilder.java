/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
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
package org.arquillian.extension.recorder.screenshooter.impl;

import org.arquillian.extension.recorder.When;
import org.arquillian.extension.recorder.screenshooter.Screenshot;
import org.arquillian.extension.recorder.screenshooter.ScreenshotMetaData;
import org.arquillian.recorder.reporter.model.entry.ScreenshotEntry;

/**
 *
 * @author <a href="mailto:smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class ScreenshotReportEntryBuilder {

    private ScreenshotMetaData metadata;

    private Screenshot screenshot;

    private When when;

    public ScreenshotReportEntryBuilder withMetadata(ScreenshotMetaData metadata) {
        this.metadata = metadata;
        return this;
    }

    public ScreenshotReportEntryBuilder withScreenshot(Screenshot screenshot) {
        this.screenshot = screenshot;
        return this;
    }

    public ScreenshotReportEntryBuilder withWhen(When when) {
        this.when = when;
        return this;
    }

    public ScreenshotEntry build() {

        if (metadata != null) {
            screenshot.setResourceMetaData(metadata);
        }

        ScreenshotEntry propertyEntry = new ScreenshotEntry();
        propertyEntry.setPath(screenshot.getResource().getAbsolutePath());
        propertyEntry.setPhase(when);
        propertyEntry.setType(screenshot.getResourceType().toString());
        propertyEntry.setSize(Long.toString(screenshot.getResource().length()));
        propertyEntry.setWidth(screenshot.getWidth());
        propertyEntry.setHeight(screenshot.getHeight());
        propertyEntry.setMessage(screenshot.getMessage());

        return propertyEntry;
    }
}
