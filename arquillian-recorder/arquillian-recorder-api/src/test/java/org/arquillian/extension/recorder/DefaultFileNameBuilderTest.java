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
package org.arquillian.extension.recorder;

import java.util.UUID;

import org.jboss.arquillian.test.spi.TestClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
@RunWith(JUnit4.class)
public class DefaultFileNameBuilderTest {

    private DefaultFileNameBuilder fileNameBuilder = new DefaultFileNameBuilder();

    @Test
    public void emptyBuildTest() {
        // produces valid uuid
        UUID.fromString(fileNameBuilder.build());
    }

    @Test
    public void nonNullMetaDataTest() throws Exception {
        FakeResourceMetaData fmd = new FakeResourceMetaData();
        fmd.setTestClass(new TestClass(FakeTestClass.class)).setTestMethod(FakeTestClass.class.getMethod("fakeTestMethod"));

        Assert.assertEquals("fakeTestMethod", fileNameBuilder.withMetaData(fmd).build());
    }

    @Test
    public void nonNullStageTest() throws Exception {
        FakeResourceMetaData fmd = new FakeResourceMetaData();
        fmd.setTestClass(new TestClass(FakeTestClass.class)).setTestMethod(FakeTestClass.class.getMethod("fakeTestMethod"));

        Assert.assertEquals("fakeTestMethod_after", fileNameBuilder.withMetaData(fmd).withStage(When.AFTER).build());
    }

    @Test
    public void nonNullTypeTest() throws Exception {
        FakeResourceMetaData fmd = new FakeResourceMetaData();
        fmd
            .setTestClass(new TestClass(FakeTestClass.class))
            .setTestMethod(FakeTestClass.class.getMethod("fakeTestMethod"))
            .setResourceType(FakeResourceType.SOMETYPE);

        Assert.assertEquals("fakeTestMethod_after.type", fileNameBuilder
            .withMetaData(fmd)
            .withStage(When.AFTER)
            .build());
    }

    @Test
    public void builderIsClearedAfterBuildTest() throws NoSuchMethodException, SecurityException {
        FakeResourceMetaData fmd = new FakeResourceMetaData();
        fmd.setTestClass(new TestClass(FakeTestClass.class)).setTestMethod(FakeTestClass.class.getMethod("fakeTestMethod"));
        fileNameBuilder.withStage(When.AFTER).withMetaData(fmd);

        Assert.assertEquals("fakeTestMethod_after", fileNameBuilder.build());

        // subsequent built is done on cleaned builder so it will produce just UUID
        UUID.fromString(fileNameBuilder.build());
    }

    private static class FakeResourceMetaData extends ResourceMetaData {

    }

    private static class FakeTestClass {
        @SuppressWarnings("unused")
        public void fakeTestMethod() {

        }
    }
}
