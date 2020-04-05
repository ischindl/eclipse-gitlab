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

import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.junit.Before;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

public class MockHttpClientTestBase extends AbstractTestBase {

    protected HttpClient mockHttpClient;
    protected HttpResponse response;
    protected StatusLine statusline;

    @Before
    public void setUp() {
        mockHttpClient = Mockito.mock(HttpClient.class);
        response = Mockito.mock(HttpResponse.class);
        statusline = Mockito.mock(StatusLine.class);
    }

    protected void mockHttpGet(int statusCode, String responseBody) throws IOException, ClientProtocolException {
        when(mockHttpClient.execute(ArgumentMatchers.any(HttpGet.class))).thenReturn(response);
        when(statusline.getStatusCode()).thenReturn(statusCode);
        when(response.getStatusLine()).thenReturn(statusline);
        when(response.getEntity()).thenReturn(new InputStreamEntity(new ByteArrayInputStream(responseBody.getBytes())));
    }

    protected void mockHttpPost(int statusCode, String responseBody) throws IOException, ClientProtocolException {
        when(mockHttpClient.execute(ArgumentMatchers.any(HttpPost.class))).thenReturn(response);
        when(statusline.getStatusCode()).thenReturn(statusCode);
        when(response.getStatusLine()).thenReturn(statusline);
        when(response.getEntity()).thenReturn(new InputStreamEntity(new ByteArrayInputStream(responseBody.getBytes())));
    }

}
