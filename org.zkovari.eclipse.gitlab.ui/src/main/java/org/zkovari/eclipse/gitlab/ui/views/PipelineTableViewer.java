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

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.zkovari.eclipse.gitlab.core.Pipeline;

public class PipelineTableViewer extends TableViewer {

    private TableViewerColumn statusColumnViewer;
    private TableViewerColumn webRefColumnViewer;
    private TableViewerColumn refColumnViewer;
    private TableViewerColumn durationColumnViewer;
    private TableViewerColumn createdAtColumnViewer;
    private TableViewerColumn coverageColumnViewer;
    private TableViewerColumn artifactsColumnViewer;

    public PipelineTableViewer(Composite parent) {
        super(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        createColumns();

        getTable().setHeaderVisible(true);
        getTable().setLinesVisible(true);
    }

    public TableViewerColumn getStatusColumnViewer() {
        return statusColumnViewer;
    }

    public TableViewerColumn getWebRefColumnViewer() {
        return webRefColumnViewer;
    }

    public TableViewerColumn getRefColumnViewer() {
        return refColumnViewer;
    }

    public TableViewerColumn getDurationColumnViewer() {
        return durationColumnViewer;
    }

    public TableViewerColumn getCreatedAtColumnViewer() {
        return createdAtColumnViewer;
    }

    public TableViewerColumn getCoverageColumnViewer() {
        return coverageColumnViewer;
    }

    public TableViewerColumn getArtifactsColumnViewer() {
        return artifactsColumnViewer;
    }

    public int indexOf(TableViewerColumn columnViewer) {
        return getTable().indexOf(columnViewer.getColumn());
    }

    private void createColumns() {
        int minSize = 40;
        statusColumnViewer = createTableViewerColumn("Status", 60);
        statusColumnViewer.setLabelProvider(new PipelineStatusImageLabelProvider());

        webRefColumnViewer = createTableViewerColumn("URL", minSize);
        webRefColumnViewer.setLabelProvider(new CellImageDrawLabelProvider(
                "platform:/plugin/org.eclipse.ui.browser/icons/obj16/external_browser.png"));

        refColumnViewer = createTableViewerColumn("Commit", 100);
        refColumnViewer.setLabelProvider(new StyledCellLabelProvider() {

            @Override
            public void update(ViewerCell cell) {
                Pipeline pipeline = (Pipeline) cell.getElement();
                cell.setText(pipeline.getSha());

                StyleRange refStyledRange = new StyleRange(0, pipeline.getSha().length(),
                        Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE), null);
                refStyledRange.underline = true;
                StyleRange[] range = { refStyledRange };
                cell.setStyleRanges(range);

                super.update(cell);
            }

        });

        durationColumnViewer = createTableViewerColumn("Duration", 65);
        durationColumnViewer.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public String getText(Object element) {
                Pipeline pipeline = (Pipeline) element;
                return Integer.toString(pipeline.getDuration());
            }

        });

        createdAtColumnViewer = createTableViewerColumn("Last updated", 100);
        createdAtColumnViewer.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public String getText(Object element) {
                Pipeline pipeline = (Pipeline) element;
                return pipeline.getUpdatedAt();
            }

        });

        coverageColumnViewer = createTableViewerColumn("Coverage", 80);
        coverageColumnViewer.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public String getText(Object element) {
                Pipeline pipeline = (Pipeline) element;
                return Double.toString(pipeline.getCoverage());
            }

        });

        artifactsColumnViewer = createTableViewerColumn("", minSize);
        artifactsColumnViewer.setLabelProvider(
                new CellImageDrawLabelProvider("platform:/plugin/org.eclipse.jdt.junit/icons/full/eview16/junit.gif"));
    }

    private TableViewerColumn createTableViewerColumn(String title, int bound) {
        TableViewerColumn viewerColumn = new TableViewerColumn(this, SWT.NONE);
        TableColumn column = viewerColumn.getColumn();
        column.setText(title);
        column.setWidth(bound);
        column.setResizable(true);
        return viewerColumn;
    }

}
