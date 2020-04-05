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
package org.zkovari.eclipse.gitlab.core.test;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.stream.Collectors;

import javax.crypto.spec.PBEKeySpec;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.provider.IProviderHints;
import org.zkovari.eclipse.gitlab.core.Activator;
import org.zkovari.eclipse.gitlab.core.GitLabClient;
import org.zkovari.eclipse.gitlab.core.internal.TestGitLabClient;

public abstract class AbstractTestBase {

    protected String loadResourceAsString(String resourcePath) throws IOException {
        InputStream inputStream = GitLabClientTest.class.getClassLoader().getResourceAsStream(resourcePath);

        if (inputStream == null) {
            throw new IOException("Could not find resource " + resourcePath);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return br.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    protected TestGitLabClient assertTestGitLabClient() {
        GitLabClient gitLabClient = Activator.getDefault().getGitLabClient();
        assertTrue("Expected TestGitLabClient instance instead of " + gitLabClient.getClass().getName()
                + ". Please make sure you specify the environment variable org.zkovari.eclipse.gitlabServerEnvironment=UNIT_TEST",
                gitLabClient instanceof TestGitLabClient);
        return (TestGitLabClient) gitLabClient;
    }

    protected ISecurePreferences setUpSecureStore(File folder) throws IOException {
        HashMap<String, Object> options = new HashMap<>();
        PBEKeySpec keySpec = new PBEKeySpec("masterpass".toCharArray());
        options.put(IProviderHints.DEFAULT_PASSWORD, keySpec);
        File secureStoreFile = Files.createFile(folder.toPath().resolve("secure_store")).toFile();
        return SecurePreferencesFactory.open(secureStoreFile.toURI().toURL(), options);
    }

}
