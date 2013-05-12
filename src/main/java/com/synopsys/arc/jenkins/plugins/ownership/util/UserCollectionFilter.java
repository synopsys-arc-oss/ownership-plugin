/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.synopsys.arc.jenkins.plugins.ownership.util;

import com.synopsys.arc.jenkins.plugins.ownership.util.userFilters.IUserFilter;
import hudson.model.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 *
 * @author Oleg Nenashev <nenashev@synopsys.com>
 */
public class UserCollectionFilter {
    public static Collection<User> filterUsers(Collection<User> input, boolean enableSort, IUserFilter ... filters)
    {   
        // Sort users
        UserComparator comparator = new UserComparator();
        LinkedList<User> userList = new LinkedList<User>(User.getAll());   
        if (enableSort)
        {
            Collections.sort(userList, comparator);
        }
        // Prepare new list
        Collection<User> res = new ArrayList<User>();
        for (User user : userList)
        {
            boolean meets = true;
            for (int i=0; meets && i < filters.length; i++)
            {
                meets = filters[i].filter(user);
            }
            
            if (meets)
            {
                res.add(user);
            }
        }
        return res;
    }
}
