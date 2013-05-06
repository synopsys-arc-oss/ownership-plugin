package com.synopsys.arc.jenkins.plugins.ownership;

import hudson.Plugin;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Contains global actions
 * 
 * @author Oleg Nenashev <nenashev@synopsys.com>
 */
public class OwnershipPlugin extends Plugin {
    private boolean enableSlaveOwnership = false;
    private boolean enableJobOwnership = true;
    private List<OwnershipAction> pluginActions = new ArrayList<OwnershipAction>();
    
    @Override 
    public void start() throws Exception {
	super.load();
        ReinitActionsList();
	Hudson.getInstance().getActions().addAll(pluginActions);
    }
    
    @Override 
    public void configure(StaplerRequest req, JSONObject formData)
	    throws IOException, ServletException, Descriptor.FormException {
	Hudson.getInstance().getActions().removeAll(pluginActions);
        
        enableSlaveOwnership = false;
        enableJobOwnership = true;
              
        ReinitActionsList();
        	
	save();
        Hudson.getInstance().getActions().addAll(pluginActions);
    }
    
    public void ReinitActionsList()
    {
        pluginActions.clear();
        /**if (enableJobOwnership)
        {
            pluginActions.add(new JobOwnerJobAction());
        }*/
    }
}
