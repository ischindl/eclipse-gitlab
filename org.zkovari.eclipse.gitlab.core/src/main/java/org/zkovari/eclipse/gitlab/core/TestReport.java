/*******************************************************************************
 * Copyright 2020 Zsolt Kovari
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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.google.gson.annotations.SerializedName;

@XmlRootElement(name = "testsuites")
public class TestReport {

    @SerializedName("total_time")
    private double totalTime;

    @SerializedName("total_count")
    private int totalCount;

    @SerializedName("success_count")
    private int successCount;

    @SerializedName("failed_count")
    private int failedCount;

    @SerializedName("skipped_count")
    private int skippedCount;

    @SerializedName("error_count")
    private int errorCount;

    @SerializedName("test_suites")
    private List<TestSuite> testSuites;

    public TestReport() {
        testSuites = new ArrayList<>();
    }

    @XmlAttribute(name = "time")
    public double getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(double totalTime) {
        this.totalTime = totalTime;
    }

    @XmlAttribute(name = "tests")
    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    @XmlTransient
    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    @XmlTransient
    public int getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    @XmlTransient
    public int getSkippedCount() {
        return skippedCount;
    }

    public void setSkippedCount(int skippedCount) {
        this.skippedCount = skippedCount;
    }

    @XmlAttribute(name = "failures")
    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    @XmlElement(name = "testsuite")
    public List<TestSuite> getTestSuites() {
        return testSuites;
    }

    public void setTestSuites(List<TestSuite> testSuites) {
        this.testSuites = testSuites;
    }

    @Override
    public String toString() {
        return "TestReport [totalTime=" + totalTime + ", totalCount=" + totalCount + ", successCount=" + successCount
                + ", failedCount=" + failedCount + ", skippedCount=" + skippedCount + ", errorCount=" + errorCount
                + ", testSuites=" + testSuites + "]";
    }

}
