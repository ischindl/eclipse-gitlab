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
package org.eclipse.zkovari.gitlab.ui.test.views;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class GitLabPipelineViewTest {

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

    @Test
    public void testPipelineView() {
        bot.menu("Window").menu("Show View").menu("Other...").click();
        SWTBotShell viewsShell = bot.shell("Show View");
        viewsShell.activate();
        bot.tree().getTreeItem("GitLab").select().expand().click().getNode("GitLab Pipeline").select().doubleClick();
        bot.viewById("org.zkovari.eclipse.gitlab.ui.views.GitLabPipelineView").setFocus();
    }

    @After
    public void sleep() {
        bot.sleep(1000);
    }

}
