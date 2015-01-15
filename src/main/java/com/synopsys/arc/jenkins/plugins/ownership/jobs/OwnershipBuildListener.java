/*
 * The MIT License
 *
 * Copyright 2015 Oleg Nenashev <o.v.nenashev@gmail.com>.
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

package com.synopsys.arc.jenkins.plugins.ownership.jobs;

import com.synopsys.arc.jenkins.plugins.ownership.wrappers.OwnershipBuildWrapper;
import hudson.EnvVars;
import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.EnvironmentContributingAction;
import hudson.model.Job;
import hudson.model.Project;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;

/**
 * Listens build statuses.
 * Functionality:
 * <ul>
 *   <li/> Inject ownership variables according to {@link OwnershipBuildWrapper}
 * settings
 * </ul>
 * 
 * @author Oleg Nenashev <o.v.nenashev@gmail.com>
 * @since 0.6
 */
@Extension
public class OwnershipBuildListener extends RunListener<Run> {

    @Override
    public void onStarted(Run r, TaskListener listener) {
        Action a = new OwnershipEnvVarsAction();  
        r.addAction(a);
    }  
    
    public static class OwnershipEnvVarsAction implements EnvironmentContributingAction {

        @Override
        public void buildEnvVars(AbstractBuild<?, ?> build, EnvVars env) {
                    final Job parent = build.getParent();
            if (!(parent instanceof Project)) {
                return; // TODO: do something?
            }

            final Project prj = (Project) parent;
            final OwnershipBuildWrapper wrapper = (OwnershipBuildWrapper) prj.getBuildWrappersList().
                            get(OwnershipBuildWrapper.class);
            if (wrapper == null) {
                return; // disabled
            } 
            wrapper.setUp(build, env, null);
        }

        @Override
        public String getIconFileName() {
            return null;
        }

        @Override
        public String getDisplayName() {
            return null;
        }

        @Override
        public String getUrlName() {
            return null;
        }
        
    }
}
