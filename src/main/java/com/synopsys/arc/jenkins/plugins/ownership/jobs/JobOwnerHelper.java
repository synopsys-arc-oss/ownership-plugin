package com.synopsys.arc.jenkins.plugins.ownership.jobs;

import com.synopsys.arc.jenkins.plugins.ownership.IOwnershipHelper;
import com.synopsys.arc.jenkins.plugins.ownership.util.UserStringFormatter;
import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.User;

/**
 *
 * @author Oleg Nenashev <nenashev@synopsys.com>
 */
public class JobOwnerHelper implements IOwnershipHelper<Job<?,?>>{
    public final static JobOwnerHelper Instance = new JobOwnerHelper();
    
    @Override
    public String getOwner(Job<?,?> job) {
        AbstractProject project = (AbstractProject) job;
        JobProperty prop = project.getProperty(JobOwnerJobProperty.class);
	return prop != null ? ((JobOwnerJobProperty)prop).getJobOwner() : JobOwnerJobProperty.DefaultBuilUserString;
    }
    
    @Override
    public boolean isOwnerExists(Job job) {
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
        
    @Override
    public String getOwnerLongString(Job job)
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
