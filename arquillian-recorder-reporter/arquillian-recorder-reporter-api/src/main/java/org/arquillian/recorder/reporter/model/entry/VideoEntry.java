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

import org.arquillian.extension.recorder.When;

/**
 * Represents video being taken during test.<br>
 * <br>
 * Can hold:
 * <ul>
 * <li>phase as {@link When}</li>
 * </ul>
 *
 * @see {@link FileEntry}
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
@XmlRootElement(name = "video")
public class VideoEntry extends FileEntry {

    private When phase;

    @XmlAttribute(required = false)
    public When getPhase() {
        return phase;
    }

    public void setPhase(When phase) {
        this.phase = phase;
    }
}
