/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.synopsys.arc.jenkins.plugins.ownership;

import hudson.model.Actionable;

/**
 * Abstract class for ownership actions, which describe item
 * @author Oleg Nenashev <nenashev@synopsys.com>
 * @since 0.0.2
 */
public abstract class ItemOwnershipAction<TObjectType extends Actionable> 
    extends OwnershipAction implements IOwnershipItem<TObjectType> {
    
    private TObjectType describedItem;
    
    /**
     * Constructor
     * @param discribedItem Item, which is related to action
     */
    public ItemOwnershipAction(TObjectType discribedItem)
    {
        this.describedItem = discribedItem;
    }
     
    @Override
    public final TObjectType getDescribedItem()
    {
        return describedItem;
    }
}
