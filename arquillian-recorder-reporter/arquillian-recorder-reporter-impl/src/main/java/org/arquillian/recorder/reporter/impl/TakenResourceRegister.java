/**
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
package org.arquillian.recorder.reporter.impl;

import java.util.ArrayList;
import java.util.List;
import org.arquillian.extension.recorder.screenshooter.Screenshot;
import org.arquillian.extension.recorder.video.Video;
import org.jboss.arquillian.core.spi.Validate;

/**
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class TakenResourceRegister {

    private final List<Screenshot> takenScreenshots = new ArrayList<Screenshot>();

    private final List<Screenshot> reportedScreenshots = new ArrayList<Screenshot>();

    private final List<Video> takenVideos = new ArrayList<Video>();

    private final List<Video> reportedVideos = new ArrayList<Video>();

    public boolean addTaken(Screenshot screenshot) {
        Validate.notNull(screenshot, "Screenshot can not be a null object!");
        return takenScreenshots.add(screenshot);
    }

    public boolean addReported(Screenshot screenshot) {
        Validate.notNull(screenshot, "Screenshot can not be a null object!");
        return reportedScreenshots.add(screenshot);
    }

    public boolean addTaken(Video video) {
        Validate.notNull(video, "Video can not be a null object!");
        return takenVideos.add(video);
    }

    public boolean addReported(Video video) {
        Validate.notNull(video, "Video can not be a null object!");
        return reportedVideos.add(video);
    }

    public List<Screenshot> getTakenScreenshots() {
        return takenScreenshots;
    }

    public List<Screenshot> getReportedScreenshots() {
        return reportedScreenshots;
    }

    public List<Video> getTakenVideos() {
        return takenVideos;
    }

    public List<Video> getReportedVideos() {
        return reportedVideos;
    }

    public boolean containsTaken(Screenshot screenshot) {
        return takenScreenshots.contains(screenshot);
    }

    public boolean containsReported(Screenshot screenshot) {
        return reportedScreenshots.add(screenshot);
    }

    public boolean containsTaken(Video video) {
        return takenVideos.contains(video);
    }

    public boolean containsReported(Video video) {
        return reportedVideos.contains(video);
    }

    public void invalidateAll() {
        invalidateScreenshots();
        invalidateVideos();
    }

    public void invalidateScreenshots() {
        takenScreenshots.clear();
        reportedScreenshots.clear();
    }

    public void invalidateVideos() {
        takenVideos.clear();
        reportedVideos.clear();
    }
}
