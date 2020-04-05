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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.zkovari.eclipse.gitlab.core.GitLabProject;
import org.zkovari.eclipse.gitlab.core.ProjectMapping;
import org.zkovari.eclipse.gitlab.core.internal.TestGitLabClient;

public class ProjectMappingTest extends MockHttpClientTestBase {

    private static final String TEST_TOKEN = "testtoken";
    private static final String GITLAB_SERVER = "http://gitlab.com";

    private ProjectMapping mapping;

    @Rule
    public final TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public final ExpectedException thrown = ExpectedException.none();
    private IPath repoPath;

    @Override
    public void setUp() {
        super.setUp();
        TestGitLabClient testGitLabClient = assertTestGitLabClient();
        testGitLabClient.setHttpClient(mockHttpClient);

        mapping = new ProjectMapping();
        repoPath = new Path(temp.getRoot().getAbsolutePath());
    }

    private void assertGitLabProject(GitLabProject project) {
        assertNotNull(project);
        assertEquals("3", project.getId());
        assertEquals("Diaspora Project Site", project.getName());
        assertTrue("" + project.getPipelines(), project.getPipelines().isEmpty());
    }

    @Test
    public void testFindRepositoryIsNull() {
        IProject mockProject = mock(IProject.class);
        IPath repo = mapping.findRepositoryPath(mockProject);

        assertNull(repo);
        verifyZeroInteractions(mockHttpClient);
    }

    @Test
    public void testFindProjectIsNull() {
        GitLabProject project = mapping.findProject(repoPath);

        assertNull(project);
        verifyZeroInteractions(mockHttpClient);
    }

    @Test
    public void testGetOrCreateGitLabProject_whenPathIsNotAGitRepository() throws IOException {
        thrown.expect(IOException.class);
        thrown.expectMessage("Given path is not a Git repository: " + repoPath);
        mapping.getOrCreateGitLabProject(repoPath, TEST_TOKEN, GITLAB_SERVER);
    }

    @Test
    public void testGetOrCreateGitLabProject_whenRemoteUrlIsMissing() throws IOException, GitAPIException {
        Git.init().setDirectory(repoPath.toFile()).call();
        thrown.expect(IOException.class);
        thrown.expectMessage("Git remote url could not have been retrieved from local repository: " + repoPath);
        mapping.getOrCreateGitLabProject(repoPath, TEST_TOKEN, GITLAB_SERVER);
    }

    @Test
    public void testGetOrCreateGitLabProject() throws Exception {
        Git git = Git.init().setDirectory(repoPath.toFile()).call();
        git.getRepository().getConfig().setString("remote", "origin", "url", "git@gitlab.com:namespace/project.git");
        git.getRepository().getConfig().save();

        String projectResponse = loadResourceAsString("gitlab/responses/project-response.json");
        mockHttpGet(200, projectResponse);
        GitLabProject project = mapping.getOrCreateGitLabProject(repoPath, TEST_TOKEN, GITLAB_SERVER);

        assertGitLabProject(project);
        verify(mockHttpClient).execute(ArgumentMatchers.any());

        GitLabProject secondProject = mapping.getOrCreateGitLabProject(repoPath, TEST_TOKEN, GITLAB_SERVER);
        verifyNoMoreInteractions(mockHttpClient);
        assertEquals(project, secondProject);
    }

    @Test(timeout = 10000)
    public void testGetOrCreateGitLabProject_fromMultipleThreads() throws Exception {
        Git git = Git.init().setDirectory(repoPath.toFile()).call();
        git.getRepository().getConfig().setString("remote", "origin", "url", "git@gitlab.com:namespace/project.git");
        git.getRepository().getConfig().save();

        String projectResponse = loadResourceAsString("gitlab/responses/project-response.json");
        mockHttpGet(200, projectResponse);

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        Future<GitLabProject> future1 = executorService
                .submit(() -> mapping.getOrCreateGitLabProject(repoPath, TEST_TOKEN, GITLAB_SERVER));
        Future<GitLabProject> future2 = executorService
                .submit(() -> mapping.getOrCreateGitLabProject(repoPath, TEST_TOKEN, GITLAB_SERVER));
        Future<GitLabProject> future3 = executorService
                .submit(() -> mapping.getOrCreateGitLabProject(repoPath, TEST_TOKEN, GITLAB_SERVER));
        Thread.sleep(250);
        while (!future1.isDone() && !future2.isDone() && !future3.isDone()) {
            Thread.sleep(500);
            System.out.println("Waiting for threads to get result from call getOrCreateGitLabProject");
        }
        assertGitLabProject(future1.get());
        assertGitLabProject(future2.get());
        assertGitLabProject(future3.get());
        verify(mockHttpClient).execute(ArgumentMatchers.any());
    }

}
