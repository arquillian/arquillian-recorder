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
package org.arquillian.extension.recorder;

import java.io.File;

import org.jboss.arquillian.core.spi.Validate;

/**
 * Base class for all resources we could generate during any test run.
 *
 * @author <a href="mailto:smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public abstract class Resource<T extends ResourceMetaData, R extends ResourceType> {

    private File resource;

    private T resourceMetaData;

    private R resourceType;

    public T getResourceMetaData() {
        return resourceMetaData;
    }

    public void setResourceMetaData(T resourceMetaData) {
        Validate.notNull(resourceMetaData, "Resource metadata you are trying to set is a null object!");
        this.resourceMetaData = resourceMetaData;
    }

    public File getResource() {
        return resource;
    }

    public void setResource(File resource) {
        Validate.notNull(resource, "Resource you are trying to set is a null object!");
        this.resource = resource;
    }

    public R getResourceType() {
        return resourceType;
    }

    public void setResourceType(R resourceType) {
        Validate.notNull(resourceType, "Resource type you are trying to set is a null object!");
        this.resourceType = resourceType;
    }

}
