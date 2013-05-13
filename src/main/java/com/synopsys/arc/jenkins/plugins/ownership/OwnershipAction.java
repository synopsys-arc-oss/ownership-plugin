/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.synopsys.arc.jenkins.plugins.ownership;

import hudson.model.Action;

/**
 *
 * @author Oleg Nenashev <nenashev@synopsys.com>
 */
public abstract class OwnershipAction implements Action {
    public static final String ICON_FILENAME = "graph.gif";
    public static final String URL_NAME = "Ownership";
    
    @Override
    public String getIconFileName() {
         return ICON_FILENAME; 
    }
    
    @Override
    public String getUrlName() {
        return URL_NAME;
    }
}
