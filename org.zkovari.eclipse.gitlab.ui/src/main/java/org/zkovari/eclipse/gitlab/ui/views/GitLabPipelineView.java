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
package org.zkovari.eclipse.gitlab.ui.views;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.property.Properties;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.zkovari.eclipse.gitlab.core.GitLabClient;
import org.zkovari.eclipse.gitlab.core.GitLabProject;
import org.zkovari.eclipse.gitlab.core.GitLabUtils;
import org.zkovari.eclipse.gitlab.core.Pipeline;
import org.zkovari.eclipse.gitlab.core.ProjectMapping;
import org.zkovari.eclipse.gitlab.core.TestReport;
import org.zkovari.eclipse.gitlab.ui.GitLabUIPlugin;
import org.zkovari.eclipse.gitlab.ui.dialogs.SecureTokenInputDialog;
import org.zkovari.eclipse.gitlab.ui.preferences.PreferenceConstants;

public class GitLabPipelineView extends ViewPart {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.zkovari.eclipse.gitlab.ui.views.GitLabPipelineView";

    @Inject
    private UISynchronize sync;

    private Composite composite;
    private TableViewer tableViewer;
    private Action refreshAction;
    private Link projectStatus;

    private GitLabProject displayedGitLabProject;
    private IPath repositoryPath;

    private final ProjectMapping projectMapping;
    private final List<Pipeline> displayedPipelines;
    private final TestReportDisplayer testReportDisplayer;
    private final GitLabClient gitLabClient;

    public GitLabPipelineView() {
        displayedPipelines = new ArrayList<>();
        projectMapping = org.zkovari.eclipse.gitlab.core.Activator.getDefault().getProjectMapping();
        testReportDisplayer = new TestReportDisplayer();
        gitLabClient = new GitLabClient();
    }

    @Override
    public void createPartControl(Composite parent) {
        composite = new Composite(parent, SWT.NONE);
        FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
        composite.setLayout(fillLayout);

        projectStatus = new Link(composite, SWT.NONE);
        projectStatus.setText("Select project...");
        addRepositoryBindingSelectionListener();
        addProjectSelectionListener();
        makeActions();
        contributeToActionBars();
    }

    @Override
    public void setFocus() {
        composite.setFocus();
    }

