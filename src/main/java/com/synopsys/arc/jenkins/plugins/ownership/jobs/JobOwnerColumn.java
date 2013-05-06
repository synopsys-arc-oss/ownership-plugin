package com.synopsys.arc.jenkins.plugins.ownership.jobs;

import org.kohsuke.stapler.DataBoundConstructor;
import hudson.Extension;
import hudson.model.Job;
import hudson.views.ListViewColumnDescriptor;
import hudson.views.ListViewColumn;

public class JobOwnerColumn extends ListViewColumn {

    @DataBoundConstructor
    public JobOwnerColumn() {
        super();
    }

    public String getJobOwner(@SuppressWarnings("rawtypes") Job job) {
        return JobOwnerHelper.getJobOwner(job);
    }
    
    public boolean isOwnerExists(@SuppressWarnings("rawtypes") Job job) {
        return JobOwnerHelper.isOwnerExists(job);
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
