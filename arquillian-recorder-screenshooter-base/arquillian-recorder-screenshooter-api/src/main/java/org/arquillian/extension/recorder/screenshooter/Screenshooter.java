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
package org.arquillian.extension.recorder.screenshooter;

import java.io.File;

/**
 * Implement this interface in order to take screenshots in concrete screenshooter extension implementation.
 *
 * @author <a href="mailto:smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public interface Screenshooter {

    /**
     * Initializes particular screenshooter implementation, e.g. by setting target directory where all screenshots will be saved
     * afterwards.
     *
     * @param configuration configuration of this screenshooter implementation
     */
    void init(ScreenshooterConfiguration configuration);

    /**
     * Takes screenshot in default format with random string as a name with file format extension.
     *
     * @return screenshot of default image format
     */
    Screenshot takeScreenshot();

    /**
     * Takes screenshot in specified format. Name of screenshot is random string with file format extension.
     *
     * @param type type of screenshot
     * @return screenshot of given image type
     */
    Screenshot takeScreenshot(ScreenshotType type);

    /**
     * Takes screenshot of default file format with specified name.
     *
     * @param fileName name of file without file format extension
     * @return screenshot of default format with specified name
     */
    Screenshot takeScreenshot(String fileName);

    /**
     * Takes screenshot of default file format to file
     *
     * @param file file to save taken screenshot to
     * @return screenshot saved in {@code file} in default format
     */
    Screenshot takeScreenshot(File file);

    /**
     * Takes screenshot of specified type which is saved under specified name
     *
     * @param fileName name of file without file format extension
     * @param type type of screenshot required
     * @return screenshot of specified format with a specified name
     */
    Screenshot takeScreenshot(String fileName, ScreenshotType type);

    /**
     * Takes screenshot of specified type which is saved under specified name
     *
     * @param file file to save taken screenshot to
     * @param type type of screenshot required
     * @return screenshot of specified format saved in {@code file}
     */
    Screenshot takeScreenshot(File file, ScreenshotType type);

    /**
     * Sets a directory where all taken screenshots will be saved from now on.
     *
     * @param screenshotTargetDir directory to save screenshots to
     * @throws IllegalArgumentException if {@code screenshotTargetDir} is null, empty or does not represents existing and
     *         writable directory
     * @return this screenshooter
     */
    Screenshooter setScreenshotTargetDir(String screenshotTargetDir);

    /**
     * Sets a directory where all taken screenshots will be saved from now on.
     *
     * @param screenshotTargetDir directory to save screenshots to
     * @throws IllegalArgumentException if {@code screenshotTargetDir} is null, empty or does not represents existing and
     *         writable directory
     * @return this screenshooter
     */
    Screenshooter setScreenshotTargetDir(File screenshotTargetDir);

    /**
     * Sets the format of images to take. After setting this, all subsequent images will be of this format when not explicitly
     * specified otherwise.
     *
     * @param type type of screenshots to take from now on
     * @return this screenshooter
     */
    Screenshooter setScreenshotType(ScreenshotType type);

    /**
     *
     * @return type of screenshot this screenshooter makes after taking it
     */
    ScreenshotType getScreenshotType();

    /**
     * You have access to this message in resulting report so you can document what some screenshot actually means. If message
     * is a null object, it should not be added to the resulting report. If this method is called before all taken screenshots,
     * all screenshots will share the same message.
     *
     * @param message message to add for to be taken screenshot
     * @return this screenshooter
     */
    Screenshooter setMessage(String message);
}