    private void addRepositoryBindingSelectionListener() {
        projectStatus.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Job job = Job.create("Update table", (ICoreRunnable) monitor -> {
                    try {
                        Optional<String> token = GitLabUtils.getToken();
                        if (!token.isPresent()) {
                            sync.asyncExec(() -> {
                                SecureTokenInputDialog dialog = new SecureTokenInputDialog(composite.getShell());
                                dialog.create();
                                dialog.open();
                            });
                            return;
                        }
                        displayedGitLabProject = projectMapping.getOrCreateGitLabProject(repositoryPath, token.get(),
                                getGitLabServer());
                        fetchPipelines();
                    } catch (IOException ex) {
                        displayedGitLabProject = null;
                        GitLabUIPlugin.showError(ex.getMessage());
                    }
                    sync.asyncExec(() -> {
                        if (tableViewer == null || tableViewer.getTable().isDisposed()) {
                            createTableViewer();
                            projectStatus.dispose();
                            composite.layout(true);
                        }
                    });
                });

                job.schedule();
            }
        });
    }

    @SuppressWarnings("rawtypes")
    private void createTableViewer() {
        tableViewer = new TableViewer(composite, SWT.H_SCROLL | SWT.V_SCROLL);
        createColumns();

        tableViewer.getTable().setHeaderVisible(true);
        tableViewer.getTable().setLinesVisible(true);

        // removed generic because it wasn't supported in previous Eclipse versions
        tableViewer.setContentProvider(new ObservableListContentProvider());
        IObservableList input = Properties.<Pipeline>selfList(Pipeline.class).observe(displayedPipelines);
        tableViewer.setInput(input);
        contributeToActionBars();
    }

    private void addProjectSelectionListener() {
        ISelectionService service = getSite().getService(ISelectionService.class);
        service.addSelectionListener((part, selection) -> {
            IProject project = findSelectedProject(selection);
            if (project == null) {
                return;
            }

            repositoryPath = projectMapping.findRepositoryPath(project);
            if (repositoryPath == null) {
                displayProjectStatusAndHideTable("Selected project is not an EGit repository: " + project.getName());
                return;
            }

            displayedGitLabProject = projectMapping.findGitLabProject(repositoryPath);
            if (displayedGitLabProject == null) {
                displayProjectStatusAndHideTable("<a>Bind project " + project.getName() + "</a>");
                return;
            }
            if (!projectStatus.isDisposed()) {
                projectStatus.dispose();
                composite.layout(true);
            }
            if (displayedGitLabProject.getPipelines().isEmpty()) {
                displayProjectStatusAndHideTable("Selected project does not have any pipelines: " + project.getName());
                return;
            }
            displayedPipelines.clear();
            displayedPipelines.addAll(displayedGitLabProject.getPipelines());
            if (tableViewer == null || tableViewer.getTable().isDisposed()) {
                createTableViewer();
                composite.layout(true);
            }
            if (tableViewer != null) {
                tableViewer.refresh();
            }
        });
    }

    private void displayProjectStatusAndHideTable(String text) {
        if (projectStatus.isDisposed()) {
            projectStatus = new Link(composite, SWT.NONE);
            addRepositoryBindingSelectionListener();
        }
        projectStatus.setText(text);

        if (tableViewer != null && !tableViewer.getTable().isDisposed()) {
            tableViewer.getTable().dispose();
            composite.layout(true);
        }
    }

    private IProject findSelectedProject(ISelection selection) {
        if (!(selection instanceof IStructuredSelection)) {
            return null;
        }

        IStructuredSelection ss = (IStructuredSelection) selection;
        Object element = ss.getFirstElement();
        if (!(element instanceof IAdaptable)) {
            return null;
        }

        IAdaptable adaptable = (IAdaptable) element;
        Object adapter = adaptable.getAdapter(IResource.class);
        IResource resource = (IResource) adapter;
        if (resource == null) {
            return null;
        }
        return resource.getProject();
    }

    private void createColumns() {
        int minSize = 40;

        TableViewerColumn statusColumnViewer = createTableViewerColumn("Status", 60);
        statusColumnViewer.setLabelProvider(new PipelineStatusImageLabelProvider());

        TableViewerColumn webRefColumnViewer = createTableViewerColumn("URL", minSize);
        ColumnImageMouseListener columnMouseListener = new ColumnImageMouseListener(webRefColumnViewer, 1, cell -> {
            try {
                Pipeline pipeline = (Pipeline) cell.getElement();
                PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser()
                        .openURL(new URL(getGitLabServer() + pipeline.getDetailedStatus().getDetailsPath()));
            } catch (PartInitException | MalformedURLException ex) {
                GitLabUIPlugin.logError(ex.getMessage());
            }
        });
        addMouseListener(webRefColumnViewer, columnMouseListener);
        webRefColumnViewer.setLabelProvider(new CellImageDrawLabelProvider(
                "platform:/plugin/org.eclipse.ui.browser/icons/obj16/external_browser.png"));

        TableViewerColumn refColumnViewer = createTableViewerColumn("Commit", 100);
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

        TableViewerColumn durationColumnViewer = createTableViewerColumn("Duration", 65);
        durationColumnViewer.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public String getText(Object element) {
                Pipeline pipeline = (Pipeline) element;
                return Integer.toString(pipeline.getDuration());
            }

        });

        TableViewerColumn createdAtColumnViewer = createTableViewerColumn("Last updated", 100);
        createdAtColumnViewer.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public String getText(Object element) {
                Pipeline pipeline = (Pipeline) element;
                return pipeline.getUpdatedAt();
            }

        });

        TableViewerColumn coverageColumnViewer = createTableViewerColumn("Coverage", 80);
        coverageColumnViewer.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public String getText(Object element) {
                Pipeline pipeline = (Pipeline) element;
                return Double.toString(pipeline.getCoverage());
            }

        });

        TableViewerColumn artifactsColumnViewer = createTableViewerColumn("", minSize);
        columnMouseListener = new ColumnImageMouseListener(artifactsColumnViewer, 6, cell -> {
            Pipeline pipeline = (Pipeline) cell.getElement();
            Optional<String> token = GitLabUtils.getToken();

            TestReport testReport;
            try {
                testReport = gitLabClient.getPipelineTestReports(getGitLabServer(), token.get(), pipeline);
                pipeline.setTestReport(testReport);
            } catch (IOException ex) {
                GitLabUIPlugin.logError(ex.getMessage());
            }

            testReportDisplayer.display(pipeline.getTestReport());

        });
        addMouseListener(artifactsColumnViewer, columnMouseListener);
        artifactsColumnViewer.setLabelProvider(
                new CellImageDrawLabelProvider("platform:/plugin/org.eclipse.jdt.junit/icons/full/eview16/junit.gif"));
    }

    private String getGitLabServer() {
        IPreferenceStore store = GitLabUIPlugin.getDefault().getPreferenceStore();
        return store.getString(PreferenceConstants.P_GITLAB_SERVER);
    }

    private void addMouseListener(TableViewerColumn columnViewer, ColumnImageMouseListener mouseListener) {
        columnViewer.getViewer().getControl().addMouseListener(mouseListener);
        columnViewer.getViewer().getControl().addMouseMoveListener(mouseListener);
        columnViewer.getViewer().getControl().addMouseTrackListener(mouseListener);
    }

    private TableViewerColumn createTableViewerColumn(String title, int bound) {
        final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
        final TableColumn column = viewerColumn.getColumn();
        column.setText(title);
        column.setWidth(bound);
        column.setResizable(true);
        return viewerColumn;
    }

    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        bars.getToolBarManager().add(refreshAction);
    }

    private void makeActions() {
        refreshAction = new Action() {
            @Override
            public void run() {
                if (tableViewer == null) {
                    return;
                }
                fetchPipelines();
                tableViewer.refresh();
            }
        };
        refreshAction.setText("Update");
        refreshAction.setToolTipText("Update pipelines");
        ImageDescriptor image = GitLabUIPlugin
                .getImageDescriptor("platform:/plugin/org.eclipse.ui.views.log/icons/elcl16/refresh.png");
        refreshAction.setImageDescriptor(image);
    }

    private void fetchPipelines() {
        if (displayedGitLabProject == null) {
            return;
        }

        Optional<String> token = GitLabUtils.getToken();
        if (!token.isPresent()) {
            sync.asyncExec(() -> {
                SecureTokenInputDialog dialog = new SecureTokenInputDialog(composite.getShell());
                dialog.create();
                dialog.open();
            });
            return;
        }

        try {
            List<Pipeline> newPipelines = gitLabClient.getPipelines(getGitLabServer(), token.get(),
                    displayedGitLabProject);
            displayedGitLabProject.getPipelines().clear();
            displayedGitLabProject.getPipelines().addAll(newPipelines);

            displayedPipelines.clear();
            displayedPipelines.addAll(newPipelines);
        } catch (IOException ex) {
            GitLabUIPlugin.showError(ex.getMessage());
        }

    }

}
