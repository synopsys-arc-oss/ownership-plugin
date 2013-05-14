/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.synopsys.arc.jenkins.plugins.ownership.nodes;

import com.synopsys.arc.jenkins.plugins.ownership.OwnershipDescription;
import com.synopsys.arc.jenkins.plugins.ownership.util.AbstractOwnershipHelper;
import com.synopsys.arc.jenkins.plugins.ownership.util.UserCollectionFilter;
import com.synopsys.arc.jenkins.plugins.ownership.util.userFilters.AccessRightsFilter;
import com.synopsys.arc.jenkins.plugins.ownership.util.userFilters.IUserFilter;
import hudson.model.Computer;
import hudson.model.Node;
import hudson.model.User;
import hudson.slaves.NodeProperty;
import java.util.Collection;

/**
 * Provides helper for Node owner
 * @todo Add Bug reference
 * @deprecated Bug in Jenkins => doesn't work
 * @since 0.0.3
 * @author Oleg Nenashev <nenashev@synopsys.com>
 * @see OwnerNodeProperty
 */
public class NodeOwnerHelper extends AbstractOwnershipHelper<Node> {

    static final NodeOwnerHelper Instance = new NodeOwnerHelper();

    /**
     * Gets OwnerNodeProperty from job if possible
     * @param node Node
     * @return OwnerNodeProperty or null
     */
    private static OwnerNodeProperty getOwnerProperty(Node node)
    {
        NodeProperty prop = node.getNodeProperties().get(OwnerNodeProperty.class);
        return prop != null ? (OwnerNodeProperty)prop : null;
    }
        
    @Override
    public OwnershipDescription getOwnershipDescription(Node item) {
        OwnerNodeProperty prop = getOwnerProperty(item);
        return prop != null ? prop.getOwnership() : OwnershipDescription.DISABLED_DESCR;
    }
    
    @Override
    public Collection<User> getPossibleOwners(Node item) {
        IUserFilter filter = new AccessRightsFilter(item, Computer.CONFIGURE);
        Collection<User> res = UserCollectionFilter.filterUsers(User.getAll(), true, filter);
        return res;
    }   
}
