/*
 * The MIT License
 *
 * Copyright (c) 2015 Oleg Nenashev.
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

import com.cloudbees.hudson.plugins.folder.AbstractFolder;
import com.cloudbees.hudson.plugins.folder.Folder;
import com.synopsys.arc.jenkins.plugins.ownership.OwnershipDescription;
import java.util.Arrays;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * Stores tests of {@link AbstractFolder} ownership.
 * @author Oleg Nenashev
 */
public class FolderOwnershipTest {
    
    @Rule
    public JenkinsRule j = new JenkinsRule();
    
    public FolderOwnershipHelper ownershipHelper = FolderOwnershipHelper.getInstance();
    
    @Test
    public void ownershipInfoShouldBeEmptyByDefault() throws Exception {
        Folder folder = j.jenkins.createProject(Folder.class, "myFolder");
        assertThat("Property should not be injected by default", 
                FolderOwnershipHelper.getOwnerProperty(folder), nullValue());
        
        assertThat("Folder ownership helper should return the \"disabled\" description",
                ownershipHelper.getOwnershipDescription(folder), 
                equalTo(OwnershipDescription.DISABLED_DESCR));
    }
    
    @Test
    public void ownershipInfoShouldSurviveRoundtrip() throws Exception {
        Folder folder = j.jenkins.createProject(Folder.class, "myFolder");
        
        // Set ownership via API
        OwnershipDescription original = new OwnershipDescription(true, "ownerId", 
            Arrays.asList("coowner1, coowner2"));
        FolderOwnershipHelper.setOwnership(folder, original);
        
        assertThat("Folder ownership helper should return the configured value",
                ownershipHelper.getOwnershipDescription(folder), 
                equalTo(original));
        
        // Reload folder from disk and check the state
        j.jenkins.reload();
        assertThat("Folder ownership helper should return the configured value after the reload",
                ownershipHelper.getOwnershipDescription(folder), 
                equalTo(original));
    }
}
