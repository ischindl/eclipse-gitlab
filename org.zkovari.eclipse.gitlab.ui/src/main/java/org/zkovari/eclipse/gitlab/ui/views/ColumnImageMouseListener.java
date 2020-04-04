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

import static org.zkovari.eclipse.gitlab.ui.views.CellImageDrawLabelProvider.PADDING_X;
import static org.zkovari.eclipse.gitlab.ui.views.CellImageDrawLabelProvider.PADDING_Y;

import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.zkovari.eclipse.gitlab.ui.Activator;

public class ColumnImageMouseListener implements MouseListener, MouseMoveListener, MouseTrackListener {

    private static final Cursor CURSOR_ARROW = new Cursor(Activator.getDefault().getWorkbench().getDisplay(),
            SWT.CURSOR_ARROW);
    private static final Cursor CURSOR_HAND = new Cursor(Activator.getDefault().getWorkbench().getDisplay(),
            SWT.CURSOR_HAND);
    private final TableViewerColumn tableViewerColumn;
    private final int columnIndex;
    private final CellMouseListenerAction action;

    public ColumnImageMouseListener(TableViewerColumn tableViewerColumn, int columnIndex,
            CellMouseListenerAction action) {
        this.tableViewerColumn = tableViewerColumn;
        this.columnIndex = columnIndex;
        this.action = action;
    }

    @Override
    public void mouseDoubleClick(MouseEvent event) {
        //
    }

    @Override
    public void mouseDown(MouseEvent event) {
        Point point = new Point(event.x, event.y);
        ViewerCell cell = tableViewerColumn.getViewer().getCell(point);

        if (event.button == 1 && cell != null && cell.getColumnIndex() == columnIndex) {
            Rectangle cellBounds = cell.getBounds();
            // TODO get rect from image' actual position in cell
            Rectangle imageRect = new Rectangle(cellBounds.x + PADDING_X, cellBounds.y + PADDING_Y, 16, 16);
            if (imageRect.contains(point)) {
                action.run(cell);
            }
        }

    }

    @Override
    public void mouseUp(MouseEvent event) {
        //
    }

    @Override
    public void mouseMove(MouseEvent event) {
        Point point = new Point(event.x, event.y);
        ViewerCell cell = tableViewerColumn.getViewer().getCell(point);
        if (cell != null && cell.getColumnIndex() == columnIndex) {
            Rectangle cellBounds = cell.getBounds();
            // TODO get rect from image' actual position in cell
            Rectangle imageRect = new Rectangle(cellBounds.x + PADDING_X, cellBounds.y + PADDING_Y, 16, 16);
            if (imageRect.contains(point)) {
                setCursor(CURSOR_HAND);
            } else {
                setCursor(CURSOR_ARROW);
            }
        }

    }

    @Override
    public void mouseEnter(MouseEvent event) {
        //
    }

    @Override
    public void mouseExit(MouseEvent event) {
        setCursor(CURSOR_ARROW);
    }

    @Override
    public void mouseHover(MouseEvent event) {
        //
    }

    private void setCursor(Cursor cursor) {
        Control cursorControle = Activator.getDefault().getWorkbench().getDisplay().getCursorControl();
        if (cursorControle != null) {
            cursorControle.getShell().setCursor(cursor);
        }
    }

}
