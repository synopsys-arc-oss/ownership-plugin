/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.synopsys.arc.jenkins.plugins.ownership.jobs;

import com.synopsys.arc.jenkins.plugins.ownership.util.UserComparator;
import com.synopsys.arc.jenkins.plugins.ownership.util.UserWrapper;
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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Filters owner's and co-owners
 * @author Oleg Nenashev <nenashev@synopsys.com>
 */
public class OwnershipJobFilter extends ViewJobFilter {
    /**
     * Macro, which allows to select currently logged user for "My owned jobs" filter
     */
    private static final String MACRO_ME = "@Me";
    
    String ownerId;
    boolean acceptsCoowners;
    
    public String getOwnerName() {
        return ownerId;
    }
    
    public boolean isAcceptsCoowners() {
        return acceptsCoowners;
    }
    
    public boolean isSelected(UserWrapper usr) {
        return ownerId.equals(usr.getId());
    }
    
    public String getDisplayName(UserWrapper usr) {
        return usr.toString();
    }
       
    @DataBoundConstructor
    public OwnershipJobFilter(String ownerName, boolean acceptCoowners) {
        this.ownerId = ownerName;
        this.acceptsCoowners = acceptCoowners;
    }
    
    @Override
    public List<TopLevelItem> filter(List<TopLevelItem> added, List<TopLevelItem> all, View filteringView) {
              
        ArrayList<TopLevelItem> newList = new ArrayList<TopLevelItem>();
        
        UserWrapper wuserWrapper = new UserWrapper(ownerId);
            
        for(TopLevelItem item : added) {
            // Convert to project
            AbstractProject project = (AbstractProject)item;
            JobProperty prop = project.getProperty(JobOwnerJobProperty.class);
              
            if (prop != null) {
                String jobOwnerId = ((JobOwnerJobProperty)prop).getJobOwner();
                if (wuserWrapper.meetsMacro(jobOwnerId)) {
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
        public Collection<UserWrapper> getUsers()
        {                    
            // Sort users
            UserComparator comparator = new UserComparator();
            LinkedList<User> userList = new LinkedList<User>(User.getAll());                     
            Collections.sort(userList, comparator);
            
            // Prepare new list
            Collection<UserWrapper> res = new ArrayList<UserWrapper>(userList.size()+1);
            res.add(new UserWrapper(MACRO_ME));
            for (User user : userList)
            {
                res.add(new UserWrapper(user));
            }
            return res;
        }    
        
        public User getCurrentUser()
        {
            return User.current();
        }
    }
    
}

