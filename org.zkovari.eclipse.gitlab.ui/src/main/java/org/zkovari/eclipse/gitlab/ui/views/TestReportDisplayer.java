/*******************************************************************************
 * Copyright 2020 Zsolt Kovari
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.jdt.internal.junit.JUnitCorePlugin;
import org.eclipse.jdt.internal.junit.model.TestRunHandler;
import org.eclipse.jdt.internal.junit.model.TestRunSession;
import org.eclipse.jdt.internal.junit.ui.JUnitPlugin;
import org.eclipse.jdt.internal.junit.ui.TestRunnerViewPart;
import org.xml.sax.SAXException;
import org.zkovari.eclipse.gitlab.core.TestReport;
import org.zkovari.eclipse.gitlab.ui.GitLabUIPlugin;

@SuppressWarnings("restriction")
public class TestReportDisplayer {

    private Marshaller marshaller;
    private SAXParser parser;

    public TestReportDisplayer() {
        try {
            JAXBContext contextObj = JAXBContext.newInstance(TestReport.class);
            marshaller = contextObj.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        } catch (JAXBException ex) {
            GitLabUIPlugin.logError(ex.getMessage());
        }

        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        try {
            parser = parserFactory.newSAXParser();
        } catch (ParserConfigurationException | SAXException ex) {
            GitLabUIPlugin.logError(ex.getMessage());
        }
    }

    public void display(TestReport testReport) {
        JUnitPlugin.showTestRunnerViewPartInActivePage();
        try {
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(testReport, stringWriter);
            String junitXmlString = stringWriter.toString();

            TestRunHandler handler = new TestRunHandler();
            InputStream targetStream = new ByteArrayInputStream(junitXmlString.getBytes());
            parser.parse(targetStream, handler);
            TestRunSession session = handler.getTestRunSession();
            JUnitCorePlugin.getModel().addTestRunSession(session);
        } catch (JAXBException | SAXException | IOException ex) {
            GitLabUIPlugin.logError(ex.getMessage());
        }
        TestRunnerViewPart view = (TestRunnerViewPart) JUnitPlugin.getActivePage().findView(TestRunnerViewPart.NAME);
        view.showTestResultsView();
    }

}
