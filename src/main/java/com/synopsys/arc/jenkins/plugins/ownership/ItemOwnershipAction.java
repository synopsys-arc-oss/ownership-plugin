/*
 * The MIT License
 *
 * Copyright 2013 Oleg Nenashev, Synopsys Inc.
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
package com.synopsys.arc.jenkins.plugins.ownership;

import hudson.model.Actionable;
import hudson.security.Permission;
import javax.annotation.Nonnull;

/**
 * Abstract class for ownership actions, which describes item at the floating box.
 * @author Oleg Nenashev
 * @param <TObjectType> A class, for which action is being created
 * @since 0.0.2
 */
public abstract class ItemOwnershipAction<TObjectType extends Actionable> 
    extends OwnershipAction implements IOwnershipItem<TObjectType> {
    
    private final @Nonnull TObjectType describedItem;
        
    /**
     * Constructor.
     * @param describedItem Item, which is related to the action
     */
    public ItemOwnershipAction(@Nonnull TObjectType describedItem)  {
        this.describedItem = describedItem;
    }
     
    @Nonnull
    @Override
    public final TObjectType getDescribedItem() {
        return describedItem;
    } 
    
    /**
     * Gets a permission for "Manage Ownership" action.
     * @return A permission to be checked
     */
    @Nonnull
    public abstract Permission getOwnerPermission();
    
    /**
     * Gets a permission for "Configure project-specific security" action.
     * @return A permission to be checked
     */
    @Nonnull
    public abstract Permission getProjectSpecificPermission();

    @Override
    public OwnershipDescription getOwnership() {
        return helper().getOwnershipDescription(describedItem);
    } 
}
