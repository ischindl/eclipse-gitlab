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

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.zkovari.eclipse.gitlab.core.Activator;
import org.zkovari.eclipse.gitlab.core.GitLabClient;
import org.zkovari.eclipse.gitlab.core.ProjectMapping;
import org.zkovari.eclipse.gitlab.core.security.GitLabSecureStore;

public class ActivatorTest {

    @Test
    public void testGetInstances() {
        Activator instance = Activator.getInstance();
        assertNotNull(instance);

        GitLabClient gitLabClient = instance.getGitLabClient();
        assertNotNull(gitLabClient);

        ProjectMapping projectMapping = instance.getProjectMapping();
        assertNotNull(projectMapping);

        GitLabSecureStore gitLabSecureStore = instance.getGitLabSecureStore();
        assertNotNull(gitLabSecureStore);
    }

}
