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
package org.zkovari.eclipse.gitlab.ui.dialogs;

import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class SecureTokenInputDialog extends TitleAreaDialog {

	public SecureTokenInputDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Please specify your GitLab API token");
		setMessage("Your token will be encrypted and saved into secure storage", IMessageProvider.INFORMATION);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		Text passwordField = new Text(container, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
		passwordField.setEchoChar('*');

		Button button = new Button(container, SWT.PUSH);
		button.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		button.setText("Store in secure storage");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ISecurePreferences preferences = SecurePreferencesFactory.getDefault();
				ISecurePreferences node = preferences.node("gitlab");
				try {
					node.put("token", passwordField.getText(), true);
				} catch (StorageException e1) {
					e1.printStackTrace();
				}
			}
		});

		return container;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}
}
