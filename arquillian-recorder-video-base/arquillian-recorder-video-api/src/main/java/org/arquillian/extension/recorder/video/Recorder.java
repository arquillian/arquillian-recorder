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
package org.arquillian.extension.recorder.video;

import java.io.File;

/**
 * Implementations of this interface are capable of recording videos.
 *
 * @author <a href="mailto:smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public interface Recorder {

    /**
     * Initializes particular video recorder implementation, e.g. by setting target directory where all videos will be saved
     * afterwards.
     *
     * @param configuration configuration of this video recorder implementation
     */
    void init(VideoConfiguration configuration);

    /**
     * Starts to record your test.
     */
    void startRecording();

    /**
     * Starts to record your test.
     *
     * @param videoType type of video you want to get
     */
    void startRecording(VideoType videoType);

    /**
     * Starts to record your test.
     *
     * @param fileName name of video you want to start to record
     */
    void startRecording(String fileName);

    /**
     * Starts to record your test.
     *
     * @param file file where to save recorded video
     */
    void startRecording(File file);

    /**
     * Starts to record your test.
     *
     * @param fileName name of video you want to start to record
     * @param videoType type of video you want to start to record
     */
    void startRecording(String fileName, VideoType videoType);

    /**
     * Starts to record your test.
     *
     * @param file file where to save recorder video
     * @param videoType type of video you want to start to record
     */
    void startRecording(File file, VideoType videoType);

    /**
     *
     * @param videoTargetDir name of directory you want to save all videos to
     * @return this recorder
     */
    Recorder setVideoTargetDir(String videoTargetDir);

    /**
     *
     * @param videoTargetDir directory you want to save all videos to
     * @return this recorder
     */
    Recorder setVideoTargetDir(File videoTargetDir);

    /**
     *
     * @param videoType type of video you want all videos to be
     * @return this recorder
     */
    Recorder setVideoType(VideoType videoType);

    /**
     *
     * @return type of video will will be recorder
     */
    VideoType getVideoType();

    /**
     *
     * Stops to record your video after you started it with {@link #startRecording()}
     *
     * @return recorded video
     */
    Video stopRecording();

    /**
     *
     * @param framerate of recorder videos
     * @return this recorder
     */
    Recorder setFrameRate(int framerate);

    /**
     *
     * @return actual framerate
     */
    int getFrameRate();

    /**
     * You have access to this message in resulting report so you can document what some video actually means. If message is a
     * null object, it should not be added to the resulting report. If this method is called before all recorded videos, all
     * videos will share the same message.
     *
     * @param message message to add for to be recorded video
     * @return this recorder
     */
    Recorder setMessage(String message);

}
