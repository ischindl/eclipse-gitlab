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
package org.zkovari.eclipse.gitlab.ui.views;

import java.io.IOException;
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
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.part.ViewPart;
import org.zkovari.eclipse.gitlab.core.GitLabClient;
import org.zkovari.eclipse.gitlab.core.GitLabProject;
import org.zkovari.eclipse.gitlab.core.GitLabUtils;
import org.zkovari.eclipse.gitlab.core.Pipeline;
import org.zkovari.eclipse.gitlab.core.ProjectMapping;
import org.zkovari.eclipse.gitlab.core.security.GitLabSecureStore;
import org.zkovari.eclipse.gitlab.core.security.SecureStoreException;
import org.zkovari.eclipse.gitlab.ui.Activator;

public class GitLabPipelineView extends ViewPart {

    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "org.zkovari.eclipse.gitlab.ui.views.GitLabPipelineView";

    @Inject
    IWorkbench workbench;

    @Inject
    UISynchronize sync;

    private Composite composite;
    private TableViewer viewer;
    private Action refreshAction;
    private Link projectStatus;

    private GitLabProject gitLabProject;
    private IPath repositoryPath;

    private final ProjectMapping projectMapping;
    private final List<Pipeline> pipelines;

    public GitLabPipelineView() {
        pipelines = new ArrayList<>();
        projectMapping = org.zkovari.eclipse.gitlab.core.Activator.getInstance().getProjectMapping();
    }

    class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
        @Override
        public String getColumnText(Object obj, int index) {
            return getText(obj);
        }

        @Override
        public Image getColumnImage(Object obj, int index) {
            return getImage(obj);
        }

        @Override
        public Image getImage(Object obj) {
            return workbench.getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
        }
    }

    @Override
    public void createPartControl(Composite parent) {
        composite = new Composite(parent, SWT.NONE);
        FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
        composite.setLayout(fillLayout);

        projectStatus = new Link(composite, SWT.NONE);
        projectStatus.setText("Select project in Package Explorer");
        addRepositoryBindingSelectionListener();
        addProjectSelectionListener();
        makeActions();
        contributeToActionBars();
    }

    private void addRepositoryBindingSelectionListener() {
        projectStatus.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                Job job = Job.create("Update table", (ICoreRunnable) monitor -> {
                    try {
                        Optional<String> token = GitLabUtils.getToken();
                        gitLabProject = projectMapping.getOrCreateGitLabProject(repositoryPath, token.get());
                        fetchPipelines();
                    } catch (IOException e1) {
                        gitLabProject = null;
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    sync.asyncExec(() -> {
                        if (viewer == null || viewer.getTable().isDisposed()) {
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

    private void createTableViewer() {
        viewer = new TableViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.FILL);
        createColumns();

        viewer.getTable().setHeaderVisible(true);
        viewer.getTable().setLinesVisible(true);

        // removed generic because it wasn't supported in previous Eclipse versions
        viewer.setContentProvider(new ObservableListContentProvider());
        IObservableList input = Properties.<Pipeline>selfList(Pipeline.class).observe(pipelines);
        viewer.setInput(input);
    }

    private void addProjectSelectionListener() {
        ISelectionService service = getSite().getService(ISelectionService.class);
        service.addSelectionListener("org.eclipse.jdt.ui.PackageExplorer", (part, selection) -> {
            IProject project = findSelectedProject(selection);
            if (project == null) {
                return;
            }
            repositoryPath = projectMapping.findRepositoryPath(project);
            if (repositoryPath == null) {
                displayProjectStatusAndHideTable("Selected project is not an EGit repository: " + project.getName());
                return;
            }

            gitLabProject = projectMapping.findProject(repositoryPath);
            if (gitLabProject == null) {
                displayProjectStatusAndHideTable("<a>Bind project " + project.getName() + "</a>");
                return;
            }
            if (!projectStatus.isDisposed()) {
                projectStatus.dispose();
                composite.layout(true);
            }
            if (gitLabProject.getPipelines().isEmpty()) {
                displayProjectStatusAndHideTable("Selected project does not have any pipelines: " + project.getName());
                return;
            }
            pipelines.clear();
            pipelines.addAll(gitLabProject.getPipelines());
            if (viewer != null && viewer.getTable().isDisposed()) {
                createTableViewer();
                composite.layout(true);
            }
            if (viewer != null) {
                viewer.refresh();
            }
        });
    }

    private void displayProjectStatusAndHideTable(String text) {
        if (projectStatus.isDisposed()) {
            projectStatus = new Link(composite, SWT.NONE);
            addRepositoryBindingSelectionListener();
        }
        projectStatus.setText(text);

        if (viewer != null && !viewer.getTable().isDisposed()) {
            viewer.getTable().dispose();
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
        return resource.getProject();
    }

    private void createColumns() {
        String[] titles = { "Status", "ID", "Web URL", "Reference" };
        int[] bounds = { 100, 100, 100, 100 };

        TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0]);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                Pipeline p = (Pipeline) element;
                return p.getStatus();
            }
        });

        col = createTableViewerColumn(titles[1], bounds[1]);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                Pipeline p = (Pipeline) element;
                return p.getId();
            }
        });

        col = createTableViewerColumn(titles[2], bounds[2]);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                Pipeline p = (Pipeline) element;
                return p.getWebUrl();
            }
        });

        col = createTableViewerColumn(titles[3], bounds[3]);
        col.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                Pipeline p = (Pipeline) element;
                return p.getRef();
            }
        });
    }

    private TableViewerColumn createTableViewerColumn(String title, int bound) {
        final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
        final TableColumn column = viewerColumn.getColumn();
        column.setText(title);
        column.setWidth(bound);
        column.setResizable(true);
        column.setMoveable(true);
        return viewerColumn;
    }

    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(refreshAction);
    }

    private void makeActions() {
        refreshAction = new Action() {
            @Override
            public void run() {
                if (viewer == null) {
                    return;
                }
                fetchPipelines();
                viewer.refresh();
                viewer.refresh();
            }

        };
        refreshAction.setText("Update");
        refreshAction.setToolTipText("Update pipelines");

        ImageDescriptor image = Activator
                .getImageDescriptor("platform:/plugin/org.eclipse.ui.views.log/icons/elcl16/refresh.png");
        refreshAction.setImageDescriptor(image);
    }

    private void fetchPipelines() {
        Optional<String> token = Optional.empty();
        try {
            token = new GitLabSecureStore().getToken(SecurePreferencesFactory.getDefault());
        } catch (SecureStoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (gitLabProject == null) {
            return;
        }
        try {
            List<Pipeline> newPipelines = new GitLabClient().getPipelines("https://gitlab.com", token.get(),
                    gitLabProject);
            gitLabProject.getPipelines().clear();
            gitLabProject.getPipelines().addAll(newPipelines);

            pipelines.clear();
            pipelines.addAll(newPipelines);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void setFocus() {
        composite.setFocus();
    }
}
