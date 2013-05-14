/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.synopsys.arc.jenkins.plugins.ownership.util;

import hudson.model.User;

/**
 *
 * @author Oleg Nenashev <nenashev@synopsys.com>
 */
public class UserStringFormatter {
    
    public static final String UNKNOWN_USER_STRING="N/A";
    
    /**
     * Formats string to be displayed
     * @param user User
     * @return Formatted string
     */
    public static String format(User user)
    {
        return user !=null ? user.getFullName()+" ("+user.getId()+")" : UNKNOWN_USER_STRING;
    }
}
