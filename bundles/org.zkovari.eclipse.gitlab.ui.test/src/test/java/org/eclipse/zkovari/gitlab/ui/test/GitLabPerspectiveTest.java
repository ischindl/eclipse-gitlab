/*******************************************************************************
 * Copyright (C) 2019 Zsolt Kovari
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.eclipse.zkovari.gitlab.ui.test;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class GitLabPerspectiveTest {

	private SWTWorkbenchBot bot;

	@Before
	public void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot();
	}

	@Test
	public void canCreateANewJavaProject() throws Exception {
		bot.menu("Window").menu("Preferences").click();
		SWTBotShell prefsShell = bot.shell("Preferences");
		prefsShell.activate();
		SWTBotTreeItem treeItem = bot.tree().getTreeItem("GitLab").select().expand().click();
//		SWTBotTreeItem treeNode = treeItem.getNode("Capabilities").select().expand().click();
	}

	@After
	public void sleep() {
		bot.sleep(2000);
	}

}
