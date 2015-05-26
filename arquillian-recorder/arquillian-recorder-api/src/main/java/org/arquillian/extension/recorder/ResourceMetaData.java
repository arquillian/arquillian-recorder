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

import java.lang.reflect.Method;

import org.jboss.arquillian.core.spi.Validate;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.arquillian.test.spi.TestResult;

/**
 * Meta data which are related to any {@link Resource}.
 *
 * @author <a href="mailto:smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public abstract class ResourceMetaData {

    private TestResult testResult;

    private TestClass testClass;

    private Method testMethod;

    private long timeStamp;

    private String optionalDescription;

    private ResourceType resourceType;

    /**
     *
     * @return name of test class as String or null if not set
     */
    public String getTestClassName() {
        return getTestClass() == null ? null : getTestClass().getName();
    }

    public TestClass getTestClass() {
        return testClass;
    }

    /**
     * Sets a class for some particular {@code Resource} where that resource was created.
     *
     * @param testClass test class where some resource was created
     * @return {@code this}
     * @throws IllegalArgumentException if {@code TestClass} is a null object
     */
    public ResourceMetaData setTestClass(TestClass testClass) {
        Validate.notNull(testClass, "Test class is a null object!");
        this.testClass = testClass;
        return this;
    }

    /**
     *
     * @return name of test method as String or null if not set
     */
    public String getTestMethodName() {
        return getTestMethod() == null ? null : getTestMethod().getName();
    }

    public Method getTestMethod() {
        return testMethod;
    }

    /**
     * Sets a test method for some particular {@code Resource} where that resource was created.
     *
     * @param testMethod test method where some resource was created
     * @return {@code this}
     * @throws IllegalArgumentException if {@code testMethod} is a null object.
     */
    public ResourceMetaData setTestMethod(Method testMethod) {
        Validate.notNull(testMethod, "Method is a null object!");
        this.testMethod = testMethod;
        return this;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    /**
     * Time stamp specifying when some particular {@code Resource} was created.
     *
     * @param timeStamp UNIX time stamp to set
     * @return {@code this}
     */
    public ResourceMetaData setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }

    /**
     * Sets a TestResult for some particular {@code Resource} where that resource was created.
     *
     * @param testResult class which contains information whether test was failed, passed or skipped
     * @return {@code this}
     * @throws IllegalArgumentException if {@code TestResult} is a null object
     */
    public ResourceMetaData setTestResult(TestResult testResult) {
        Validate.notNull(testResult, "TestResult is a null object!");
        this.testResult = testResult;
        return this;
    }

    /**
     *
     * @return {@code TestResult} or null if not set
     */
    public TestResult getTestResult() {
        return testResult;
    }

    /**
     *
     * @return type of the resource, e.g JPG for screenshot
     */
    public ResourceType getResourceType() {
        return resourceType;
    }

    /**
     *
     * @return optional description of the resource
     */
    public String getOptionalDescription() {
        return optionalDescription;
    }

    /**
     * Sets optional description of the resource
     *
     * @param optionalDescription description of this resource
     */
    public void setOptionalDescription(String optionalDescription) {
        Validate.notNull(optionalDescription, "OptionalDescription is null!");
        this.optionalDescription = optionalDescription;
    }

    /**
     * Sets the type of the resource, that is e.g. JPG for screenshots
     *
     * @param resourceType type of the resource
     */
    public void setResourceType(ResourceType resourceType) {
        Validate.notNull(resourceType, "ResourceType is null!");
        this.resourceType = resourceType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("test class\t\t:\n").append(getTestClass())
            .append("test method\t\t:\n").append(getTestMethod())
            .append("timestamp\t\t:\n").append(getTimeStamp())
            .append(optionalDescription.isEmpty() ? "" : "payload\t\t:\n" + optionalDescription);
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((optionalDescription == null) ? 0 : optionalDescription.hashCode());
        result = prime * result + ((resourceType == null) ? 0 : resourceType.hashCode());
        result = prime * result + ((testClass == null) ? 0 : testClass.hashCode());
        result = prime * result + ((testMethod == null) ? 0 : testMethod.hashCode());
        result = prime * result + ((testResult == null) ? 0 : testResult.hashCode());
        result = prime * result + (int) (timeStamp ^ (timeStamp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ResourceMetaData other = (ResourceMetaData) obj;
        if (optionalDescription == null) {
            if (other.optionalDescription != null)
                return false;
        } else if (!optionalDescription.equals(other.optionalDescription))
            return false;
        if (resourceType == null) {
            if (other.resourceType != null)
                return false;
        } else if (!resourceType.equals(other.resourceType))
            return false;
        if (testClass == null) {
            if (other.testClass != null)
                return false;
        } else if (!testClass.equals(other.testClass))
            return false;
        if (testMethod == null) {
            if (other.testMethod != null)
                return false;
        } else if (!testMethod.equals(other.testMethod))
            return false;
        if (testResult == null) {
            if (other.testResult != null)
                return false;
        } else if (!testResult.equals(other.testResult))
            return false;
        if (timeStamp != other.timeStamp)
            return false;
        return true;
    }
}
