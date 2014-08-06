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
package org.arquillian.extension.recorder.video.impl;

import org.arquillian.extension.recorder.When;
import org.arquillian.extension.recorder.video.Video;
import org.arquillian.extension.recorder.video.VideoMetaData;
import org.arquillian.recorder.reporter.model.entry.VideoEntry;

/**
 * @author <a href="mailto:smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class VideoReportEntryBuilder {

    private VideoMetaData metadata;
    private Video video;
    private When when;

    public VideoReportEntryBuilder withMetadata(VideoMetaData metadata) {
        this.metadata = metadata;
        return this;
    }

    public VideoReportEntryBuilder withVideo(Video video) {
        this.video = video;
        return this;
    }

    public VideoReportEntryBuilder withWhen(When when) {
        this.when = when;
        return this;
    }

    public VideoEntry build() {

        if (metadata != null) {
            video.setResourceMetaData(metadata);
        }

        VideoEntry propertyEntry = new VideoEntry();
        propertyEntry.setPath(video.getResource().getAbsolutePath());
        propertyEntry.setPhase(when);
        propertyEntry.setType(video.getResourceType().toString());
        propertyEntry.setSize(Long.toString(video.getResource().length()));
        propertyEntry.setWidth(video.getWidth());
        propertyEntry.setHeight(video.getHeight());
        propertyEntry.setMessage(video.getMessage());

        return propertyEntry;
    }
}
