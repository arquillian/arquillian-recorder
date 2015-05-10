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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.arquillian.extension.recorder.screenshooter.ScreenshotMetaData;
import org.arquillian.extension.recorder.screenshooter.ScreenshotType;
import org.arquillian.extension.recorder.screenshooter.api.BlurLevel;
import org.arquillian.extension.recorder.screenshooter.event.AfterScreenshotTaken;
import org.jboss.arquillian.core.api.annotation.Observes;

/**
 * After Screenshot Taken Observer which is thrown after each screenshot is taken and blurs an image if it is necessary.
 * @author <a href="mailto:asotobu@gmail.com">Alex Soto</a>
 *
 */
public class BlurAfterScreenshotTakenObserver {

    public void blurImageIfNecessary(@Observes AfterScreenshotTaken event) {
        ScreenshotMetaData metaData = event.getMetaData();
        if (metaData != null) {

            BlurLevel blurLevel = metaData.getBlurLevel();
            if (blurLevel != null && metaData.getResource() != null) {
                File screeenshot = metaData.getResource();

                try {
                    //Read the image
                    BufferedImage screenshotBuffer = ImageIO.read(screeenshot);

                    //Blur the image with desired radius
                    BufferedImage blurredScreenshotBuffer = blurLevel.blur(screenshotBuffer);

                    //Gets the output format to store the image in same format as original
                    ScreenshotType type = ScreenshotType.PNG;
                    if(metaData.getResourceType() != null && metaData.getResourceType() instanceof ScreenshotType) {
                        type = (ScreenshotType) metaData.getResourceType();
                    }

                    //Overwrite the image
                    ImageIO.write(blurredScreenshotBuffer, type.toString(), screeenshot);
                } catch (IOException e) {
                    throw new IllegalArgumentException(e);
                }

            }
        }
    }
}
