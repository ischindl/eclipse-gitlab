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
package org.zkovari.eclipse.gitlab.ui.views;

import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.zkovari.eclipse.gitlab.ui.GitLabUIPlugin;

public class CellImageDrawLabelProvider extends OwnerDrawLabelProvider {

    public static final int PADDING_X = 5;
    public static final int PADDING_Y = 5;

    private final String resourceImagePath;
    private Image image;

    public CellImageDrawLabelProvider(String resourceImagePath) {
        this.resourceImagePath = resourceImagePath;
    }

    @Override
    protected void measure(Event event, Object element) {
        image = GitLabUIPlugin.getImageDescriptor(resourceImagePath).createImage();
    }

    @Override
    protected void paint(Event event, Object element) {
        Rectangle bounds = event.getBounds();
        event.gc.drawImage(image, bounds.x + PADDING_X, bounds.y + PADDING_Y);
    }

}
