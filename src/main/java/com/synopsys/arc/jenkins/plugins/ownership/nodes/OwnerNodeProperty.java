/*
 * The MIT License
 *
 * Copyright 2013 Oleg Nenashev <nenashev@synopsys.com>, Synopsys Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.synopsys.arc.jenkins.plugins.ownership.nodes;

import com.synopsys.arc.jenkins.plugins.ownership.IOwnershipHelper;
import com.synopsys.arc.jenkins.plugins.ownership.IOwnershipItem;
import com.synopsys.arc.jenkins.plugins.ownership.Messages;
import com.synopsys.arc.jenkins.plugins.ownership.OwnershipDescription;
import com.synopsys.arc.jenkins.plugins.ownership.OwnershipPlugin;
import com.synopsys.arc.jenkins.plugins.ownership.util.ui.OwnershipLayoutFormatter;
import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Node;
import hudson.slaves.NodeProperty;
import hudson.slaves.NodePropertyDescriptor;
import hudson.slaves.SlaveComputer;
import java.util.List;
import javax.annotation.CheckForNull;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.Ancestor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Implements owner property for Jenkins Nodes.
 * @author Oleg Nenashev <nenashev@synopsys.com>
 * @deprecated Will be removed in future versions
 * @since 0.0.3
 */
public class OwnerNodeProperty extends NodeProperty<Node> 
    implements IOwnershipItem<NodeProperty> {

    private OwnershipDescription ownership;
    private String nodeName; 
    
    @DataBoundConstructor
    public OwnerNodeProperty(NodeOwnerWrapper slaveOwnership) {
        this(null, (slaveOwnership != null) ? slaveOwnership.getDescription() : null);
    }
    
    public OwnerNodeProperty(Node node, OwnershipDescription ownership) {
         setNode(node);
         //FIXME: remove hack with owner
         this.nodeName = (node != null) ? node.getNodeName() : null;
         this.ownership = (ownership != null) ? ownership : OwnershipDescription.DISABLED_DESCR;
    }
    
    @Override
    public OwnershipDescription getOwnership() {             
        return ownership != null ? ownership : OwnershipDescription.DISABLED_DESCR;
    }
    
    public void setOwnershipDescription(OwnershipDescription descr) {
        ownership = descr;
        getDescriptor().save();
    }
    
    @CheckForNull
    public Node getNode() {
        if (node == null) {
            setNode(Jenkins.getInstance().getNode(nodeName));
        }
        return node;
    }

    @Override
    public NodePropertyDescriptor getDescriptor() {
        return super.getDescriptor(); 
    }
            
    @Override
    public IOwnershipHelper<NodeProperty> helper() {       
        return NodeOwnerPropertyHelper.Instance;
    }

    @Override
    public NodeProperty<?> reconfigure(StaplerRequest req, JSONObject form) throws Descriptor.FormException {
        return new OwnerNodeProperty(DescriptorImpl.getNodePropertyOwner(req), getOwnership());
    }
     
    @Override
    public NodeProperty getDescribedItem() {
        return this;
    }
    
    public OwnershipLayoutFormatter<Node> getLayoutFormatter() {
        return OwnershipPlugin.getInstance().getOwnershipLayoutFormatterProvider().getLayoutFormatter(getNode());
    }  
      
    @Extension
    public static class DescriptorImpl extends NodePropertyDescriptor {       
        /**
         * Gets Node, which is being configured by StaplerRequest
         * @remarks Workaround for
         * @param req StaplerRequest (ex, from NodePropertyDescriptor::newInstance())
         * @return Instance of the node, which is being configured (or null)
         * 
         * @since 0.0.5
         * @author Oleg Nenashev <nenashev@synopsys.com>
         */
        private static Node getNodePropertyOwner(StaplerRequest req)
        {                
            List<Ancestor> ancestors = req.getAncestors();
            if (ancestors.isEmpty()) {
                assert false : "StaplerRequest is empty";
                return null;
            }
            
            Object node = ancestors.get(ancestors.size()-1).getObject();
            if (SlaveComputer.class.isAssignableFrom(node.getClass())) {
                return ((SlaveComputer)node).getNode();
            } else {
                assert false : "StaplerRequest should have Node ancestor";
                return null;
            }
        }
        
        @Override
        public String getDisplayName() {
                return Messages.NodeOwnership_Config_SectionTitle()+". (just a stub, use \"Manage ownership\" page to configure)";
        }

        @Override
        public boolean isApplicable( Class<? extends Node> Type ) {
                return true;
        }         
    }
}
