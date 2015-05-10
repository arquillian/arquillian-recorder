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
package org.arquillian.extension.recorder.screenshooter.api;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

import com.jhlabs.image.BoxBlurFilter;

/**
 * This enum is used to blur a BufferedImage according to level.
 *
 * @author <a href="mailto:asotobu@gmail.com">Alex Soto</a>
 *
 */
public enum BlurLevel {
    NONE {
        @Override
        public BufferedImage blur(BufferedImage image) {
            return image;
        }
    },
    LOW {
        @Override
        public BufferedImage blur(BufferedImage image) {
            return blur(image, 10);
        }
    },
    MEDIUM {
        @Override
        public BufferedImage blur(BufferedImage image) {
            return blur(image, 15);
        }
    },
    HIGH {
        @Override
        public BufferedImage blur(BufferedImage image) {
            return blur(image, 25);
        }
    };

    protected BufferedImage blur(BufferedImage srcImage, int radius) {
        BufferedImage destImage = deepCopy(srcImage);
        BoxBlurFilter boxBlurFilter = new BoxBlurFilter();
        boxBlurFilter.setRadius(radius);
        boxBlurFilter.setIterations(3);
        destImage = boxBlurFilter.filter(srcImage, destImage);

        return destImage;

    }

    private BufferedImage deepCopy(BufferedImage srcImage) {
        ColorModel cm = srcImage.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = srcImage.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public abstract BufferedImage blur(BufferedImage image);
}
