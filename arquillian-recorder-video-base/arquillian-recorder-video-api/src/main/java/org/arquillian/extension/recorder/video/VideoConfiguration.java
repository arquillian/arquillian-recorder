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

import org.arquillian.extension.recorder.Configuration;
import org.arquillian.recorder.reporter.ReporterConfiguration;

/**
 * Video configuration for every recorder extension implementation.
 *
 * @author <a href="mailto:smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class VideoConfiguration extends Configuration<VideoConfiguration> {

    private static final String ROOT_DIR = "target/videos";

    private static final String VIDEO_TYPE = VideoType.MP4.name();

    private static final String START_BEFORE_TEST = "false";

    private static final String START_BEFORE_CLASS = "false";

    private static final String START_BEFORE_SUITE = "false";

    private static final String TAKE_ONLY_ON_FAIL = "true";

    private static final String VIDEO_NAME = "record";

    private static final String TEST_TIMEOUT = "1800"; // 30 minutes

    private static final String WIDTH = "0"; // pixels

    private static final String HEIGHT = "0"; // pixels.

    private final ReporterConfiguration reporterConfiguration;

    public VideoConfiguration(ReporterConfiguration reporterConfiguration) {
        this.reporterConfiguration = reporterConfiguration;
    }

    /**
     * By default set to true
     *
     * @return true if video should be taken only for the failed tests
     */
    public boolean getTakeOnlyOnFail() {
        return Boolean.parseBoolean(getProperty("takeOnlyOnFail", TAKE_ONLY_ON_FAIL));
    }

    /**
     * By default set to 1800, i.e. 30 minutes
     *
     * @return timeout for each test in order to stop recording and save the video file if the test gets stuck
     */
    public int getTestTimeout() {
        return Integer.parseInt(getProperty("testTimeout", TEST_TIMEOUT));
    }

    /**
     * By default set to "record"
     *
     * @return video name which will be used in case you want to record whole test suite into one file.
     */
    public String getVideoName() {
        return getProperty("videoName", VIDEO_NAME);
    }

    /**
     * By default set to "target/videos"
     *
     * @return root folder where all videos will be placed. Directory structure is left on the extension itself.
     */
    public File getRootDir() {
        return new File(getProperty("rootDir", ROOT_DIR));
    }

    /**
     * By default set to "MP4".
     *
     * @return type of video we want our screenshots to be of
     */
    public String getVideoType() {
        return getProperty("videoType", VIDEO_TYPE).toUpperCase();
    }

    /**
     * By default set to false.
     *
     * @return true if video recording should start before test, false otherwise
     */
    public boolean getStartBeforeTest() {
        return Boolean.parseBoolean(getProperty("startBeforeTest", START_BEFORE_TEST));
    }

    /**
     * By default set to false.
     *
     * @return true if video should be taken before class, false otherwise
     */
    public boolean getStartBeforeClass() {
        return Boolean.parseBoolean(getProperty("startBeforeClass", START_BEFORE_CLASS));
    }

    /**
     * By default set to false.
     *
     * @return true if screenshot should be taken before suite, false otherwise
     */
    public boolean getStartBeforeSuite() {
        return Boolean.parseBoolean(getProperty("startBeforeSuite", START_BEFORE_SUITE));
    }

    /**
     * By default set to 0, you have to set this in configuration in order to use it.
     *
     * @return height of recorded video
     */
    public int getHeight() {
        return Integer.parseInt(getProperty("height", HEIGHT));
    }

    /**
     * By default set to 0, you have to set this in configuration in order to use it.
     *
     * @return width of recorder video
     */
    public int getWidth() {
        return Integer.parseInt(getProperty("width", WIDTH));
    }

    @Override
    public void validate() throws VideoConfigurationException {
        validate(reporterConfiguration);
    }

    private void validate(ReporterConfiguration reporterConfiguration) {
        try {
            VideoType.valueOf(VideoType.class, getVideoType());
        } catch (IllegalArgumentException ex) {
            throw new VideoConfigurationException(
                "Video type you specified in arquillian.xml is not valid video type. "
                        + "The configured video type is: " + getVideoType() + ". "
                        + "The supported video types are: " + VideoType.getAll());
        }

        final String report = reporterConfiguration.getReport().toLowerCase();

        if (report.contains("htm") || report.contains("ad") || report.equals("asciidoc")) {
            final File recorderRootDir = new File(reporterConfiguration.getRootDir(), "videos");
            if (!getRootDir().equals(recorderRootDir)) {
                setProperty("rootDir", recorderRootDir.getAbsolutePath());
            }
        }

        try {
            if (!getRootDir().exists()) {
                boolean created = getRootDir().mkdirs();
                if (!created) {
                    throw new VideoConfigurationException("Unable to create root directory " + getRootDir().getAbsolutePath());
                }
            } else {
                if (!getRootDir().isDirectory()) {
                    throw new VideoConfigurationException("Root directory you specified is not a directory - " +
                        getRootDir().getAbsolutePath());
                }
                if (!getRootDir().canWrite()) {
                    throw new VideoConfigurationException(
                        "You can not write to '" + getRootDir().getAbsolutePath() + "'.");
                }
            }
        } catch (SecurityException ex) {
            throw new VideoConfigurationException(
                "You are not permitted to operate on specified resource: " + getRootDir().getAbsolutePath() + "'.", ex);
        }

        try {
            if (Integer.parseInt(getProperty("width", WIDTH)) < 0) {
                throw new VideoConfigurationException("It seems you have set width of video to be lower then 0.");
            }
        } catch (NumberFormatException ex) {
            throw new VideoConfigurationException("Provided width of video is not recognized to be an integer number.");
        }

        try {
            if (Integer.parseInt(getProperty("height", HEIGHT)) < 0) {
                throw new VideoConfigurationException("It seems you have set height of video to be lower then 0.");
            }
        } catch (NumberFormatException ex) {
            throw new VideoConfigurationException("Provided height of video is not recognized to be an integer number.");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-40s %s%n", "startBeforeSuite", getStartBeforeSuite()));
        sb.append(String.format("%-40s %s%n", "startBeforeClass", getStartBeforeClass()));
        sb.append(String.format("%-40s %s%n", "startBeforeTest", getStartBeforeTest()));
        sb.append(String.format("%-40s %s%n", "takeOnlyOnFail", getTakeOnlyOnFail()));
        sb.append(String.format("%-40s %s%n", "testTimeOut", getTestTimeout()));
        sb.append(String.format("%-40s %s%n", "rootDir", getRootDir()));
        sb.append(String.format("%-40s %s%n", "videoName", getVideoName()));
        sb.append(String.format("%-40s %s%n", "videoType", getVideoType()));
        sb.append(String.format("%-40s %s%n", "width", getWidth()));
        sb.append(String.format("%-40s %s%n", "height", getHeight()));
        return sb.toString();
    }

}
