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
    public static String format(User user)
    {
        return user.getFullName()+" ("+user.getId()+")";
    }
}
