/*******************************************************************************
 * Copyright 2019 Zsolt Kovari
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.zkovari.eclipse.gitlab.core;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.zkovari.eclipse.gitlab.core.security.SecureStoreException;

public class GitLabUtils {

    private static final Pattern URL_PATH_PATTERN = Pattern
            .compile("(\\w+((:\\/\\/)|(@)))(.+@)*([\\w\\d\\.]+)(:[\\d]+){0,1}\\/*(:?)");

    private GitLabUtils() {
        throw new UnsupportedOperationException("Static class");
    }

    public static String getProjectPath(String urlString) {
        String path;
        try {
            path = new URL(urlString).getPath();
        } catch (MalformedURLException e) {
            path = findPathManually(urlString);
        }
        if (path == null) {
            throw new IllegalArgumentException("Cannot extract project path from url:" + urlString);
        }
        return path.replaceFirst("\\.git/*$", "").replaceFirst("^/", "");
    }

    private static String findPathManually(String urlString) {
        Matcher matcher = URL_PATH_PATTERN.matcher(urlString);
        if (matcher.find()) {
            return urlString.replaceFirst(matcher.group(0), "");
        }
        return null;
    }

    public static Optional<String> getToken() {
        try {
            return Activator.getDefault().getGitLabSecureStore().getToken(SecurePreferencesFactory.getDefault());
        } catch (SecureStoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return Optional.empty();
    }

}
