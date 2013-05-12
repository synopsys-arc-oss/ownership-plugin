/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.synopsys.arc.jenkins.plugins.ownership.util.userFilters;

import hudson.model.User;
import hudson.security.AccessControlled;
import hudson.security.Permission;
import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;

/**
 * Filters user according to access rights to specified item
 * @author Oleg Nenashev <nenashev@synopsys.com>
 */
public class AccessRightsFilter implements IUserFilter {
    AccessControlled item;
    Permission permission;

    public AccessRightsFilter(AccessControlled item, Permission permission) {
        this.item = item;
        this.permission = permission;
    }
   
    public boolean filter(User user) {
        if (user == null) return false;
        
        boolean res = false;
        Authentication auth = user.impersonate();
        SecurityContext context = null;
        try {
            context = hudson.security.ACL.impersonate(auth);
            res = item.hasPermission(permission);
        }
        finally {
            if (context != null)
                SecurityContextHolder.setContext(context);
        }
        return  res;
    }
    
}
