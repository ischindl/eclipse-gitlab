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
package org.zkovari.eclipse.gitlab.ui.test.views;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroManager;
import org.eclipse.ui.intro.IIntroPart;
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
                final IIntroManager introManager = PlatformUI.getWorkbench().getIntroManager();
                IIntroPart part = introManager.getIntro();
                introManager.closeIntro(part);
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().forceActive();
            }
        });
    }

    private void openPipelineView() {
        bot.menu("Window").menu("Show View").menu("Other...").click();
        bot.shell("Show View").activate();
        bot.tree().getTreeItem("GitLab").select().expand().click().getNode("GitLab Pipeline").select().doubleClick();
    }

    private void createNewProject(String projectName) {
        bot.menu("File").menu("New").menu("Project...").click();
        bot.shell("New Project").activate();
        bot.tree().getTreeItem("Project").select().doubleClick();
        bot.text(0).setText(projectName);
        bot.button("Finish").click();
    }

    @Test
    public void testProjectSelection() {
        openPipelineView();
        bot.viewByTitle("GitLab Pipeline").setFocus();
        bot.link("Select project...").isVisible();

        createNewProject("test-project-name");
        bot.viewByTitle("Project Explorer").setFocus();
        bot.tree().getTreeItem("test-project-name").select();
        bot.viewByTitle("GitLab Pipeline").setFocus();
        bot.link("Selected project is not an EGit repository: test-project-name").isVisible();
    }

    @After
    public void sleep() {
        bot.sleep(1000);
    }

}
