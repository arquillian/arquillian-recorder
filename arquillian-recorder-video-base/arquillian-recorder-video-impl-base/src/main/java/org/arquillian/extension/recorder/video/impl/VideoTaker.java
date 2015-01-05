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

import java.io.File;
import java.io.IOException;

import org.arquillian.extension.recorder.DefaultFileNameBuilder;
import org.arquillian.extension.recorder.video.Recorder;
import org.arquillian.extension.recorder.video.Video;
import org.arquillian.extension.recorder.video.VideoConfiguration;
import org.arquillian.extension.recorder.video.VideoMetaData;
import org.arquillian.extension.recorder.video.event.StartRecordClassVideo;
import org.arquillian.extension.recorder.video.event.StartRecordSuiteVideo;
import org.arquillian.extension.recorder.video.event.StartRecordVideo;
import org.arquillian.extension.recorder.video.event.StopRecordClassVideo;
import org.arquillian.extension.recorder.video.event.StopRecordSuiteVideo;
import org.arquillian.extension.recorder.video.event.StopRecordVideo;
import org.arquillian.recorder.reporter.PropertyEntry;
import org.arquillian.recorder.reporter.event.PropertyReportEvent;
import org.arquillian.recorder.reporter.impl.TakenResourceRegister;
import org.arquillian.recorder.reporter.model.entry.VideoEntry;
import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.TestResult;
import org.jboss.arquillian.test.spi.TestResult.Status;

/**
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class VideoTaker {

    @Inject
    private Instance<Recorder> recorder;

    @Inject
    private Instance<VideoConfiguration> configuration;

    @Inject
    private Event<PropertyReportEvent> propertyReportEvent;

    @Inject
    private Instance<TakenResourceRegister> resourceRegister;

    private DefaultFileNameBuilder nb = new DefaultFileNameBuilder();

    // starts

    public void onStartSuiteRecording(@Observes StartRecordSuiteVideo event) {
        File videoTarget = new File("suite", configuration.get().getVideoName());
        recorder.get().startRecording(videoTarget, event.getVideoType());
    }

    public void onStartClassRecording(@Observes StartRecordClassVideo event) {
        File videoTarget = new File("class", configuration.get().getVideoName());
        recorder.get().startRecording(videoTarget, event.getVideoType());
    }

    public void onStartRecording(@Observes StartRecordVideo event) {

        VideoMetaData metaData = event.getVideoMetaData();
        metaData.setResourceType(event.getVideoType());
        String fileName = nb
            .withMetaData(metaData)
            .build();

        File videoTarget = new File(event.getVideoMetaData().getTestClassName(), fileName);
        recorder.get().startRecording(videoTarget, event.getVideoType());
    }

    // stops

    public void onStopSuiteRecording(@Observes StopRecordSuiteVideo event) {
        Video video = recorder.get().stopRecording();
        resourceRegister.get().addTaken(video);
        video.setResourceMetaData(event.getVideoMetaData());

        resourceRegister.get().addReported(video);
        propertyReportEvent.fire(new PropertyReportEvent(getVideoEntry(video)));
    }

    public void onStopClassRecording(@Observes StopRecordClassVideo event) {
        Video video = recorder.get().stopRecording();
        video.setResourceMetaData(event.getVideoMetaData());
        resourceRegister.get().addReported(video);

        propertyReportEvent.fire(new PropertyReportEvent(getVideoEntry(video)));
    }

    public void onStopRecording(@Observes StopRecordVideo event) throws IOException {
        Video video = recorder.get().stopRecording();

        TestResult testResult = event.getVideoMetaData().getTestResult();

        if (testResult != null) {
            Status status = testResult.getStatus();
            appendStatus(video, status);

            resourceRegister.get().addReported(video);

            if ((status.equals(Status.PASSED) && !configuration.get().getTakeOnlyOnFail())
                || (status.equals(Status.FAILED) && configuration.get().getTakeOnlyOnFail())) {
                propertyReportEvent.fire(new PropertyReportEvent(getVideoEntry(video)));
            }
        } else {
            resourceRegister.get().addTaken(video);
        }
    }

    private PropertyEntry getVideoEntry(Video video) {
        VideoEntry videoEntry = new VideoEntry();
        videoEntry.setPath(video.getResource().getAbsolutePath());
        videoEntry.setType(video.getResourceType().toString());
        videoEntry.setSize(Long.toString(video.getResource().length()));
        videoEntry.setHeight(video.getHeight());
        videoEntry.setWidth(video.getWidth());
        videoEntry.setMessage(video.getMessage());
        return videoEntry;
    }

    private void appendStatus(Video video, Status status) {
        File record = video.getResource();

        String fileNameWithExtension = record.getName();

        String newFileName;

        int lastIndexOfDot = fileNameWithExtension.lastIndexOf(".");

        StringBuilder sb = new StringBuilder();

        if (lastIndexOfDot == -1) {
            sb.append(fileNameWithExtension).append("_").append(status.toString().toLowerCase());
        } else {
            String extension = fileNameWithExtension.substring(lastIndexOfDot + 1);
            sb.append(fileNameWithExtension.substring(0, lastIndexOfDot))
                .append("_")
                .append(status.toString().toLowerCase())
                .append(".")
                .append(extension);
        }

        newFileName = sb.toString();

        File moved = new File(record.getParentFile(), newFileName);

        record.renameTo(moved);
        video.setResource(moved);
    }

}
