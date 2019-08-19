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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.zkovari.eclipse.gitlab.core.GitLabClient;
import org.zkovari.eclipse.gitlab.core.GitLabProject;
import org.zkovari.eclipse.gitlab.core.Pipeline;

public class GitLabClientTest extends MockHttpClientTestBase {

    private GitLabClient client;

    @Override
    public void setUp() {
        super.setUp();

        client = new GitLabClient(mockHttpClient);
    }

    @Test
    public void testGetPipelines() throws IOException {
        String pipelinesResponse = loadResourceAsString("gitlab/responses/pipelines-response.json");
        mockHttpGet(200, pipelinesResponse);

        List<Pipeline> pipelines = client.getPipelines("https://non-existing", "", new GitLabProject());
        assertNotNull("Parsed pipelines list is null", pipelines);
        assertFalse("Parsed pipelines list is empty", pipelines.isEmpty());
        assertEquals("Parsed pipelines list's size", 20, pipelines.size());

        Pipeline firstPipeline = pipelines.get(0);
        assertEquals("failed", firstPipeline.getStatus());
        assertEquals("74595935", firstPipeline.getId());
        assertEquals("3d09c47d3bd06f6cec383e92e9dc0fb34f063368", firstPipeline.getSha());
        assertEquals("master", firstPipeline.getRef());
        assertEquals("https://gitlab.com/zkovari/eclipse-gitlab/pipelines/74595935", firstPipeline.getWebUrl());
    }

}
