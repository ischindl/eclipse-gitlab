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
import org.zkovari.eclipse.gitlab.ui.views.labels.CellImageDrawLabelProvider;
import org.zkovari.eclipse.gitlab.ui.views.labels.ColumnTextLabelProvider;
import org.zkovari.eclipse.gitlab.ui.views.labels.pipeline.PipelineStatusImageLabelProvider;

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
        statusColumnViewer = createTableViewerColumn("Status", 60);
        webRefColumnViewer = createTableViewerColumn("URL", 40);
        refColumnViewer = createTableViewerColumn("Commit", 100);
        durationColumnViewer = createTableViewerColumn("Duration", 65);
        createdAtColumnViewer = createTableViewerColumn("Last updated", 100);
        coverageColumnViewer = createTableViewerColumn("Coverage", 80);
        artifactsColumnViewer = createTableViewerColumn("", 40);

        statusColumnViewer.setLabelProvider(new PipelineStatusImageLabelProvider());
        webRefColumnViewer.setLabelProvider(new CellImageDrawLabelProvider(
                "platform:/plugin/org.eclipse.ui.browser/icons/obj16/external_browser.png"));

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

        durationColumnViewer.setLabelProvider(
                new ColumnTextLabelProvider<Pipeline>(pipeline -> Integer.toString(pipeline.getDuration())));

        createdAtColumnViewer.setLabelProvider(new ColumnTextLabelProvider<Pipeline>(Pipeline::getUpdatedAt));

        coverageColumnViewer.setLabelProvider(
                new ColumnTextLabelProvider<Pipeline>(pipeline -> Double.toString(pipeline.getCoverage())));

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
