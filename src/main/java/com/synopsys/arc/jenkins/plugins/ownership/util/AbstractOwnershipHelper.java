/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.synopsys.arc.jenkins.plugins.ownership.util;

import com.synopsys.arc.jenkins.plugins.ownership.IOwnershipHelper;
import com.synopsys.arc.jenkins.plugins.ownership.OwnershipDescription;
import com.synopsys.arc.jenkins.plugins.ownership.OwnershipPlugin;
import hudson.model.User;

/**
 * Provides basic operations for typical helpers
 * @since 0.0.4
 * @author Oleg Nenashev <nenashev@synopsys.com>
 */
public abstract class AbstractOwnershipHelper<TObjectType>  
    implements IOwnershipHelper<TObjectType>  
{
    
    @Override
    public final String getDisplayName(User usr) {
        return UserStringFormatter.format(usr);
    } 
    
    @Override
    public final String getOwnerEmail(TObjectType item) {
        OwnershipDescription descr = getOwnershipDescription(item);
        User owner = descr.getPrimaryOwner();
        OwnershipPlugin plugin = OwnershipPlugin.Instance();
        
        if (owner == null || plugin == null)
        {
            return null;
        }             
        return owner.getId() + plugin.getEmailSuffix();
    }
    
    @Override
    public final String getOwnerLongString(TObjectType item) {
        OwnershipDescription descr = getOwnershipDescription(item);
        if (descr == null)
            return UserStringFormatter.UNKNOWN_USER_STRING;
             
        return descr.isOwnershipEnabled() 
                ? UserStringFormatter.format(descr.getPrimaryOwner()) 
                : UserStringFormatter.UNKNOWN_USER_STRING;
    }
    
    @Override
    public final String getOwner(TObjectType item) {
        OwnershipDescription descr = getOwnershipDescription(item);
        return (descr != null && descr.isOwnershipEnabled()) ? descr.getPrimaryOwnerId() : UserStringFormatter.UNKNOWN_USER_STRING;
    }    
    
    @Override
    public final boolean isOwnerExists(TObjectType item) {
        OwnershipDescription descr = getOwnershipDescription(item);
        return (descr != null && descr.isOwnershipEnabled()) ? descr.hasPrimaryOwner() : false;
    }
}
