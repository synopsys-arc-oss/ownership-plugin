package com.synopsys.arc.jenkins.plugins.ownership.jobs;

import com.synopsys.arc.jenkins.plugins.ownership.OwnershipAction;
import com.synopsys.arc.jenkins.plugins.ownership.util.UserCollectionFilter;
import com.synopsys.arc.jenkins.plugins.ownership.util.userFilters.AccessRightsFilter;
import com.synopsys.arc.jenkins.plugins.ownership.util.userFilters.IUserFilter;
import hudson.model.*;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.security.Permission;
import java.util.ArrayList;
import java.util.Collection;

public class JobOwnerJobProperty extends JobProperty<Job<?, ?>> {

    public static final String DefaultBuilUserString="N/A";
    private String jobOwner = DefaultBuilUserString;
    public boolean ownershipIsEnabled;
    
    @DataBoundConstructor
    public JobOwnerJobProperty( String jobOwner, boolean ownershipIsEnabled) {
            this.jobOwner = jobOwner;
            this.ownershipIsEnabled = ownershipIsEnabled;
    }

    public String getJobOwner() {
        return jobOwner;
    }
    
    public boolean isSelected(User usr) {
        return jobOwner.equals(usr.getId());
    }
    
    public String getDisplayName(User usr) {
        return usr.toString();
    }
    
    public User getJobOwnerClass() {
        return User.get(jobOwner);
    }
    
    public Collection<User> getUsers()
    {                    
        // Sort users
        IUserFilter filter = new AccessRightsFilter(owner, Job.CONFIGURE);
        Collection<User> res =UserCollectionFilter.filterUsers(User.getAll(), true, filter);
        return res;
    }
    
    public boolean isOwnerExists() {
        return JobOwnerHelper.isUserExists(jobOwner);
    }

    @Extension
    public static class DescriptorImpl extends JobPropertyDescriptor {

            @Override
            public JobProperty<?> newInstance( StaplerRequest req, JSONObject formData ) throws FormException {
                    Object debugObject = formData.get( "jobOwner" );

                    System.out.println( formData.toString( 2 ) );

                    if( debugObject != null ) {
                            JSONObject debugJSON = (JSONObject) debugObject;
                            String jobOwner = debugJSON.getString( "jobOwner" );
                            boolean ownershipIsEnabled = true;
                            //TODO: ownership enabled from global config                           
                            JobOwnerJobProperty instance = new JobOwnerJobProperty( jobOwner, ownershipIsEnabled );
                            return instance;
                    }

                    return null;
            }

            @Override
            public String getDisplayName() {
                    return "Job Owner";
            }

            @Override
            public boolean isApplicable( Class<? extends Job> jobType ) {
                    return true;
            }
            
            
	}

        @Override
	public String toString() {
		return "jobOwner=" + jobOwner;
	}

    @Override
    public Collection<? extends Action> getJobActions(Job<?, ?> job) {
        Collection<OwnershipAction> col = new ArrayList<OwnershipAction>();
         
        ownershipIsEnabled = true;
        if (ownershipIsEnabled) 
        {
            col.add(new JobOwnerJobAction(job));
        }
        return col;
    }
}
