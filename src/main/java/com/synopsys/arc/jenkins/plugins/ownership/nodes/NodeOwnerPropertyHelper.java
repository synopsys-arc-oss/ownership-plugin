/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.synopsys.arc.jenkins.plugins.ownership.nodes;

import com.synopsys.arc.jenkins.plugins.ownership.OwnershipDescription;
import com.synopsys.arc.jenkins.plugins.ownership.util.AbstractOwnershipHelper;
import com.synopsys.arc.jenkins.plugins.ownership.util.UserCollectionFilter;
import hudson.model.User;
import hudson.slaves.NodeProperty;
import java.util.Collection;

/**
 * Provides helper for Node owner
 * @since 0.0.3
 * @author Oleg Nenashev <nenashev@synopsys.com>
 * @see OwnerNodeProperty, NodeOwnerHelper
 */
public class NodeOwnerPropertyHelper extends AbstractOwnershipHelper<NodeProperty> {

    static final NodeOwnerPropertyHelper Instance = new NodeOwnerPropertyHelper();

    /**
     * Gets OwnerNodeProperty from job if possible
     * @param node Node
     * @return OwnerNodeProperty or null
     */
    private static OwnerNodeProperty getOwnerProperty(NodeProperty node)
    {
        return (OwnerNodeProperty)node;
    }
      
    @Override
    public OwnershipDescription getOwnershipDescription(NodeProperty item) {
        OwnerNodeProperty prop = getOwnerProperty(item);
        OwnershipDescription descr = (prop != null) ? prop.getOwnership() : null;
        return descr != null ? descr : OwnershipDescription.DISABLED_DESCR;
    }
    
    @Override
    public Collection<User> getPossibleOwners(NodeProperty item) {
        //FIXME: Fix after fix of bug at Jenkins
       // IUserFilter filter = new AccessRightsFilter(item, Computer.CONFIGURE);
        Collection<User> res = UserCollectionFilter.filterUsers(User.getAll(), true);
        return res;
    }   
    
      
}
