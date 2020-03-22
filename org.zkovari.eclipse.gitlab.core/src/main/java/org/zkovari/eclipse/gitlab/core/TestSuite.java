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

@XmlRootElement(name = "testsuite")
public class TestSuite {

    private String name;

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

    @SerializedName("test_cases")
    private List<TestCase> testCases;

    public TestSuite() {
        testCases = new ArrayList<>();
    }

    @XmlAttribute(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @XmlElement(name = "testcase")
    public List<TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
    }

    @Override
    public String toString() {
        return "TestSuite [name=" + name + ", totalTime=" + totalTime + ", totalCount=" + totalCount + ", successCount="
                + successCount + ", failedCount=" + failedCount + ", skippedCount=" + skippedCount + ", errorCount="
                + errorCount + ", testCases=" + testCases + "]";
    }

}
