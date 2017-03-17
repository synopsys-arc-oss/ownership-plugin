package org.jenkinsci.plugins.ownership.config;

import com.cloudbees.hudson.plugins.folder.Folder;
import com.synopsys.arc.jenkins.plugins.ownership.IOwnershipItem;
import com.synopsys.arc.jenkins.plugins.ownership.Messages;
import com.synopsys.arc.jenkins.plugins.ownership.OwnershipDescription;
import com.synopsys.arc.jenkins.plugins.ownership.extensions.ItemOwnershipPolicy;
import com.synopsys.arc.jenkins.plugins.ownership.extensions.ItemOwnershipPolicyDescriptor;
import com.synopsys.arc.jenkins.plugins.ownership.jobs.JobOwnerHelper;
import com.synopsys.arc.jenkins.plugins.ownership.jobs.JobOwnerJobProperty;
import com.synopsys.arc.jenkins.plugins.ownership.util.AbstractOwnershipHelper;
import hudson.Extension;
import hudson.model.Item;
import hudson.model.Job;
import org.jenkinsci.plugins.ownership.model.folders.FolderOwnershipHelper;
import org.jenkinsci.plugins.ownership.model.folders.FolderOwnershipProperty;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;

/**
 * A policy, which keeps the previous job's ownership.
 * @author cpuydebois
 * @since TODO
 */
public class PreserveOwnershipPolicy extends ItemOwnershipPolicy {

    @DataBoundConstructor
    public PreserveOwnershipPolicy() {
    }

    @Override
    public OwnershipDescription onCreated(@Nonnull Item item) {
        IOwnershipItem<?> ownershipProperty = extractOwnershipProperty(item);
        if (ownershipProperty != null) {
            return ownershipProperty.getOwnership();
        }
        return null;
    }

    private IOwnershipItem<?> extractOwnershipProperty(Item item) {
        if (item instanceof Job) {
            return JobOwnerHelper.getOwnerProperty((Job<?, ?>) item);
        } else if (item instanceof Folder) {
            return FolderOwnershipHelper.getOwnerProperty((Folder) item);
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
