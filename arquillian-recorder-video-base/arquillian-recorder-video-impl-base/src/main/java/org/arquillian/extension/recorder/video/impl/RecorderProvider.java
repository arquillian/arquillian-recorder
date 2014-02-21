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
package org.arquillian.extension.recorder.video.impl;

import java.lang.annotation.Annotation;
import org.arquillian.extension.recorder.video.Recorder;
import org.arquillian.extension.recorder.video.VideoConfiguration;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

/**
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class RecorderProvider implements ResourceProvider {

    @Inject
    private Instance<Recorder> recorder;

    @Inject
    private Instance<VideoConfiguration> configuration;

    @Override
    public boolean canProvide(Class<?> type) {
        return Recorder.class.isAssignableFrom(type);
    }

    @Override
    public Object lookup(ArquillianResource resource, Annotation... qualifiers) {
        Recorder recorder = this.recorder.get();

        VideoConfiguration configuration = this.configuration.get();

        if (configuration.getStartBeforeClass() || configuration.getStartBeforeSuite()
            || configuration.getStartBeforeTest() || configuration.getTakeOnlyOnFail()) {
            throw new IllegalStateException("It is not possible to inject video recorder into test "
                + "when you have specified that you want to take videos automatically via configuration "
                + "where you set one of start* properties to true or takeOnlyOnFail to true. In order "
                + "to use recorder manually in test class, you have to set all mentioned above to false.");
        }

        if (recorder == null) {
            throw new IllegalStateException("Unable to inject recorder into test.");
        }

        return recorder;
    }

}
