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
package org.zkovari.eclipse.gitlab.ui.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class GitLabPreferencePageTest {

    private SWTWorkbenchBot bot;

    @Before
    public void setUp() throws Exception {
        bot = new SWTWorkbenchBot();
        UIThreadRunnable.syncExec(new VoidResult() {
            public void run() {
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().forceActive();
            }
        });
    }

    private void visitPreferences() {
        bot.menu("Window").menu("Preferences").click();
        SWTBotShell prefsShell = bot.shell("Preferences");
        prefsShell.activate();
    }

    @Test
    public void testMainPreferencesPageIsVisible() throws Exception {
        visitPreferences();
        bot.tree().getTreeItem("GitLab").select().expand().click();
        assertTrue("Expected visible label 'GitLab server:'", bot.label("GitLab server:").isVisible());
        SWTBotText serverText = bot.text("https://gitlab.com");
        assertFalse("Expected writeable text field for gitlab server", serverText.isReadOnly());
    }

    @Test
    public void testStoringToken() {
        visitPreferences();
        bot.tree().getTreeItem("GitLab").select().expand().click().getNode("API Token").click();
        bot.button("Specify API token").click();
        bot.text(0).setText("my-token");
        bot.button("Store in secure storage").click();
        bot.button("OK").click();
    }

    @After
    public void sleep() {
        bot.sleep(1000);
    }

}
