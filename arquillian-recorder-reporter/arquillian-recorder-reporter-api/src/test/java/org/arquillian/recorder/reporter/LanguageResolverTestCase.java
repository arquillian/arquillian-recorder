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
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

/**
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class LanguageResolverTestCase {

    @Mock
    private LanguageResolver languageResolver;

    @Before
    public void setup() {
        List<String> supportedLanguages = new ArrayList<String>();
        supportedLanguages.addAll(Arrays.asList(new String[] { "en", "fr", "de", "sk", "cz" }));

        Mockito.when(languageResolver.getSupportedLanguages()).thenReturn(supportedLanguages);

        Mockito.when(languageResolver.isLanguageSupported(Mockito.anyString())).then(new Answer<Boolean>() {

            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                return languageResolver.getSupportedLanguages().contains(invocation.getArguments()[0]);
            }
        });
    }

    @Test
    public void testSupportedLanguages() {
        Assert.assertTrue(languageResolver.isLanguageSupported("en"));
        Assert.assertTrue(languageResolver.isLanguageSupported("fr"));
        Assert.assertTrue(languageResolver.isLanguageSupported("de"));
        Assert.assertTrue(languageResolver.isLanguageSupported("sk"));
        Assert.assertTrue(languageResolver.isLanguageSupported("cz"));

        Assert.assertFalse(languageResolver.isLanguageSupported(""));
        Assert.assertFalse(languageResolver.isLanguageSupported("es"));
        Assert.assertFalse(languageResolver.isLanguageSupported(null));
    }

}
