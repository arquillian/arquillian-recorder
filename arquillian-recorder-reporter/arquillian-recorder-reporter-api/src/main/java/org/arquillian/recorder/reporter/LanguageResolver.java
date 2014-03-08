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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Resolves language specific questions. You can not run tests from IDE since resolution of these resources
 *
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
class LanguageResolver implements Resolver {

    public static final String DEFAULT_TEMPLATE_DIR = "arquillian_reporter_templates/";

    public static final String DEFAULT_TEMPLATE_BASE_DIR = "arquillian_reporter_templates_base/";

    public static final String DEFAULT_TEMPLATE_EXTENSION = "xsl";

    private final List<String> supportedLanguages;

    public LanguageResolver() {
        supportedLanguages = resolveSupportedLanguages();
    }

    @Override
    public boolean resolveAsBoolean(String toResolve) {
        return getSupportedLanguages().contains(toResolve);
    }

    @Override
    public String resolveAsString(String toResolve) {
        boolean answer = resolveAsBoolean(toResolve);
        return Boolean.toString(answer);
    }

    /**
     * Decides if some language is supported or not. Supported languages are mapped as directory names in templates/_language_
     * placed on class path.
     *
     * @param language language to query the support of
     * @return true if {@code language} is supported, false otherwise.
     */
    public boolean isLanguageSupported(String language) {
        return resolveAsBoolean(language);
    }

    public List<String> getSupportedLanguages() {
        return supportedLanguages;
    }

    /**
     * Supported languages for templates are resolved from arquillian_reporter_templates/_language_ directory which is bundled
     * in jar of reporter api. In order to be able to support languages when you run your test from IDE as Eclipse, in order to
     * be able to scan it, you have to put it on build path (project->Propeties->Java Build Path->Add External JARs). When you
     * test with Maven, it behaves ok.
     *
     * @return
     */
    protected List<String> resolveSupportedLanguages() {
        List<String> supportedLanguages = new ArrayList<String>();
        final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());

        if (jarFile.isFile()) { // Run with JAR file
            try {
                JarFile jar = new JarFile(jarFile);
                final Enumeration<JarEntry> entries = jar.entries();
                JarEntry entry;
                while (entries.hasMoreElements()) {
                    entry = entries.nextElement();
                    if ((entry.getName().startsWith(DEFAULT_TEMPLATE_DIR)
                        || entry.getName().startsWith(DEFAULT_TEMPLATE_BASE_DIR))
                        && entry.getName().endsWith(DEFAULT_TEMPLATE_EXTENSION)) {
                        supportedLanguages.add(parseLanguage(entry.getName()));
                    }
                }
                jar.close();
            } catch (IOException e) {
                // left intentionally empty
            }
        }

        return supportedLanguages;
    }

    private String parseLanguage(String name) {
        String language = name.substring(DEFAULT_TEMPLATE_DIR.length());
        return language.substring(0, language.indexOf("/"));
    }

}
