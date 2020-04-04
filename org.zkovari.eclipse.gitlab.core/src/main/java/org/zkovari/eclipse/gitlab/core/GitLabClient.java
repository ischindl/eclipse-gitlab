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

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class GitLabClient {

    private static final String PRIVATE_TOKEN_HEADER = "PRIVATE-TOKEN";

    protected HttpClient client;
    private Gson gson;
    private ResponseHandler<String> responseHandler;

    void initFields(HttpClient httpClient) {
        this.client = httpClient;
        gson = new Gson();
        responseHandler = new BasicResponseHandler();
    }

    public GitLabClient(HttpClient httpClient) {
        initFields(httpClient);
    }

    public GitLabClient() {
        initFields(HttpClients.createDefault());
    }

    public List<Pipeline> getPipelines(String serverUrl, String token, GitLabProject project) throws IOException {
        HttpPost httpPost = new HttpPost(serverUrl + "/api/graphql");
        httpPost.addHeader("Authorization", "Bearer " + token);
        httpPost.addHeader("Content-Type", "application/json");
        StringEntity requestEntity = new StringEntity(String.format(
                "{\"query\": \"query {project(fullPath: \\\"%s\\\") {pipelines(first: 5){"
                        + "nodes{coverage sha updatedAt duration status detailedStatus{group detailsPath} userPermissions{updatePipeline}}}}}\"}",
                project.getFullPath()), ContentType.APPLICATION_JSON);

        httpPost.setEntity(requestEntity);

        HttpResponse response = client.execute(httpPost);
        String responseJsonStr = responseHandler.handleResponse(response);
        JsonObject jsonObject = new JsonParser().parse(responseJsonStr).getAsJsonObject();
        JsonArray pipelinesJsonArray = jsonObject.getAsJsonObject("data").getAsJsonObject("project")
                .getAsJsonObject("pipelines").getAsJsonArray("nodes");
        return gson.fromJson(pipelinesJsonArray.toString(), new TypeToken<List<Pipeline>>() {
        }.getType());
    }

    public GitLabProject getProject(String serverUrl, String token, String projectPath) throws IOException {
        HttpGet httpGet = new HttpGet(
                serverUrl + "/api/v4/projects/" + URLEncoder.encode(projectPath, StandardCharsets.UTF_8.toString()));
        httpGet.addHeader(PRIVATE_TOKEN_HEADER, token);

        HttpResponse response = client.execute(httpGet);
        return gson.fromJson(responseHandler.handleResponse(response), GitLabProject.class);
    }

    public List<Job> getPipelineJobs(String serverUrl, String token, Pipeline pipeline, GitLabProject project)
            throws IOException {
        HttpGet httpGet = new HttpGet(serverUrl + "/api/v4/projects/"
                + URLEncoder.encode(pipeline.getDetailedStatus().getDetailsPath(), StandardCharsets.UTF_8.toString())
                + "/jobs");
        httpGet.addHeader(PRIVATE_TOKEN_HEADER, token);

        HttpResponse response = client.execute(httpGet);
        return gson.fromJson(responseHandler.handleResponse(response), new TypeToken<List<Job>>() {
        }.getType());
    }

    public TestReport getPipelineTestReports(String serverUrl, String token, Pipeline pipeline) throws IOException {
        HttpGet httpGet = new HttpGet(serverUrl + pipeline.getDetailedStatus().getDetailsPath() + "/test_report.json");

        httpGet.addHeader(PRIVATE_TOKEN_HEADER, token);
        HttpResponse response = client.execute(httpGet);

        return gson.fromJson(responseHandler.handleResponse(response), TestReport.class);
    }

}
