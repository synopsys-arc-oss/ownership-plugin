/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.synopsys.arc.jenkins.plugins.ownership;

import hudson.model.User;
import java.util.Collection;

/**
 * Adapter for typical ownership operations with different item types
 * @param <TObjectType> Type of object, for which ownership should be resolved
 * @author Oleg Nenashev <nenashev@synopsys.com>
 * @since 0.0.2
 */
public interface IOwnershipHelper<TObjectType>  {
    /**
     * Get ownerId of the item
     * @param item Item to be describes
     * @return userId
     */
    public String getOwner(TObjectType item);
    
    /**
     * Check if item has owners
     * @param item Item to be checked
     * @return true if item has owners
     */
    public boolean isOwnerExists(TObjectType item);
    
    /**
     * Get Display string of the owner (and coowners)
     * @param item Item to be described
     * @return User description string
     */
    public String getOwnerLongString(TObjectType item);
    
    /**
     * Gets ownership description of the requested item
     * @param item Item to be described
     * @return Ownership description
     * @since 0.0.3
     */
    public OwnershipDescription getOwnershipDescription(TObjectType item);
    
    
    /**
     * Get list of users, who can be item's owner
     * @param item Item to be described
     * @return List of users, who can be item's owner
     * @since 0.0.3
     */
    public Collection<User> getPossibleOwners(TObjectType item);
    
    /**
     * Gets User's name for visualization
     * @param usr User
     * @return String for display
     */
    public String getDisplayName(User usr);
}
