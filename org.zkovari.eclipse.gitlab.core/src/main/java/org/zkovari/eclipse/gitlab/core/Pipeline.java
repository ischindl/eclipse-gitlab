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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Pipeline {

    @SerializedName("status")
    private String status;

    @SerializedName("detailedStatus")
    private PipelineDetailedStatus detailedStatus;

    @SerializedName("sha")
    private String sha;

    @SerializedName("updatedAt")
    private String updatedAt;

    @SerializedName("coverage")
    private double coverage;

    @SerializedName("duration")
    private int duration;

    private TestReport testReport;
    private List<Job> jobs;

    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public double getCoverage() {
        return coverage;
    }

    public void setCoverage(double coverage) {
        this.coverage = coverage;
    }

    public int getDuration() {
        return duration;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Pipeline() {
        jobs = new ArrayList<>();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        changeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        firePropertyChange("status", this.status, this.status = status);
    }

    public PipelineDetailedStatus getDetailedStatus() {
        return detailedStatus;
    }

    public void setDetailedStatus(PipelineDetailedStatus detailedStatus) {
        this.detailedStatus = detailedStatus;
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        firePropertyChange("sha", this.sha, this.sha = sha);
    }

    public TestReport getTestReport() {
        return testReport;
    }

    public void setTestReport(TestReport testReport) {
        this.testReport = testReport;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    @Override
    public String toString() {
        return "Pipeline [status=" + status + ", detailedStatus=" + detailedStatus + ", sha=" + sha + ", updatedAt="
                + updatedAt + ", coverage=" + coverage + ", duration=" + duration + "]";
    }

}
