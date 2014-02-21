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

import java.io.File;

import org.jboss.arquillian.core.spi.Validate;

/**
 * @author <a href="smikloso@redhat.com">Stefan Miklosovic</a>
 *
 */
public class RecorderFileUtils {

    /**
     *
     * @param file file to check the file extension of
     * @param type type of file
     * @return the same file instance if {@code file} name ends with {@code type} string, dot included, otherwise new file
     *         instance with the appended {@code type} file.
     * @throws IllegalArgumentException if name of file is empty string or if file is null
     */
    public static File checkFileExtension(File file, ResourceType type) {
        Validate.notNull(file, "file to check can not be null");
        Validate.notNull(type, "type can not be null");

        if (file.getName().isEmpty()) {
            throw new IllegalArgumentException("File name you are going to save the image to can not be empty.");
        } else {
            if (!file.getName().endsWith("." + type.toString())) {
                file = new File(file.getParent(), file.getName().concat("." + type.toString()));
            }
        }
        return file;
    }

    public static void createDirectory(File file) {
        if (!file.exists()) {
            file.mkdirs();
        }
    }

}
