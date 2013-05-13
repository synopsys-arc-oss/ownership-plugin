/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.synopsys.arc.jenkins.plugins.ownership;

import hudson.model.Actionable;

/**
 * Adapter for typical ownership operations with different item types
 * @param <TObjectType> Type of object, for which ownership should be resolved
 * @author Oleg Nenashev <nenashev@synopsys.com>
 * @since 0.0.2
 */
public interface IOwnershipHelper<TObjectType extends Actionable>  {
    public String getOwner(TObjectType item);
    
    public boolean isOwnerExists(TObjectType item);
    
    public String getOwnerLongString(TObjectType item);
}
