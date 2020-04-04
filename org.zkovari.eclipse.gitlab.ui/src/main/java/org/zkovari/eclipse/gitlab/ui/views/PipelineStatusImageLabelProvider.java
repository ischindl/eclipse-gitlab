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
import org.zkovari.eclipse.gitlab.core.Pipeline;
import org.zkovari.eclipse.gitlab.ui.Activator;

public class PipelineStatusImageLabelProvider extends OwnerDrawLabelProvider {

    private Image image;

    @Override
    protected void measure(Event event, Object element) {
        Pipeline pipeline = (Pipeline) element;
        switch (pipeline.getStatus()) {
        case "RUNNING":
            image = Activator
                    .getImageDescriptor("platform:/plugin/org.eclipse.mylyn.commons.ui/icons/eview16/progress/1.png")
                    .createImage();
            break;
        case "PENDING":
            image = Activator
                    .getImageDescriptor("platform:/plugin/org.eclipse.team.ui/icons/full/ovr/waiting_ovr@2x.png")
                    .createImage();
            break;
        case "SUCCESS":
            if (pipeline.getDetailedStatus().getGroup().contains("warning")) {
                image = Activator.getImageDescriptor(
                        "platform:/plugin/org.eclipse.jface/org/eclipse/jface/dialogs/images/message_warning.png")
                        .createImage();
            } else {
                image = Activator
                        .getImageDescriptor("platform:/plugin/org.eclipse.platform.doc.user/images/image92-check.png")
                        .createImage();
            }
            break;
        case "FAILED":
            image = Activator.getImageDescriptor("platform:/plugin/org.eclipse.jface/icons/full/message_error.png")
                    .createImage();
            break;
        case "CANCELED":
            image = Activator.getImageDescriptor("platform:/plugin/org.eclipse.ui.console/icons/full/elcl16/rem_co.png")
                    .createImage();
            break;
        case "SKIPPED":
        default:
            image = Activator
                    .getImageDescriptor("platform:/plugin/org.eclipse.ui.ide/icons/full/obj16/incomplete_tsk.png")
                    .createImage();
        }
    }

    @Override
    protected void paint(Event event, Object element) {
        if (image == null) {
            return;
        }
        Rectangle bounds = event.getBounds();
        event.gc.drawImage(image, bounds.x + 5, bounds.y + 5);
    }

}
