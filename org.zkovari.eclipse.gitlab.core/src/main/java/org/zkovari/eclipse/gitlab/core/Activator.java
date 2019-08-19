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

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.zkovari.eclipse.gitlab.core.internal.TestGitLabClient;
import org.zkovari.eclipse.gitlab.core.security.GitLabSecureStore;

public class Activator implements BundleActivator {

    private static BundleContext context;
    private static Activator plugin;

    private GitLabClient gitLabClient;
    private GitLabSecureStore gitLabSecureStore;
    private ProjectMapping projectMapping;

    static BundleContext getContext() {
        return context;
    }

    public Activator() {
        plugin = this;
    }

    /**
     * @return the singleton {@link Activator}
     */
    public static Activator getInstance() {
        return plugin;
    }

    @Override
    public void start(BundleContext bundleContext) throws Exception {
        Activator.context = bundleContext;

        if ("UNIT_TEST".equals(System.getenv("org.zkovari.eclipse.gitlabServerEnvironment"))) {
            gitLabClient = new TestGitLabClient();
        } else {
            gitLabClient = new GitLabClient();
        }

        gitLabSecureStore = new GitLabSecureStore();
        projectMapping = new ProjectMapping();
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        Activator.context = null;
    }

    public GitLabClient getGitLabClient() {
        return gitLabClient;
    }

    public GitLabSecureStore getGitLabSecureStore() {
        return gitLabSecureStore;
    }

    public ProjectMapping getProjectMapping() {
        return projectMapping;
    }

}
