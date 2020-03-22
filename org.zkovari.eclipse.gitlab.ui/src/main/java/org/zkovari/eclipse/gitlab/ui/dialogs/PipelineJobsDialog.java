///*******************************************************************************
// * Copyright 2020 Zsolt Kovari
// * 
// * Licensed under the Apache License, Version 2.0 (the "License"); you may not
// * use this file except in compliance with the License.  You may obtain a copy
// * of the License at
// * 
// *   http://www.apache.org/licenses/LICENSE-2.0
// * 
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
// * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
// * License for the specific language governing permissions and limitations under
// * the License.
// ******************************************************************************/
//package org.zkovari.eclipse.gitlab.ui.dialogs;
//
//import java.io.IOException;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Optional;
//
//import org.eclipse.core.databinding.observable.list.IObservableList;
//import org.eclipse.core.databinding.property.Properties;
//import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
//import org.eclipse.jface.dialogs.TitleAreaDialog;
//import org.eclipse.jface.viewers.ColumnLabelProvider;
//import org.eclipse.jface.viewers.TableViewer;
//import org.eclipse.jface.viewers.TableViewerColumn;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.graphics.Point;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Control;
//import org.eclipse.swt.widgets.Shell;
//import org.eclipse.swt.widgets.TableColumn;
//import org.zkovari.eclipse.gitlab.core.GitLabClient;
//import org.zkovari.eclipse.gitlab.core.GitLabProject;
//import org.zkovari.eclipse.gitlab.core.GitLabUtils;
//import org.zkovari.eclipse.gitlab.core.Job;
//import org.zkovari.eclipse.gitlab.core.Pipeline;
//import org.zkovari.eclipse.gitlab.core.TestReport;
//
//public class PipelineJobsDialog extends TitleAreaDialog {
//
//    private final Pipeline pipeline;
//    private final GitLabProject project;
//    private TableViewer tableViewer;
//
//    public PipelineJobsDialog(Shell parentShell, GitLabProject project, Pipeline pipeline) {
//        super(parentShell);
//        this.project = project;
//        this.pipeline = pipeline;
//    }
//
//    @Override
//    public void create() {
//        super.create();
//        setTitle("Pipeline Jobs for " + project.getName());
//    }
//
//    @Override
//    protected boolean isResizable() {
//        return true;
//    }
//
//    @Override
//    protected Control createDialogArea(Composite parent) {
//        Composite container = (Composite) super.createDialogArea(parent);
//
//        Optional<String> token = GitLabUtils.getToken();
//        List<Job> pipelineJobs = new LinkedList<>();
//        GitLabClient gitLabClient = new GitLabClient();
//        try {
//            pipelineJobs.addAll(gitLabClient.getPipelineJobs("https://gitlab.com", token.get(), pipeline, project));
//            TestReport testReport = gitLabClient.getPipelineTestReports("https://gitlab.com", token.get(), project,
//                    pipeline);
//        } catch (IOException e1) {
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }
//
//        tableViewer = new TableViewer(container,
//                SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.FILL);
//        createColumns();
//        tableViewer.getTable().setHeaderVisible(true);
//        tableViewer.getTable().setLinesVisible(true);
//
//        tableViewer.setContentProvider(new ObservableListContentProvider());
//        IObservableList input = Properties.<Job>selfList(Pipeline.class).observe(pipelineJobs);
//        tableViewer.setInput(input);
//
//        return container;
//    }
//
//    private void createColumns() {
//        String[] titles = { "Name", "ID", "Artifacts" };
//        int[] bounds = { 150, 100, 200 };
//
//        TableViewerColumn columnViewer = createTableViewerColumn(titles[0], bounds[0]);
//        columnViewer.setLabelProvider(new ColumnLabelProvider() {
//            @Override
//            public String getText(Object element) {
//                Job job = (Job) element;
//                return job.getName();
//            }
//        });
//
//        columnViewer = createTableViewerColumn(titles[1], bounds[1]);
//        columnViewer.setLabelProvider(new ColumnLabelProvider() {
//            @Override
//            public String getText(Object element) {
//                Job job = (Job) element;
//                return job.getId();
//            }
//        });
//
//        columnViewer = createTableViewerColumn(titles[2], bounds[2]);
//        columnViewer.setLabelProvider(new ColumnLabelProvider() {
//            @Override
//            public String getText(Object element) {
//                Job job = (Job) element;
//                boolean hasArchivesArtifact = job.getArtifacts().stream()
//                        .anyMatch(artifact -> "archive".equals(artifact.getType()));
//                if (hasArchivesArtifact) {
//                    return "Artifacts";
//                }
//                return "";
//            }
//        });
//    }
//
//    private TableViewerColumn createTableViewerColumn(String title, int bound) {
//        final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
//        final TableColumn column = viewerColumn.getColumn();
//        column.setText(title);
//        column.setWidth(bound);
//        column.setResizable(true);
//        column.setMoveable(true);
//        return viewerColumn;
//    }
//
//    @Override
//    protected Point getInitialSize() {
//        return new Point(650, 300);
//    }
//
//}
