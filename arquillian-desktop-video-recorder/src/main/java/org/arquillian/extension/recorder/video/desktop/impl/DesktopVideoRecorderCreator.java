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

import org.arquillian.extension.recorder.video.Recorder;
import org.arquillian.extension.recorder.video.VideoConfiguration;
import org.arquillian.extension.recorder.video.desktop.configuration.DesktopVideoConfiguration;
import org.arquillian.extension.recorder.video.event.VideoExtensionConfigured;
import org.arquillian.recorder.reporter.impl.TakenResourceRegister;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;

/**
 * Observes:
 * <ul>
 * <li>{@link VideoExtensionConfigured}</li>
 * </ul>
 * Produces {@link ApplicationScoped}:
 * <ul>
 * <li>{@link Recorder}</li>
 * <li>{@link TakenResourceRegister}</li>
 * </ul>
 * 
 * @author <a href="mailto:pmensik@redhat.com">Petr Mensik</a>
 */
public class DesktopVideoRecorderCreator {

    @Inject
    @ApplicationScoped
    private InstanceProducer<Recorder> recorder;

    @Inject
    @ApplicationScoped
    private InstanceProducer<TakenResourceRegister> takenResourceRegister;

    @Inject
    private Instance<VideoConfiguration> configuration;

    public void onVideoRecorderExtensionConfigured(@Observes VideoExtensionConfigured event) {

        if (takenResourceRegister.get() == null) {
            this.takenResourceRegister.set(new TakenResourceRegister());
        }

        Recorder recorder = new DesktopVideoRecorder(takenResourceRegister.get());
        recorder.init((DesktopVideoConfiguration) configuration.get());

        this.recorder.set(recorder);
    }

}
