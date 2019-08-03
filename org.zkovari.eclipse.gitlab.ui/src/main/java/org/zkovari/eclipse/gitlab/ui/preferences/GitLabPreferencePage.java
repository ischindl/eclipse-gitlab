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

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.zkovari.eclipse.gitlab.ui.Activator;

public class GitLabPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public GitLabPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("General GitLab settings:");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI
	 * blocks needed to manipulate various types of preferences. Each field editor
	 * knows how to save and restore itself.
	 */
	@Override
	protected void createFieldEditors() {
//		addField(
//				new DirectoryFieldEditor(PreferenceConstants.P_PATH, "&Directory preference:", getFieldEditorParent()));
//		addField(new BooleanFieldEditor(PreferenceConstants.P_BOOLEAN, "&An example of a boolean preference",
//				getFieldEditorParent()));
//		addField(new RadioGroupFieldEditor(PreferenceConstants.P_CHOICE, "An example of a multiple-choice preference",
//				1, new String[][] { { "&Choice 1", "choice1" }, { "C&hoice 2", "choice2" } }, getFieldEditorParent()));
		addField(new StringFieldEditor(PreferenceConstants.P_GITLAB_SERVER, "GitLab server:", getFieldEditorParent()));

	}

	public void init(IWorkbench workbench) {
	}

}
