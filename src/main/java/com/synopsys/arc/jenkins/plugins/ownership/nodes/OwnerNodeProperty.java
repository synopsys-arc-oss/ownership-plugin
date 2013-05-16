/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.synopsys.arc.jenkins.plugins.ownership.nodes;

import com.synopsys.arc.jenkins.plugins.ownership.IOwnershipHelper;
import com.synopsys.arc.jenkins.plugins.ownership.IOwnershipItem;
import com.synopsys.arc.jenkins.plugins.ownership.OwnershipDescription;
import hudson.Extension;
import hudson.model.Computer;
import hudson.model.Descriptor;
import hudson.model.Node;
import hudson.slaves.NodeProperty;
import hudson.slaves.NodePropertyDescriptor;
import jenkins.model.Jenkins;
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
    public OwnerNodeProperty(Node node, OwnershipDescription ownership) {
         setNode(node);
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
            Node owner = getNodePropertyOwner(req);
            OwnershipDescription descr = OwnershipDescription.Parse(formData, "slaveOwnership");
            return descr.isOwnershipEnabled() ? new OwnerNodeProperty(owner, descr) : null;       
        }
        
        
        
        /**
         * Gets Node, which is being configured by StaplerRequest
         * @remarks Workaround for
         * @param req StaplerRequest (ex, from NodePropertyDescriptor::newInstance())
         * @return Instance of the node, which is being configured
         * 
         * @since 0.0.5
         * @author Oleg Nenashev <nenashev@synopsys.com>
         */
        private static Node getNodePropertyOwner(StaplerRequest req)
        {                
           // Check format;
           String[] items = req.getOriginalRequestURI().split("/");
           if (items.length < 4)
           { 
               assert false : "Unexpected format of the request. "
                   + "Supported format is /computer/${NODE_NAME}/"+STAPLER_CONFIGURE_REQ_NAME;
               return null;
           }
           assert items[items.length-1].equals(STAPLER_CONFIGURE_REQ_NAME) : "Unexpected request name.";
       
           // Get node from Jenkins
           String nodeName = items[items.length-2];        
           return Jenkins.getInstance().getNode(nodeName); 
        }
        private static final String STAPLER_CONFIGURE_REQ_NAME = "configSubmit";
        
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
    protected final void setNode(Node node) {
        super.setNode(node); //To change body of generated methods, choose Tools | Templates.
    } 
}
