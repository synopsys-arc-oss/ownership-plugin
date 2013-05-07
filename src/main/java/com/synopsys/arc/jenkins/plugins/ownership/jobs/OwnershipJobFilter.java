/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.synopsys.arc.jenkins.plugins.ownership.jobs;

import static com.synopsys.arc.jenkins.plugins.ownership.jobs.JobOwnerHelper.isUserExists;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.model.JobProperty;
import hudson.model.TopLevelItem;
import hudson.model.User;
import hudson.model.View;
import hudson.views.ViewJobFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Filters owner's and co-owners
 * @author Oleg Nenashev <nenashev@synopsys.com>
 */
public class OwnershipJobFilter extends ViewJobFilter {

    String ownerId;
    boolean acceptsCoowners;
    
    public String getOwnerName() {
        return ownerId;
    }
    
    public boolean isAcceptsCoowners() {
        return acceptsCoowners;
    }
    
    public boolean isCurrentOwner(User usr) {
        return User.get(ownerId, false, null) == usr;
    }
       
    @DataBoundConstructor
    public OwnershipJobFilter(String ownerName, boolean acceptCoowners) {
        this.ownerId = ownerName;
        this.acceptsCoowners = acceptCoowners;
    }
    
    @Override
    public List<TopLevelItem> filter(List<TopLevelItem> added, List<TopLevelItem> all, View filteringView) {
              
        ArrayList<TopLevelItem> newList = new ArrayList<TopLevelItem>();
        
        for(TopLevelItem item : added) {
            // Convert to project
            AbstractProject project = (AbstractProject)item;
            JobProperty prop = project.getProperty(JobOwnerJobProperty.class);

            if (prop != null) {
                String jobOwner = ((JobOwnerJobProperty)prop).getJobOwner();
                if (ownerId.equals(jobOwner)) {
                    newList.add(item);
                }
            }
        }
        
        return newList;
    }

    @Extension
    public static class DescriptorImpl extends Descriptor<ViewJobFilter> {

        @Override
        public String getDisplayName() {
            return "Ownership filter";
        }
   
        @Override
        public ViewJobFilter newInstance(StaplerRequest req, JSONObject formData) throws FormException {
                String jobOwner = formData.getString( "jobOwner" );
                boolean acceptCoowners = formData.getBoolean("acceptsCoowners" );
                OwnershipJobFilter instance = new OwnershipJobFilter( jobOwner, acceptCoowners );
                return instance;
        } 
        
        /**
         * Get users for selector
         * @return Collection of all registered users 
         */
        public Collection<User> getUsers()
        {
            return User.getAll();
        }
        
        public User getCurrentUser()
        {
            return User.current();
        }
    }
    
}

