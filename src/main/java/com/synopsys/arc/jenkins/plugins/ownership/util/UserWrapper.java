/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.synopsys.arc.jenkins.plugins.ownership.util;

import hudson.model.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Implements wrapper, which allows to implement "non-existent" users
 * @author Oleg Nenashev <nenashev@synopsys.com>
 */
public class UserWrapper {
    boolean isUser;
    User user;
    String macro;
    public static final String USER_MACRO_PREFIX = "@";
    
    public UserWrapper(User user)
    {
        this.isUser = true;
        this.user = user;
    }
    
    /**
     * Creates Wrapper from user id or wrapper
     * @param userMacro 
     */
    public UserWrapper(String userMacro)
    {
        if (userMacro.startsWith(USER_MACRO_PREFIX))
        {
            this.isUser = false;
            this.macro = userMacro;
        }
        else
        {
            this.isUser = true;
            this.user = User.get(userMacro, false, null);
            //   throw new UnsupportedOperationException("User macro must start with prefix '"+USER_MACRO_PREFIX+"'");
        }
        
    }

    public boolean IsUser()
    {
        return isUser;
    }
    
    /**
     * Gets id of the user (calls User.getId() or returns macro)
     * @return 
     */
    public String getId()
    {
        return isUser ? user.getId() : macro;
    }
    
    @Override
    public String toString() {
        return isUser ? UserStringFormatter.format(user) : macro;
    }
    
    public boolean meetsMacro(String userId) {
        String comparedId;
        if (isUser)
        {
            if (user == null) return false;
            comparedId = user.getId();
        }
        else
        {
            User current = User.current();
            if (current == null) return false;
            comparedId = current.getId();
        }        

        return comparedId.equals(userId);
    }
    
    
}
