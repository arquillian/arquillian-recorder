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

import org.arquillian.extension.recorder.ResourceMetaData;
import org.arquillian.extension.recorder.screenshooter.api.BlurLevel;

/**
 * @author <a href="mailto:smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class ScreenshotMetaData extends ResourceMetaData {

    private int width;

    private int height;

    private BlurLevel blurLevel;

    private File resource;

    public BlurLevel getBlurLevel() {
        return blurLevel;
    }

    public void setBlurLevel(BlurLevel blurLevel) {
        this.blurLevel = blurLevel;
    }

    public File getResource() {
        return resource;
    }

    public void setFilename(File filename) {
        this.resource = filename;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

}
