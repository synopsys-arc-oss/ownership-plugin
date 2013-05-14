/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.synopsys.arc.jenkins.plugins.ownership.nodes;

import com.synopsys.arc.jenkins.plugins.ownership.IOwnershipHelper;
import com.synopsys.arc.jenkins.plugins.ownership.IOwnershipItem;
import com.synopsys.arc.jenkins.plugins.ownership.OwnershipDescription;
import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Node;
import hudson.slaves.NodeProperty;
import hudson.slaves.NodePropertyDescriptor;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Implements owner property for Jenkins Nodes
 * 
 * @author Oleg Nenashev <nenashev@synopsys.com>
 * @since 0.0.3
 */
public class OwnerNodeProperty extends NodeProperty<Node> 
    implements IOwnershipItem<NodeProperty> {

    OwnershipDescription ownership;
    
    @DataBoundConstructor
    public OwnerNodeProperty( OwnershipDescription ownership) {
         this.ownership = ownership;
    }
    
    public OwnershipDescription getOwnership()
    {       
        return ownership;
    }
    
    @Override
    public IOwnershipHelper<NodeProperty> helper() {
        return NodeOwnerPropertyHelper.Instance;
    }

    @Override
    public NodeProperty getDescribedItem() {
        return this;
    }
      
    @Extension
    public static class DescriptorImpl extends NodePropertyDescriptor {
        
        @Override
        public NodeProperty<?> newInstance( StaplerRequest req, JSONObject formData ) throws Descriptor.FormException {
                
            OwnershipDescription descr = OwnershipDescription.Parse(formData, "slaveOwnership");
            return descr.isOwnershipEnabled() ? new OwnerNodeProperty(descr) : null;
        }

        @Override
        public String getDisplayName() {
                return "Slave Owner";
        }

        @Override
        public boolean isApplicable( Class<? extends Node> Type ) {
                return true;
        }           
    }

    @Override
    protected void setNode(Node node) {
        super.setNode(node); //To change body of generated methods, choose Tools | Templates.
    } 
}
