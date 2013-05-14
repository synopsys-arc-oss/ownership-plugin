/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.synopsys.arc.jenkins.plugins.ownership;

import hudson.model.User;
import java.util.Collection;
import net.sf.json.JSONObject;

/**
 * Contains description of item's ownership 
 * @author Oleg Nenashev <nenashev@synopsys.com>
 * @since 0.0.3
 */
public class OwnershipDescription {
    /**
     * Disabled description, which means that ownership is disabled
     */
    public static final OwnershipDescription DISABLED_DESCR = new OwnershipDescription(false, null);
    
    /**
     * Indicates if ownership is enabled
     */
    boolean ownershipEnabled;
    
    /**
     * UserId of primary user
     */
    String primaryOwnerId;
    
    /**
     * @deprecated Not implemented in current version
     */
    Collection<String> coownersIds;

    /**
     * Constructor
     * @param primaryOwnerId userId of primary owner 
     */
    public OwnershipDescription(boolean ownershipEnabled, String primaryOwnerId) {
        this.ownershipEnabled = ownershipEnabled;
        this.primaryOwnerId = primaryOwnerId;
        this.coownersIds = null;
    }

    /**
     * Constructor
     * @param primaryOwnerId userId of primary owner 
     * @param coownersIds userIds of secondary owners
     */
    public OwnershipDescription(boolean ownershipEnabled, String primaryOwnerId, Collection<String> coownersIds) {
        this.ownershipEnabled = ownershipEnabled;
        this.primaryOwnerId = primaryOwnerId;
        this.coownersIds = coownersIds;
    }

    /**
     * Check if ownership is enabled
     * @return true if ownership is enabled
     */
    public boolean isOwnershipEnabled() {
        return ownershipEnabled;
    }

    /**
     * Gets id of primary owner
     * @return userId of primary owner 
     */
    public String getPrimaryOwnerId() {
        return ownershipEnabled ? primaryOwnerId :  User.getUnknown().getId();
    }
    
    public User getPrimaryOwner() {
        return User.get(primaryOwnerId, false, null);
    }

    /**
     * Gets list of coowners
     * @deprecated Not implemented in current version
     * @return Collection of coowners
     */
    public Collection<String> getCoownersIds() {
        return coownersIds;
    }
    
    public static OwnershipDescription Parse(JSONObject formData, String blockName)
    {
        Object blockObject = formData.get( blockName );
        boolean ownershipIsEnabled = false;
        String primaryOwner = null;
        
        if( blockObject != null ) {
            ownershipIsEnabled = true;          
            JSONObject debugJSON = (JSONObject)blockObject;

            // Read primary owner
            primaryOwner = debugJSON.getString( "primaryOwner" );
            
            //TODO: read coowners
        }
        
        return new OwnershipDescription(ownershipIsEnabled, primaryOwner);
    }
    
    /**
     * Check if User is owner
     * @param user User
     * @param acceptCoowners Check if user belongs to coowners
     * @return true if User belongs to primary owners (and/or coowners)
     */
    public boolean isOwner(User user, boolean acceptCoowners)
    {
        return user != null && user == getPrimaryOwner();
                
        //TODO: implement co-owners
    }
    
    public boolean hasPrimaryOwner()
    {
        return ownershipEnabled && getPrimaryOwner() != null;
    }
    
    public boolean isPrimaryOwner(User user)
    {
        return user != null && user == getPrimaryOwner();
    }
}
