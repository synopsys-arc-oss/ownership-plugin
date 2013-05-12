/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.synopsys.arc.jenkins.plugins.ownership.jobs;

import com.synopsys.arc.jenkins.plugins.ownership.util.UserStringFormatter;
import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.User;

/**
 *
 * @author Oleg Nenashev <nenashev@synopsys.com>
 */
public class JobOwnerHelper {
    public static String getJobOwner(@SuppressWarnings("rawtypes") Job job) {
        AbstractProject project = (AbstractProject) job;
        JobProperty prop = project.getProperty(JobOwnerJobProperty.class);
	return prop != null ? ((JobOwnerJobProperty)prop).getJobOwner() : JobOwnerJobProperty.DefaultBuilUserString;
    }
    
    public static boolean isOwnerExists(@SuppressWarnings("rawtypes") Job job) {
        AbstractProject project = (AbstractProject) job;
        JobProperty prop = project.getProperty(JobOwnerJobProperty.class);
	return prop != null ? ((JobOwnerJobProperty)prop).isOwnerExists() : false;
    }
    
    public static boolean isUserExists(User user)
    {
        assert (user != null);
        return isUserExists(user.getId());
    }
    
    public static boolean isUserExists(String userIdOrFullName)
    {
        assert (userIdOrFullName != null);
        return User.get(userIdOrFullName, false, null) != null;
    }
        
    public static String getJobOwnerLongString(@SuppressWarnings("rawtypes") Job job)
    {
        AbstractProject project = (AbstractProject) job;
        JobProperty prop = project.getProperty(JobOwnerJobProperty.class);
        
        if (prop != null)
        {
            User user = ((JobOwnerJobProperty)prop).getJobOwnerClass();
            return isUserExists(user) ? UserStringFormatter.format(user) : JobOwnerJobProperty.DefaultBuilUserString;
        }
        else {
            return JobOwnerJobProperty.DefaultBuilUserString;
        }
    }
}
