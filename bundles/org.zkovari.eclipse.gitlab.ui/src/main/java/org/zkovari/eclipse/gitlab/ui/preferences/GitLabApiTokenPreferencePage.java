/*******************************************************************************
 * Copyright (C) 2019 Zsolt Kovari
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.zkovari.eclipse.gitlab.ui.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.zkovari.eclipse.gitlab.ui.Activator;
import org.zkovari.eclipse.gitlab.ui.dialogs.SecureTokenInputDialog;

public class GitLabApiTokenPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public GitLabApiTokenPreferencePage() {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Store GitLab API Token is secure storage:");
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);

		Button button = new Button(composite, SWT.PUSH);
		button.setText("Press me");

		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SecureTokenInputDialog dialog = new SecureTokenInputDialog(getShell());
				dialog.create();
				dialog.open();
			}
		});
		return button;
	}

	@Override
	public void init(IWorkbench workbench) {
		// empty

	}

}
