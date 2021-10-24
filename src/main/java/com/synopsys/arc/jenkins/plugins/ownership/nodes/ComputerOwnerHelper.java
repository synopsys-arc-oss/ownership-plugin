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
package com.synopsys.arc.jenkins.plugins.ownership.nodes;

import com.synopsys.arc.jenkins.plugins.ownership.OwnershipDescription;
import com.synopsys.arc.jenkins.plugins.ownership.OwnershipPlugin;
import com.synopsys.arc.jenkins.plugins.ownership.util.AbstractOwnershipHelper;
import hudson.Extension;
import hudson.model.Computer;
import hudson.model.Node;
import hudson.model.User;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import hudson.security.Permission;
import org.jenkinsci.plugins.ownership.model.OwnershipHelperLocator;
import org.jenkinsci.plugins.ownership.model.OwnershipInfo;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/**
 * Provides ownership helper for {@link Computer}.
 * The class implements a wrapper of {@link NodeOwnerHelper}.
 * @author Oleg Nenashev
 */
public class ComputerOwnerHelper extends AbstractOwnershipHelper<Computer> {

    static final ComputerOwnerHelper INSTANCE = new ComputerOwnerHelper();

    public static ComputerOwnerHelper getInstance() {
        return INSTANCE;
    }
        
    @Override
    public OwnershipDescription getOwnershipDescription(@Nonnull Computer item) {
        // TODO: This method impl is a performance hack. May be replaced by getOwnershipInfo() in 1.0
        Node node = item.getNode();      
        return node != null 
                ? NodeOwnerHelper.Instance.getOwnershipDescription(node)
                : OwnershipDescription.DISABLED_DESCR; // No node - no ownership
    }

    @Override
    public OwnershipInfo getOwnershipInfo(Computer item) {
        Node node = item.getNode();      
        return node != null 
                ? NodeOwnerHelper.Instance.getOwnershipInfo(node)
                : OwnershipInfo.DISABLED_INFO;
    }

    @Override
    public Collection<User> getPossibleOwners(@Nonnull Computer computer) {
        Node node = computer.getNode();
        return node != null 
                ? NodeOwnerHelper.Instance.getPossibleOwners(node)
                : Collections.<User>emptyList();
    }  
    
    public static void setOwnership(@Nonnull Computer computer, 
            @CheckForNull OwnershipDescription descr) throws IOException {
        Node node = computer.getNode();
        if (node == null) {
            throw new IOException("Cannot set ownership. Probably, the node has been renamed or deleted.");
        }
        
        NodeOwnerHelper.setOwnership(node, descr);
    }

    @Override
    public Permission getRequiredPermission() {
        return OwnershipPlugin.MANAGE_SLAVES_OWNERSHIP;
    }

    @Override
    public boolean hasLocallyDefinedOwnership(@Nonnull Computer computer) {
        Node node = computer.getNode();
        if (node == null) {
            // Node is not defined => permission is detached
            return false;
        }
        return NodeOwnerHelper.Instance.hasLocallyDefinedOwnership(node);
    }

    @Override
    public String getItemTypeName(Computer item) {
        return "computer";
    }

    @Override
    public String getItemDisplayName(Computer item) {
        return item.getDisplayName();
    }

    @Override
    public String getItemURL(Computer item) {
        //TODO: Absolute URL
        return item.getUrl();
    }

    @Extension
    @Restricted(NoExternalUse.class)
    public static class LocatorImpl extends OwnershipHelperLocator<Computer> {

        @Override
        public AbstractOwnershipHelper<Computer> findHelper(Object item) {
            if (item instanceof Computer) {
                return INSTANCE;
            }
            return null;
        }
    }
}
