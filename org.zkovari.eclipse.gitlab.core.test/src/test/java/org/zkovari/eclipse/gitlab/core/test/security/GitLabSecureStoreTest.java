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
package org.zkovari.eclipse.gitlab.core.test.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.HashMap;
import java.util.Optional;

import javax.crypto.spec.PBEKeySpec;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.provider.IProviderHints;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.zkovari.eclipse.gitlab.core.security.GitLabSecureStore;
import org.zkovari.eclipse.gitlab.core.security.SecureStoreException;

public class GitLabSecureStoreTest {

	@Rule
	public final TemporaryFolder temp = new TemporaryFolder();

	private GitLabSecureStore gitLabSecureStore;
	private ISecurePreferences secureStore;

	@Before
	public void setUp() throws Exception {
		HashMap<String, Object> options = new HashMap<>();
		PBEKeySpec keySpec = new PBEKeySpec("masterpass".toCharArray());
		options.put(IProviderHints.DEFAULT_PASSWORD, keySpec);
		File secureStoreFile = temp.newFile("secure_store");
		secureStore = SecurePreferencesFactory.open(secureStoreFile.toURI().toURL(), options);
		gitLabSecureStore = new GitLabSecureStore();
	}

	@Test
	public void testStoreToken() throws SecureStoreException {
		gitLabSecureStore.storeToken(secureStore, "testtoken");
		Optional<String> tokenOpt = gitLabSecureStore.getToken(secureStore);
		assertTrue("Expected token but it was empty", tokenOpt.isPresent());
		assertEquals("testtoken", tokenOpt.get());
	}

	@Test
	public void testRetrieveEmptyToken() throws SecureStoreException {
		Optional<String> tokenOpt = gitLabSecureStore.getToken(secureStore);
		if (tokenOpt.isPresent()) {
			fail("Expected empty token but got " + tokenOpt.get());
		}
	}
}
