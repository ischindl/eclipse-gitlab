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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.google.gson.annotations.SerializedName;

public class Pipeline {

    private String id;
    @SerializedName("web_url")
    private String webUrl;
    private String status;
    private String ref;
    private String sha;

    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    public String getId() {
        return id;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        firePropertyChange("webUrl", this.webUrl, this.webUrl = webUrl);
    }

    public void setId(String id) {
        firePropertyChange("id", this.id, this.id = id);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        firePropertyChange("status", this.status, this.status = status);
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        firePropertyChange("ref", this.ref, this.ref = ref);
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        firePropertyChange("sha", this.sha, this.sha = sha);
    }

    @Override
    public String toString() {
        return "Pipeline [id=" + id + ", webUrl=" + webUrl + ", status=" + status + ", ref=" + ref + ", sha=" + sha
                + "]";
    }

}
