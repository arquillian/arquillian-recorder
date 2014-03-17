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
package org.arquillian.extension.recorder.video.desktop.impl;

import java.io.File;

import org.arquillian.extension.recorder.DefaultFileNameBuilder;
import org.arquillian.extension.recorder.RecorderFileUtils;
import org.arquillian.extension.recorder.video.Recorder;
import org.arquillian.extension.recorder.video.Video;
import org.arquillian.extension.recorder.video.VideoConfiguration;
import org.arquillian.extension.recorder.video.VideoMetaData;
import org.arquillian.extension.recorder.video.VideoType;
import org.jboss.arquillian.core.spi.Validate;

/**
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class DesktopVideoRecorder implements Recorder {

    private static final int MIN_FRAMERATE = 0;

    private static final int MAX_FRAMERATE = 100;

    private File videoTargetDir;

    private VideoType videoType;

    private VideoConfiguration configuration;

    private VideoRecorder recorder;

    @Override
    public void init(VideoConfiguration configuration) {
        if (this.configuration == null) {
            if (configuration != null) {
                this.configuration = configuration;
                File root = new File(this.configuration.getRootDir(), this.configuration.getBaseDir());

                setVideoTargetDir(root);
                setVideoType(VideoType.valueOf(this.configuration.getVideoType()));

                recorder = new VideoRecorder(this.configuration);
            }
        }
    }

    @Override
    public void startRecording() {
        startRecording(videoType);
    }

    @Override
    public void startRecording(VideoType videoType) {
        Validate.notNull(videoType, "Video type is a null object!");
        VideoMetaData metaData = new VideoMetaData();
        metaData.setResourceType(videoType);
        startRecording(
            new File(DefaultFileNameBuilder.getInstance().withMetaData(metaData).build()),
            videoType);
    }

    @Override
    public void startRecording(String fileName) {
        Validate.notNullOrEmpty(fileName, "File name is a null object or an empty string!");
        startRecording(new File(fileName));
    }

    @Override
    public void startRecording(File file) {
        Validate.notNull(file, "File is a null object");
        startRecording(file, videoType);
    }

    @Override
    public void startRecording(String fileName, VideoType videoType) {
        Validate.notNullOrEmpty(fileName, "File name is a null object or an empty string!");
        Validate.notNull(videoType, "Type of video is a null object!");
        startRecording(fileName, videoType);
    }

    @Override
    public void startRecording(File file, VideoType videoType) {
        if (recorder == null) {
            throw new IllegalStateException("It seems you have not called init() method of this video recorder yet.");
        }

        if (recorder.isRecording()) {
            throw new IllegalStateException("It seems you are already recording some video, call stopRecording() firstly.");
        }

        file = RecorderFileUtils.checkFileExtension(file, videoType);
        file = new File(videoTargetDir, file.getPath());
        RecorderFileUtils.createDirectory(file.getParentFile());

        recorder.startRecording(file);
    }

    @Override
    public Video stopRecording() {
        if (recorder != null && recorder.isRecording()) {
            return recorder.stopRecording();
        }
        throw new IllegalStateException("It seems you have not called init() method of this video recorder yet.");
    }

    @Override
    public void setVideoTargetDir(String videoTargetDir) {
        Validate.notNullOrEmpty(videoTargetDir, "Video target directory can not be a null object nor an empty string!");
        setVideoTargetDir(new File(videoTargetDir));
    }

    @Override
    public void setVideoTargetDir(File videoTargetDir) {
        Validate.notNull(videoTargetDir, "File is a null object!");
        RecorderFileUtils.createDirectory(videoTargetDir);
        this.videoTargetDir = videoTargetDir;
    }

    @Override
    public void setVideoType(VideoType videoType) {
        Validate.notNull(videoType, "Video type is a null object!");
        this.videoType = videoType;
    }

    @Override
    public VideoType getVideoType() {
        return videoType;
    }

    @Override
    public void setFrameRate(int framerate) {
        if (recorder != null) {
            if (framerate > MIN_FRAMERATE && framerate < MAX_FRAMERATE) {
                recorder.setFrameRate(framerate);
            }
            return;
        }
        throw new IllegalStateException("It seems you have not called init() method of this video recorder yet.");
    }

    @Override
    public int getFrameRate() {
        if (recorder != null) {
            return recorder.getFrameRate();
        }
        throw new IllegalStateException("It seems you have not called init() method of this video recorder yet.");
    }

}
