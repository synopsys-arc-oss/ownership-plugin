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
public class UserComparator implements java.util.Comparator<User>
{
    @Override
    public int compare(User o1, User o2) {
        return o1.getFullName().compareTo(o2.getFullName());
    }
}
