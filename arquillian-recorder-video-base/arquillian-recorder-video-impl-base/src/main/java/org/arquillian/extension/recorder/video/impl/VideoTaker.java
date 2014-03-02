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

import org.apache.commons.io.FileUtils;
import org.arquillian.extension.recorder.DefaultFileNameBuilder;
import org.arquillian.extension.recorder.video.Recorder;
import org.arquillian.extension.recorder.video.Video;
import org.arquillian.extension.recorder.video.VideoConfiguration;
import org.arquillian.extension.recorder.video.VideoMetaData;
import org.arquillian.extension.recorder.video.event.StartRecordSuiteVideo;
import org.arquillian.extension.recorder.video.event.StartRecordVideo;
import org.arquillian.extension.recorder.video.event.StopRecordSuiteVideo;
import org.arquillian.extension.recorder.video.event.StopRecordVideo;
import org.arquillian.recorder.reporter.PropertyEntry;
import org.arquillian.recorder.reporter.event.PropertyReportEvent;
import org.arquillian.recorder.reporter.model.entry.VideoEntry;
import org.jboss.arquillian.core.api.Event;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.EventContext;
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

    private DefaultFileNameBuilder nb = DefaultFileNameBuilder.getInstance();

    public void onStartSuiteRecording(@Observes EventContext<StartRecordSuiteVideo> context) {
        File videoTarget = new File("suite", configuration.get().getVideoName());
        recorder.get().startRecording(videoTarget, context.getEvent().getVideoType());

        context.proceed();
    }

    public void onStartRecording(@Observes EventContext<StartRecordVideo> context) {

        VideoMetaData metaData = context.getEvent().getVideoMetaData();
        metaData.setResourceType(context.getEvent().getVideoType());
        String fileName = nb
            .withMetaData(metaData)
            .build();

        File videoTarget = new File(context.getEvent().getVideoMetaData().getTestClassName(), fileName);
        recorder.get().startRecording(videoTarget, context.getEvent().getVideoType());

        context.proceed();
    }

    public void onStopSuiteRecording(@Observes EventContext<StopRecordSuiteVideo> context) {
        Video video = recorder.get().stopRecording();
        video.setResourceMetaData(context.getEvent().getVideoMetaData());

        propertyReportEvent.fire(new PropertyReportEvent(getVideoEntry(video)));

        context.proceed();
    }

    public void onStopRecording(@Observes EventContext<StopRecordVideo> context) throws IOException {
        Video video = recorder.get().stopRecording();

        TestResult testResult = context.getEvent().getVideoMetaData().getTestResult();

        if (testResult != null) {
            Status status = testResult.getStatus();
            appendStatus(video, status);
            if (!status.equals(Status.FAILED) && configuration.get().getTakeOnlyOnFail()) {
                if (!video.getResource().getAbsoluteFile().delete()) {
                    System.out.println("video was not deleted: " + video.getResource().getAbsolutePath());
                }
                File directory = video.getResource().getParentFile();
                if (directory != null && directory.listFiles().length == 0) {
                    FileUtils.deleteDirectory(directory);
                }
            }
        }

        propertyReportEvent.fire(new PropertyReportEvent(getVideoEntry(video)));

        context.proceed();
    }

    private PropertyEntry getVideoEntry(Video video) {
        VideoEntry videoEntry = new VideoEntry();
        videoEntry.setPath(video.getResource().getAbsolutePath());
        videoEntry.setType(video.getResourceType().toString());
        videoEntry.setSize(Long.toString(video.getResource().length()));
        videoEntry.setHeight(video.getHeight());
        videoEntry.setWidth(video.getWidth());
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
