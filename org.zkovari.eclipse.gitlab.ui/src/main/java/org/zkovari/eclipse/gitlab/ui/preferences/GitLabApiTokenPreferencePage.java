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
