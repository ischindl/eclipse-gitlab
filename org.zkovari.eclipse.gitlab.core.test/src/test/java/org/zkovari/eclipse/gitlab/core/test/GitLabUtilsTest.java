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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URISyntaxException;
import java.util.Optional;

import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.zkovari.eclipse.gitlab.core.GitLabUtils;

public class GitLabUtilsTest {

    @ParameterizedTest
    // @formatter:off
	@ValueSource(strings = { 
	    "http://gitlab.com/namespace/project.git",
	    "https://gitlab.com/namespace/project.git",
	    "ssh://user@host.xz:21/namespace/project.git/",
	    "ssh://user@host.xz/namespace/project.git/",
	    "ssh://host.xz:21/namespace/project.git/",
	    "ssh://user@host.xz:21/namespace/project.git/",
	    "git@gitlab.com:namespace/project.git",
	    "user@host.xy:namespace/project.git",
	    "git://host.xz/namespace/project.git/",
	    
	    // CERN
	    "https://gitlab.cern.ch/namespace/project.git",
	    "https://:@gitlab.cern.ch:8443/namespace/project.git",
	    "ssh://git@gitlab.cern.ch:7999/namespace/project.git"
	})
	// @formatter:on
    public void testGetProjectPath(String urlStr) throws URISyntaxException {
        String projectPath = GitLabUtils.getProjectPath(urlStr);
        assertEquals("namespace/project", projectPath);
    }

    @ParameterizedTest
    // @formatter:off
    @ValueSource(strings = { 
        "http://gitlab.com/namespace/nested/project.git",
        "https://gitlab.com/namespace/nested/project.git",
        "git@gitlab.com:namespace/nested/project.git",
        
        // CERN
        "https://gitlab.cern.ch/namespace/nested/project.git",
        "https://:@gitlab.cern.ch:8443/namespace/nested/project.git",
        "ssh://git@gitlab.cern.ch:7999/namespace/nested/project.git"
    })
    public void testGetProjectNestedPath(String urlStr) throws URISyntaxException {
        String projectPath = GitLabUtils.getProjectPath(urlStr);
        assertEquals("namespace/nested/project", projectPath);
    }

    @Test
    public void testGetGitLabToken() {
        Optional<String> token = GitLabUtils.getToken();
        assertNotNull(token);
    }

}
