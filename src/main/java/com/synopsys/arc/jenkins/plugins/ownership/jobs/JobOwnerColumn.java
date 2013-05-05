package com.synopsys.arc.jenkins.plugins.ownership.jobs;

import org.kohsuke.stapler.DataBoundConstructor;
import hudson.model.JobProperty;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.views.ListViewColumnDescriptor;
import hudson.views.ListViewColumn;

public class JobOwnerColumn extends ListViewColumn {

    @DataBoundConstructor
    public JobOwnerColumn() {
        super();
    }

    public String getJobOwner(@SuppressWarnings("rawtypes") Job job) {
        AbstractProject project = (AbstractProject) job;
        JobProperty prop = project.getProperty(JobOwnerJobProperty.class);
		return prop != null ? ((JobOwnerJobProperty)prop).getJobOwner() : JobOwnerJobProperty.DefaultBuilUserString;
    }
    
    public boolean isOwnerExists(@SuppressWarnings("rawtypes") Job job) {
        AbstractProject project = (AbstractProject) job;
        JobProperty prop = project.getProperty(JobOwnerJobProperty.class);
		return prop != null ? ((JobOwnerJobProperty)prop).isOwnerExists() : false;
    }

    @Extension
    public static class DescriptorImpl extends ListViewColumnDescriptor {

        @Override
        public boolean shownByDefault() {
            return false;
        }

        @Override
        public String getDisplayName() {
            return "Job Owner";
        }

    }
}
