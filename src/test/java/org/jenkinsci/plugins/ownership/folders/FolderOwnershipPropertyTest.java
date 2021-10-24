/*
 * The MIT License
 *
 * Copyright 2018 CloudBees, Inc., Oleg Nenashev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jenkinsci.plugins.ownership.folders;

import com.cloudbees.hudson.plugins.folder.Folder;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.synopsys.arc.jenkins.plugins.ownership.OwnershipDescription;
import com.synopsys.arc.jenkins.plugins.ownership.util.AbstractOwnershipHelper;
import hudson.cli.CLICommandInvoker;
import hudson.cli.UpdateJobCommand;
import hudson.model.Item;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.ownership.model.OwnershipHelperLocator;
import org.jenkinsci.plugins.ownership.model.folders.FolderOwnershipHelper;
import org.jenkinsci.plugins.ownership.model.folders.FolderOwnershipProperty;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.For;
import org.jvnet.hudson.test.Issue;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.WebClient;
import org.jvnet.hudson.test.MockAuthorizationStrategy;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static hudson.cli.CLICommandInvoker.Matcher.succeededSilently;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

// TODO: DRY, merge with JobOwnerJobHelper once helper#setOwnership() is a non-static method
@For(FolderOwnershipProperty.class)
public class FolderOwnershipPropertyTest {

    @Rule
    public JenkinsRule r = new JenkinsRule();

    private Folder p;
    private AbstractOwnershipHelper<Folder> ownershipHelper;

    @Before
    public void setupSecurity() {
        r.jenkins.setSecurityRealm(r.createDummySecurityRealm());
        MockAuthorizationStrategy mas = new MockAuthorizationStrategy();
        mas.grant(Jenkins.ADMINISTER) // Implies MANAGE_ITEMS_OWNERSHIP.
                .everywhere()
                .to("admin");
        mas.grant(Item.CONFIGURE, Item.READ, Jenkins.READ)
                .everywhere()
                .to("non-admin");
        r.jenkins.setAuthorizationStrategy(mas);
    }

    @Before
    public void initFolder() throws Exception {
        // Unique project name
        p = r.createProject(Folder.class, "test" + r.jenkins.getItems().size());
        ownershipHelper = OwnershipHelperLocator.locate(p);
        if (ownershipHelper == null) {
            throw new AssertionError("Cannot locate ownership helper for " + p + " of type " + p.getClass());
        }
    }

    @Test
    @Issue("JENKINS-49744")
    public void changeOwnerViaPost() throws Exception {
        FolderOwnershipHelper.setOwnership(p,
                new OwnershipDescription(true, "admin", null));

        WebClient wc = r.createWebClient();
        wc.login("non-admin", "non-admin");
        WebRequest req = new WebRequest(wc.createCrumbedUrl(String.format("%sconfig.xml", p.getUrl())), HttpMethod.POST);
        req.setAdditionalHeader("Content-Type", "application/xml");
        req.setRequestBody(getItemXml("admin"));
        wc.getPage(req);
        assertThat("Users should be able to configure Folder when ownership is unchanged",
                ownershipHelper.getOwner(p), is(equalTo("admin")));

        try {
            wc.login("non-admin", "non-admin");
            req.setRequestBody(getItemXml("non-admin"));
            wc.getPage(req);
        } catch (FailingHttpStatusCodeException e) {
            // fine
        }
        assertThat(ownershipHelper.getOwner(p), is(equalTo("admin")));

        wc.login("admin", "admin");
        req.setRequestBody(getItemXml("non-admin"));
        wc.getPage(req);
        assertThat("Users with Manage Ownership/Jobs permissions should be able to change ownership",
                ownershipHelper.getOwner(p), is(equalTo("non-admin")));
    }

    @Test
    @Issue("JENKINS-49744")
    public void changeOwnerViaCLI() throws Exception {
        FolderOwnershipHelper.setOwnership(p,
                new OwnershipDescription(true, "admin", null));

        CLICommandInvoker command = new CLICommandInvoker(r, new UpdateJobCommand())
                .asUser("non-admin")
                .withArgs(p.getFullName())
                .withStdin(getItemXmlAsStream("admin"));
        assertThat(ownershipHelper.getOwner(p), is(equalTo("admin")));

        command.asUser("admin")
                .withArgs(p.getFullName())
                .withStdin(getItemXmlAsStream("non-admin"));
        assertThat("Users with Overall/Administer permissions should be able to configure jobs via CLI", 
                command.invoke(), succeededSilently());
        assertThat(ownershipHelper.getOwner(p), is(equalTo("non-admin")));
    }

    private String getItemXml(String ownerSid) {
        return String.format(FOLDER_XML_TEMPLATE, ownerSid);
    }

    private InputStream getItemXmlAsStream(String ownerSid) {
        return new ByteArrayInputStream(getItemXml(ownerSid).getBytes(StandardCharsets.UTF_8));
    }

    private static final String FOLDER_XML_TEMPLATE =
            "<?xml version='1.0' encoding='UTF-8'?>" +
            "<com.cloudbees.hudson.plugins.folder.Folder plugin=\"cloudbees-folder@6.1.0\">" +
            "  <properties>" +
            "    <org.jenkinsci.plugins.ownership.model.folders.FolderOwnershipProperty plugin=\"ownership@0.10.1\">" +
            "        <ownership>" +
            "           <ownershipEnabled>true</ownershipEnabled>" +
            "           <primaryOwnerId>%s</primaryOwnerId>" +
            "           <coownersIds class=\"sorted-set\"/>" +
            "       </ownership>" +
            "   </org.jenkinsci.plugins.ownership.model.folders.FolderOwnershipProperty>" +
            "  </properties>" +
            "  <views>\n" +
            "    <hudson.model.AllView>\n" +
            "      <owner class=\"com.cloudbees.hudson.plugins.folder.Folder\" reference=\"../../..\"/>\n" +
            "      <name>All</name>\n" +
            "      <filterExecutors>false</filterExecutors>\n" +
            "      <filterQueue>false</filterQueue>\n" +
            "      <properties class=\"hudson.model.View$PropertyList\"/>\n" +
            "    </hudson.model.AllView>\n" +
            "  </views>\n" +
            "  <viewsTabBar class=\"hudson.views.DefaultViewsTabBar\"/>" +
            "</com.cloudbees.hudson.plugins.folder.Folder>";

}
