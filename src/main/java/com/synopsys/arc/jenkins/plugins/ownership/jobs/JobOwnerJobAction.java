package com.synopsys.arc.jenkins.plugins.ownership.jobs;

import com.synopsys.arc.jenkins.plugins.ownership.ItemOwnershipAction;
import hudson.model.Job;

/**
 * Ownership action for jobs
 * @author Oleg Nenashev <nenashev@synopsys.com>
 */
public class JobOwnerJobAction extends ItemOwnershipAction<Job<?,?>> {
    
    private final static JobOwnerHelper helper = new JobOwnerHelper();
    
    public JobOwnerJobAction(Job<?, ?> job) {
      super(job);
    }

    @Override
    public JobOwnerHelper helper() {
        return helper;
    }
       
    /** 
     * Gets described job
     * @deprecated Just for compatibility with 0.0.1
     */
    public Job<?, ?> getJob()
    {
        return getDescribedItem();
    }

    @Override
    public String getDisplayName() {
        return "Job ownership";
    }
}
