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
package org.arquillian.recorder.reporter.model.entry;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.arquillian.extension.recorder.When;

/**
 * Represents a screenshot being taken during some test.<br>
 * <br>
 * Can hold:
 * <ul>
 * <li>phase as {@link When}</li>
 * <li>width</li>
 * <li>height</li>
 * <li>link</li>
 * </ul>
 *
 * @see FileEntry
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
@XmlRootElement(name = "screenshot")
@XmlType(propOrder = { "phase", "width", "height", "link" })
public class ScreenshotEntry extends FileEntry {

    private When phase;

    private int width;

    private int height;

    private String link;

    @XmlAttribute(required = false)
    public When getPhase() {
        return phase;
    }

    public void setPhase(When phase) {
        this.phase = phase;
    }

    @XmlAttribute(required = false)
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @XmlAttribute(required = false)
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @XmlAttribute
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}
