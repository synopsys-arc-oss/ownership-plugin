package com.synopsys.arc.jenkins.plugins.ownership.jobs;

import hudson.model.*;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;

public class JobOwnerJobProperty extends JobProperty<Job<?, ?>> {

    public static final String DefaultBuilUserString="N/A";
    private String jobOwner = DefaultBuilUserString;

    @DataBoundConstructor
    public JobOwnerJobProperty( String buildOwner ) {
            this.jobOwner = buildOwner;
    }

    public String getJobOwner() {
        return jobOwner;
    }
    
    public boolean isOwnerExists() {
        return !User.get(jobOwner).equals(User.getUnknown());
    }

    @Extension
    public static class DescriptorImpl extends JobPropertyDescriptor {

            @Override
            public JobProperty<?> newInstance( StaplerRequest req, JSONObject formData ) throws FormException {
                    Object debugObject = formData.get( "jobOwner" );

                    System.out.println( formData.toString( 2 ) );

                    if( debugObject != null ) {
                            JSONObject debugJSON = (JSONObject) debugObject;
                            String buildOwner = debugJSON.getString( "jobOwner" );
                            JobOwnerJobProperty instance = new JobOwnerJobProperty( buildOwner );
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

}
