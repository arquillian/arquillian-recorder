/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import org.arquillian.extension.recorder.video.Video;
import org.arquillian.extension.recorder.video.VideoConfiguration;
import org.arquillian.extension.recorder.video.VideoType;
import org.jboss.arquillian.core.spi.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

/**
 *
 * @author <a href="mailto:pmensik@redhat.com">Petr Mensik</a>
 */
class VideoRecorder {

    private static final Logger logger = LoggerFactory.getLogger(VideoRecorder.class);

    public static final int DEFAULT_FRAMERATE = 20;

    public static final int MIN_FRAMERATE = 0;

    public static final int MAX_FRAMERATE = 100;

    private int frameRate = DEFAULT_FRAMERATE;

    private final Dimension screenBounds;

    private volatile boolean running = false;

    private Thread thread;

    private File recordedVideo = null;

    private Timer timer;

    private VideoConfiguration configuration;

    public VideoRecorder(VideoConfiguration configuration) {
        Validate.notNull(configuration, "Video configuration is null!");
        this.configuration = configuration;
        this.frameRate = configuration.getFrameRate();
        screenBounds = Toolkit.getDefaultToolkit().getScreenSize();
    }

    public void startRecording(final File toFile) {
        if (isRecording()) {
            throw new IllegalStateException("Already recording");
        }

        recordedVideo = toFile;

        try {
            recordedVideo.createNewFile();
        } catch (IOException ex) {
            throw new RuntimeException("Unable to create file to which video will be saved: " + recordedVideo.getAbsolutePath());
        }

        running = true;

        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                final IMediaWriter writer = ToolFactory.makeWriter(recordedVideo.getAbsolutePath());
                writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, screenBounds.width / 2, screenBounds.height / 2);

                long startTime = System.nanoTime();

                timer = new Timer();
                timer.schedule(new TestTimeoutTask(), TimeUnit.SECONDS.toMillis(configuration.getTestTimeout()));

                while (running) {
                    BufferedImage bgrScreen = convertToType(getDesktopScreenshot(), BufferedImage.TYPE_3BYTE_BGR);
                    writer.encodeVideo(0, bgrScreen, System.nanoTime() - startTime, TimeUnit.NANOSECONDS);

                    try {
                        Thread.sleep(500 / frameRate);
                    } catch (InterruptedException ex) {
                        logger.error("Exception occured during video recording", ex);
                    }
                    if (!running) {
                        writer.close();
                    }
                }
            }
        });
        thread.start();
    }

    public synchronized Video stopRecording() {
        if (!running) {
            throw new IllegalStateException("It seems you are not recording anything yet. Please call startRecording()");
        }

        running = false;

        try {
            thread.join();
        } catch (InterruptedException ignored) {
            throw new RuntimeException("Unable to stop video recording.");
        }

        timer = null;
        Video video = new DesktopVideo();
        video.setResource(recordedVideo);
        video.setResourceType(VideoType.valueOf(configuration.getVideoType()));
        video.setWidth(screenBounds.width / 2);
        video.setHeight(screenBounds.height / 2);
        return video;
    }

    public int getFrameRate() {
        return frameRate;
    }

    /**
     *
     * @return true if this recording is recording some video right now, false othewise
     */
    public boolean isRecording() {
        return running;
    }

    /**
     *
     * @param frameRate framerate to set has to be between {@link VideoRecorder#MIN_FRAMERATE} and
     *        {@link VideoRecorder#MAX_FRAMERATE} exclusive to take effect.
     */
    public void setFrameRate(int frameRate) {
        if (frameRate > MIN_FRAMERATE && frameRate < MAX_FRAMERATE) {
            this.frameRate = frameRate;
        }
    }

    private BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
        BufferedImage image;
        if (sourceImage.getType() == targetType) {
            image = sourceImage;
        } // otherwise create a new image of the target type and draw the new image
        else {
            image = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }
        return image;

    }

    private BufferedImage getDesktopScreenshot() {
        try {
            Robot robot = new Robot();
            Rectangle captureSize = new Rectangle(screenBounds);
            return robot.createScreenCapture(captureSize);
        } catch (AWTException e) {
            logger.error("Exception occured while taking screenshot for video record", e);
            return null;
        }
    }

    private class TestTimeoutTask extends TimerTask {

        @Override
        public void run() {
            stopRecording();
            cancel();
        }
    }

}
