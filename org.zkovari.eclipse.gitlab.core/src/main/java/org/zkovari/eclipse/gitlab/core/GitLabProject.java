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
package org.zkovari.eclipse.gitlab.core;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GitLabProject {

    private String id;
    private String name;

    @SerializedName("path_with_namespace")
    private String fullPath;
    private List<Pipeline> pipelines;

    public GitLabProject() {
        pipelines = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Pipeline> getPipelines() {
        return pipelines;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    @Override
    public String toString() {
        return "GitLabProject [id=" + id + ", name=" + name + ", fullPath=" + fullPath + ", pipelines=" + pipelines
                + "]";
    }

}
