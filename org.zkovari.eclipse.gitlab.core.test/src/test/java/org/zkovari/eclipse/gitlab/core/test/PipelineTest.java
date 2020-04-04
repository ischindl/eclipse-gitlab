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
package org.zkovari.eclipse.gitlab.core.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.zkovari.eclipse.gitlab.core.Pipeline;

public class PipelineTest {

    private PropertyChangeListener mockListener;
    private ArgumentCaptor<PropertyChangeEvent> argumentCaptor;
    private Pipeline pipeline;

    @Before
    public void setUp() {
        pipeline = new Pipeline();
        mockListener = Mockito.mock(PropertyChangeListener.class);
        argumentCaptor = ArgumentCaptor.forClass(PropertyChangeEvent.class);
    }

    private void verifyPropertyChangeEvent(PropertyChangeEvent propertyChangeEvent, String key, String oldValue,
            String newValue) {
        assertNotNull(propertyChangeEvent);
        assertEquals(key, propertyChangeEvent.getPropertyName());
        assertEquals(oldValue, propertyChangeEvent.getOldValue());
        assertEquals(newValue, propertyChangeEvent.getNewValue());
    }

    @Test
    public void testPropertyChangeForStatus() {
        pipeline.addPropertyChangeListener(mockListener);
        pipeline.setStatus("oldValue");
        pipeline.setStatus("newValue");

        verify(mockListener, times(2)).propertyChange(argumentCaptor.capture());
        verifyPropertyChangeEvent(argumentCaptor.getAllValues().get(0), "status", null, "oldValue");
        verifyPropertyChangeEvent(argumentCaptor.getAllValues().get(1), "status", "oldValue", "newValue");
        assertEquals("newValue", pipeline.getStatus());
    }

    @Test
    public void testPropertyChangeForSha() {
        pipeline.addPropertyChangeListener(mockListener);
        pipeline.setSha("oldValue");
        pipeline.setSha("newValue");

        verify(mockListener, times(2)).propertyChange(argumentCaptor.capture());
        verifyPropertyChangeEvent(argumentCaptor.getAllValues().get(0), "sha", null, "oldValue");
        verifyPropertyChangeEvent(argumentCaptor.getAllValues().get(1), "sha", "oldValue", "newValue");
        assertEquals("newValue", pipeline.getSha());
    }

    @Test
    public void testPropertyChangeWithoutListener() {
        pipeline.setStatus("newStatus");
        pipeline.setSha("newSha");

        verifyZeroInteractions(mockListener);
        assertEquals("newStatus", pipeline.getStatus());
        assertEquals("newSha", pipeline.getSha());
    }

    @Test
    public void testPropertyChangeWithRemovedListener() {
        pipeline.addPropertyChangeListener(mockListener);
        pipeline.removePropertyChangeListener(mockListener);

        pipeline.setStatus("newStatus");
        pipeline.setSha("newSha");

        verifyZeroInteractions(mockListener);
        assertEquals("newStatus", pipeline.getStatus());
        assertEquals("newSha", pipeline.getSha());
    }

    @Test
    public void testEmptyJobs() {
        assertNotNull(pipeline.getJobs());
        assertTrue(pipeline.getJobs().isEmpty());
    }

    @Test
    public void testToStringWithUninitizalizedValues() {
        assertNotNull(pipeline.toString());
    }

}
