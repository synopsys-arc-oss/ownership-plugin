package com.synopsys.arc.jenkins.plugins.ownership.jobs;

import com.synopsys.arc.jenkins.plugins.ownership.IOwnershipHelper;
import com.synopsys.arc.jenkins.plugins.ownership.OwnershipDescription;
import com.synopsys.arc.jenkins.plugins.ownership.util.UserCollectionFilter;
import com.synopsys.arc.jenkins.plugins.ownership.util.UserStringFormatter;
import com.synopsys.arc.jenkins.plugins.ownership.util.userFilters.AccessRightsFilter;
import com.synopsys.arc.jenkins.plugins.ownership.util.userFilters.IUserFilter;
import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.User;
import java.util.Collection;

/**
 *
 * @author Oleg Nenashev <nenashev@synopsys.com>
 */
public class JobOwnerHelper implements IOwnershipHelper<Job<?,?>>{
    final static JobOwnerHelper Instance = new JobOwnerHelper();
    
    /**
     * Gets JobOwnerProperty from job if possible
     * @param job Job
     * @return JobOwnerJobProperty or null
     */
    private static JobOwnerJobProperty getOwnerProperty(Job<?,?> job)
    {
        AbstractProject project = (AbstractProject) job;
        JobProperty prop = project.getProperty(JobOwnerJobProperty.class);
        return prop != null ? (JobOwnerJobProperty)prop : null;
    }
    
    @Override
    public String getOwner(Job<?,?> job) {
        JobOwnerJobProperty prop = getOwnerProperty(job);
	return prop != null ? prop.getJobOwner() : UserStringFormatter.UNKNOWN_USER_STRING;
    }
    
    @Override
    public boolean isOwnerExists(Job job) {
        JobOwnerJobProperty prop = getOwnerProperty(job);
        return prop != null ? prop.isOwnerExists() : false;
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
        JobOwnerJobProperty prop = getOwnerProperty(job);
        
        if (prop != null)
        {
            User user = prop.getJobOwnerClass();
            return isUserExists(user) ? UserStringFormatter.format(user) : UserStringFormatter.UNKNOWN_USER_STRING;
        }
        else {
            return UserStringFormatter.UNKNOWN_USER_STRING;
        }
    }

    @Override
    public OwnershipDescription getOwnershipDescription(Job<?, ?> job) {
        JobOwnerJobProperty prop = getOwnerProperty(job);
        
        return prop != null
                ? new OwnershipDescription(prop.ownershipIsEnabled, prop.getJobOwner())
                : new OwnershipDescription(false, null);
    }

    @Override
    public Collection<User> getPossibleOwners(Job<?, ?> item) {
        IUserFilter filter = new AccessRightsFilter(item, Job.CONFIGURE);
        Collection<User> res = UserCollectionFilter.filterUsers(User.getAll(), true, filter);
        return res;
    }

    @Override
    public String getDisplayName(User usr) {
        return UserStringFormatter.format(usr);
    }
    
    
}
