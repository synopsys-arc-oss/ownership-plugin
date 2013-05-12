/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.synopsys.arc.jenkins.plugins.ownership.util.userFilters;

import hudson.model.User;

/**
 *
 * @author Oleg Nenashev <nenashev@synopsys.com>
 */
public interface IUserFilter {
    /**
     * Filters users
     * @param user User to be checked
     * @return true if User passed filter
     */
    boolean filter(User user);
}
