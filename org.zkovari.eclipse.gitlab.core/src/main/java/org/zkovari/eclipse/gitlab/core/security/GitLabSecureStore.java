/*******************************************************************************
 * Copyright 2019-2020 Zsolt Kovari
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
package org.zkovari.eclipse.gitlab.core.security;

import java.util.Optional;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;

public class GitLabSecureStore {

    private static final String GITLAB_SECURE_STORE_TOKEN_KEY = "token";
    private static final String GITLAB_SECURE_STORE_PATH_NAME = "org.zkovari.eclipse.gitlab";

    public void storeToken(ISecurePreferences preferences, String token) throws SecureStoreException {
        ISecurePreferences node = getNode(preferences);
        try {
            node.put(GITLAB_SECURE_STORE_TOKEN_KEY, token, true);
        } catch (StorageException ex) {
            throw new SecureStoreException(
                    "Encryption failed while storing GitLab token in secure storage: " + ex.getMessage(), ex);
        }
    }

    public Optional<String> getToken(ISecurePreferences preferences) throws SecureStoreException {
        ISecurePreferences node = getNode(preferences);
        try {
            String token = node.get(GITLAB_SECURE_STORE_TOKEN_KEY, null);
            return Optional.ofNullable(token);
        } catch (StorageException ex) {
            throw new SecureStoreException(
                    "Decryption failed while retrieving GitLab token from secure storage: " + ex.getMessage(), ex);
        }
    }

    private ISecurePreferences getNode(ISecurePreferences preferences) throws SecureStoreException {
        try {
            return preferences.node(GITLAB_SECURE_STORE_PATH_NAME);
        } catch (IllegalStateException ex) {
            throw new SecureStoreException("The following node was already removed from secure storage: "
                    + GITLAB_SECURE_STORE_TOKEN_KEY + ". Reason: " + ex.getMessage(), ex);
        }
    }

}
