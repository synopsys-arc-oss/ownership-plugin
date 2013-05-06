/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.synopsys.arc.jenkins.plugins.ownership.jobs;

import com.synopsys.arc.jenkins.plugins.ownership.OwnershipAction;
import hudson.model.Job;

/**
 *
 * @author Oleg Nenashev <nenashev@synopsys.com>
 */
public class JobOwnerJobAction implements OwnershipAction {
    
    Job<?, ?> linkedJob;
    
    public JobOwnerJobAction(Job<?, ?> job) {
      // ignore job - just4backward compatibility  
      linkedJob = job;
    }
    public String getJobOwner(@SuppressWarnings("rawtypes") Job job) {
        assert (job != null);
        assert(linkedJob == job);
        return JobOwnerHelper.getJobOwner(job);
    }
    
    public boolean isOwnerExists(@SuppressWarnings("rawtypes") Job job) {
        assert (job != null);
        assert(linkedJob == job);
        return JobOwnerHelper.isOwnerExists(job);
    }
    
    public String getJobOwnerLongString(@SuppressWarnings("rawtypes") Job job) {
        assert (job != null);
        assert(linkedJob == job);
        return JobOwnerHelper.getJobOwnerLongString(job);
    }
    
    public Job<?, ?> getJob()
    {
        return linkedJob;
    }

    @Override
    public String getIconFileName() {
         return "graph.gif"; 
    }

    @Override
    public String getDisplayName() {
        return "Job ownership";
    }

    @Override
    public String getUrlName() {
        return "Ownership";
    }
}
