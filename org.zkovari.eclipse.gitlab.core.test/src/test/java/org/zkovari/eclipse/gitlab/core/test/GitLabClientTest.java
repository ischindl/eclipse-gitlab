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
package org.zkovari.eclipse.gitlab.core.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
        assertEquals("3d09c47d3bd06f6cec383e92e9dc0fb34f063368", firstPipeline.getSha());
    }

    @Test
    public void testGetProject() throws IOException {
        String projectResponse = loadResourceAsString("gitlab/responses/project-response.json");
        mockHttpGet(200, projectResponse);

        GitLabProject project = client.getProject("https://non-existing", "", "project_path");

        assertNotNull("Parsed project is null", project);
        assertEquals("3", project.getId());
        assertEquals("Diaspora Project Site", project.getName());
        assertNotNull(project.getPipelines());
        assertTrue(project.getPipelines().isEmpty());
    }

//    @Test
//    public void testGetPipelineJobs() throws IOException {
//        String projectResponse = loadResourceAsString("gitlab/responses/jobs-response.json");
//        mockHttpGet(200, projectResponse);
//
//        List<Job> jobs = client.getPipelineJobs("https://non-existing", "", new Pipeline(), new GitLabProject());
//
//        assertNotNull("Parsed jobs list is null", jobs);
//        assertEquals(2, jobs.size());
//        assertEquals("6", jobs.get(0).getId());
//        assertEquals("rspec:other", jobs.get(0).getName());
//        assertNotNull(jobs.get(0).getArtifacts());
//        assertTrue(jobs.get(0).getArtifacts().isEmpty());
//
//        assertEquals("7", jobs.get(1).getId());
//        assertEquals("teaspoon", jobs.get(1).getName());
//        assertNotNull(jobs.get(1).getArtifacts());
//        assertEquals(4, jobs.get(1).getArtifacts().size());
//
//        assertEquals("archive", jobs.get(1).getArtifacts().get(0).getType());
//        assertEquals("artifacts.zip", jobs.get(1).getArtifacts().get(0).getFilename());
//        assertEquals("zip", jobs.get(1).getArtifacts().get(0).getFormat());
//
//        assertEquals("metadata", jobs.get(1).getArtifacts().get(1).getType());
//        assertEquals("metadata.gz", jobs.get(1).getArtifacts().get(1).getFilename());
//        assertEquals("gzip", jobs.get(1).getArtifacts().get(1).getFormat());
//
//        assertEquals("trace", jobs.get(1).getArtifacts().get(2).getType());
//        assertEquals("job.log", jobs.get(1).getArtifacts().get(2).getFilename());
//        assertEquals("raw", jobs.get(1).getArtifacts().get(2).getFormat());
//
//        assertEquals("junit", jobs.get(1).getArtifacts().get(3).getType());
//        assertEquals("junit.xml.gz", jobs.get(1).getArtifacts().get(3).getFilename());
//        assertEquals("gzip", jobs.get(1).getArtifacts().get(3).getFormat());
//    }

}
