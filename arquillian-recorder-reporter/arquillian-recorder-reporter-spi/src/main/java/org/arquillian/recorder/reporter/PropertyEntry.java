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
package org.arquillian.recorder.reporter;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

/**
 * Entry which does not belong to Arquillian report itself but represents some custom property for it.
 *
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public abstract class PropertyEntry implements ReportEntry {

    @XmlTransient
    private final List<PropertyEntry> propertyEntries = new ArrayList<PropertyEntry>();

    @Override
    public List<PropertyEntry> getPropertyEntries() {
        return propertyEntries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PropertyEntry that = (PropertyEntry) o;

        return propertyEntries.equals(that.propertyEntries);

    }

    @Override
    public int hashCode() {
        return propertyEntries.hashCode();
    }
}
