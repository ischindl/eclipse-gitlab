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
package org.zkovari.eclipse.gitlab.ui.views.labels;

import java.util.function.Function;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.widgets.Display;

public class StyledCellLinkLabelProvider<T> extends StyledCellLabelProvider {

    private Function<T, String> function;

    public StyledCellLinkLabelProvider(Function<T, String> function) {
        this.function = function;
    }

    @Override
    public void update(ViewerCell cell) {
        @SuppressWarnings("unchecked")
        String link = function.apply((T) cell.getElement());
        cell.setText(link);

        StyleRange refStyledRange = new StyleRange(0, link.length(),
                Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE), null);
        refStyledRange.underline = true;
        StyleRange[] range = { refStyledRange };
        cell.setStyleRanges(range);

        super.update(cell);
    }

}
