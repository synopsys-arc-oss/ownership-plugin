/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.synopsys.arc.jenkins.plugins.ownership;

/**
 * Class provides basic methods for ownership handling
 * @param <TObjectType> Type of the described object
 * @author Oleg Nenashev <nenashev@synopsys.com>
 * @since 0.0.3
 */
public interface IOwnershipItem<TObjectType> {
    /**
     * Gets helper for typical operations
     * @return Helper
     */
    public abstract IOwnershipHelper<TObjectType> helper();
    
    /**
     * Gets item, which is being described by action
     * @return Described item
     */
    public TObjectType getDescribedItem();
}
