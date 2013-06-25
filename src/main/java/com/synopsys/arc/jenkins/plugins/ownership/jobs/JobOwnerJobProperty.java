/*
 * The MIT License
 *
 * Copyright 2013 Oleg Nenashev <nenashev@synopsys.com>, Synopsys Inc.
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

import com.synopsys.arc.jenkins.plugins.ownership.IOwnershipHelper;
import com.synopsys.arc.jenkins.plugins.ownership.IOwnershipItem;
import com.synopsys.arc.jenkins.plugins.ownership.OwnershipAction;
import com.synopsys.arc.jenkins.plugins.ownership.util.UserCollectionFilter;
import com.synopsys.arc.jenkins.plugins.ownership.util.UserStringFormatter;
import com.synopsys.arc.jenkins.plugins.ownership.util.userFilters.AccessRightsFilter;
import com.synopsys.arc.jenkins.plugins.ownership.util.userFilters.IUserFilter;
import hudson.model.*;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Ownership job property
 * @todo Implement generic approaches from 0.0.3
 * @author Oleg Nenashev <nenashev@synopsys.com>
 * @since 0.0.1
 */
public class JobOwnerJobProperty extends JobProperty<Job<?, ?>> 
    implements IOwnershipItem<Job<?, ?>>
{
    private String jobOwner = UserStringFormatter.UNKNOWN_USER_STRING;
    public boolean ownershipIsEnabled = false;
    
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
        return  JobOwnerHelper.Instance.getDisplayName(usr);
    }
    
    public User getJobOwnerClass() {
        return User.get(jobOwner);
    }
      
    public Collection<User> getUsers()
    {     
        // Sort users
        IUserFilter filter = new AccessRightsFilter(owner, Job.CONFIGURE);
        Collection<User> res = UserCollectionFilter.filterUsers(User.getAll(), true, filter);
        return res;
    }
    
    public boolean isOwnerExists() {
        return JobOwnerHelper.isUserExists(jobOwner);
    }

    @Override
    public IOwnershipHelper<Job<?, ?>> helper() {
        return JobOwnerHelper.Instance;
    }

    @Override
    public Job<?, ?> getDescribedItem() {
        return owner;   
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
