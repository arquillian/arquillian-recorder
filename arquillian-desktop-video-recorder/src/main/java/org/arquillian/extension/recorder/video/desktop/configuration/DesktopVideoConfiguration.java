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
package org.arquillian.extension.recorder.video.desktop.configuration;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.arquillian.extension.recorder.video.VideoConfiguration;
import org.arquillian.extension.recorder.video.VideoConfigurationException;
import org.arquillian.recorder.reporter.ReporterConfiguration;

/**
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class DesktopVideoConfiguration extends VideoConfiguration {

    private static Logger logger = Logger.getLogger(DesktopVideoConfiguration.class.getSimpleName());

    private String frameRate = "20"; // fps

    public DesktopVideoConfiguration(ReporterConfiguration reporterConfiguration) {
        super(reporterConfiguration);
    }

    /**
     * By default set to 20 fps when not overridden in configuration.
     *
     * @return framerate of which videos should be taken
     */
    public int getFrameRate() {
        return Integer.parseInt(getProperty("frameRate", frameRate));
    }

    @Override
    public void validate() throws VideoConfigurationException {
        super.validate();

        try {
            if (Integer.parseInt(getProperty("frameRate", this.frameRate)) <= 0) {
                throw new VideoConfigurationException("It seems you have set framerate to be lower or equal to 0.");
            }
        } catch (NumberFormatException ex) {
            throw new VideoConfigurationException("Provided framerate is not recognized to be an integer number.");
        }

        if ((getStartBeforeClass() || getStartBeforeSuite())
            && getStartBeforeTest()) {
            setProperty("startBeforeTest", "false");
            logger.log(Level.INFO, "You have set both startBeforeTest and startBeforeSuite or startBeforeClass to true - "
                + "startBeforeTest was set to false.");
        }

        if (getStartBeforeSuite() && getStartBeforeClass()) {
            logger.log(Level.INFO, "You have set startBeforeSuite and startBeforeClass both to true. You can not set them "
                + "simultaneously to true - startBeforeSuite has precedence so startBeforeClass was set to true.");
            setProperty("startBeforeClass", "false");
        }

        if (getTakeOnlyOnFail() && getStartBeforeTest()) {
            setProperty("startBeforeTest", "false");
            logger.log(Level.INFO, "You have set takeOnlyOnFail to true as well as startBeforeTest to true. You can not set "
                + "both to true - startBeforeTest was set to false. Videos of all tests will be recorded automatically "
                + "and some video of test will be preserved only if test fails.");
        }

        if ((getStartBeforeClass() || getStartBeforeSuite() || getStartBeforeTest()) && getTakeOnlyOnFail()) {

            setProperty("startBeforeClass", "false");
            setProperty("startBeforeSuite", "false");
            setProperty("startBeforeTest", "false");

            logger.log(Level.INFO, "You have set one of startBeforeClass, startBeforeSuite or startBeforeTest in connection "
                + "with takeOnlyOnFail. All start* properties were set to false so every @Test will be recorded and "
                + "preserved only in case it has failed");
        }
    }

    @Override
    public String toString() {
        String parent = super.toString();
        StringBuilder sb = new StringBuilder();
        sb.append(parent);
        sb.append(String.format("%-40s %s\n", "frameRate", getFrameRate()));
        return sb.toString();
    }
}
