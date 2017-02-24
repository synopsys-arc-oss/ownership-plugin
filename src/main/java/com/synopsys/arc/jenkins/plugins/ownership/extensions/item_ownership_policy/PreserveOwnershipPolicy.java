package com.synopsys.arc.jenkins.plugins.ownership.extensions.item_ownership_policy;

import com.synopsys.arc.jenkins.plugins.ownership.Messages;
import com.synopsys.arc.jenkins.plugins.ownership.OwnershipDescription;
import com.synopsys.arc.jenkins.plugins.ownership.extensions.ItemOwnershipPolicy;
import com.synopsys.arc.jenkins.plugins.ownership.extensions.ItemOwnershipPolicyDescriptor;
import com.synopsys.arc.jenkins.plugins.ownership.jobs.JobOwnerHelper;
import com.synopsys.arc.jenkins.plugins.ownership.jobs.JobOwnerJobProperty;
import hudson.Extension;
import hudson.model.Item;
import hudson.model.Job;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * A policy, which keeps the previous job's ownership.
 * @author cpuydebois
 * @since 0.9.2
 */
public class PreserveOwnershipPolicy extends ItemOwnershipPolicy {

    @DataBoundConstructor
    public PreserveOwnershipPolicy() {
    }

    @Override
    public OwnershipDescription onCreated(Item item) {
        if (item instanceof Job) {
            JobOwnerJobProperty jobProperty = JobOwnerHelper.getOwnerProperty((Job<?, ?>) item);
            if (jobProperty != null) {
                return jobProperty.getOwnership();
            }
        }
        return null;
    }

    @Extension
    public static class DescriptorImpl extends ItemOwnershipPolicyDescriptor {
        @Override
        public String getDisplayName() {
            return Messages.ItemOwnershipPolicy_PreserveOwnershipPolicy_displayName();
        }
    }
}
