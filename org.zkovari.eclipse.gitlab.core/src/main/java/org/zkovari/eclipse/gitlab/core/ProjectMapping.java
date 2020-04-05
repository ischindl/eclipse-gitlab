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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.egit.core.project.RepositoryMapping;
import org.eclipse.jgit.api.Git;

@SuppressWarnings("restriction")
public class ProjectMapping {

    private final Map<IPath, GitLabProject> projectMapping;

    public ProjectMapping() {
        projectMapping = new HashMap<>();
    }

    public IPath findRepositoryPath(IProject project) {
        RepositoryMapping mapping = RepositoryMapping.getMapping(project);
        if (mapping == null) {
            return null;
        }

        return mapping.getGitDirAbsolutePath();
    }

    public GitLabProject findProject(IPath path) {
        return projectMapping.get(path);
    }

    public GitLabProject getOrCreateGitLabProject(IPath path, String token) throws IOException {
        Objects.requireNonNull(path);
        Objects.requireNonNull(token);
        if (projectMapping.containsKey(path)) {
            return projectMapping.get(path);
        }

        synchronized (this) {
            if (projectMapping.containsKey(path)) {
                return projectMapping.get(path);
            }

            String url;
            try (Git git = Git.open(path.toFile())) {
                url = git.getRepository().getConfig().getString("remote", "origin", "url");
            } catch (IOException ex) {
                throw new IOException("Given path is not a Git repository: " + path + ". Reason: " + ex.getMessage(),
                        ex);
            }

            if (url == null) {
                throw new IOException("Git remote url could not have been retrieved from local repository: " + path);
            }

            String projectPath = GitLabUtils.getProjectPath(url);

            GitLabClient gitLabClient = Activator.getInstance().getGitLabClient();
            GitLabProject gitLabProject = gitLabClient.getProject(GitLabUtils.getServerUrl(), token, projectPath);
            projectMapping.put(path, gitLabProject);

            return gitLabProject;
        }

    }

}
