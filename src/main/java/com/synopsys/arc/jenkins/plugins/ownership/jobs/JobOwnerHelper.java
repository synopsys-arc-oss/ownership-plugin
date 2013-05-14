package com.synopsys.arc.jenkins.plugins.ownership.jobs;

import com.synopsys.arc.jenkins.plugins.ownership.OwnershipDescription;
import com.synopsys.arc.jenkins.plugins.ownership.util.AbstractOwnershipHelper;
import com.synopsys.arc.jenkins.plugins.ownership.util.UserCollectionFilter;
import com.synopsys.arc.jenkins.plugins.ownership.util.userFilters.AccessRightsFilter;
import com.synopsys.arc.jenkins.plugins.ownership.util.userFilters.IUserFilter;
import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.model.JobProperty;
import hudson.model.User;
import java.util.Collection;

/**
 * Helper for Jobs Ownership
 * @since 0.0.3
 * @author Oleg Nenashev <nenashev@synopsys.com>
 */
public class JobOwnerHelper extends AbstractOwnershipHelper<Job<?,?>>{
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
}
