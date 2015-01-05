/*
 * The MIT License
 *
 * Copyright 2013 Oleg Nenashev <nenashev@synopsys.com>, Synopsys Inc.
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
package com.synopsys.arc.jenkins.plugins.ownership.util;

import com.synopsys.arc.jenkins.plugins.ownership.IOwnershipHelper;
import com.synopsys.arc.jenkins.plugins.ownership.Messages;
import com.synopsys.arc.jenkins.plugins.ownership.OwnershipDescription;
import com.synopsys.arc.jenkins.plugins.ownership.util.mail.MailFormatter;
import hudson.model.User;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import jenkins.model.Jenkins;

/**
 * Provides basic operations for ownership helpers.
 * @param <TObjectType> An item type, which is supported by the class.
 * @since 0.0.4
 * @author Oleg Nenashev <nenashev@synopsys.com>
 */
public abstract class AbstractOwnershipHelper<TObjectType>  
    implements IOwnershipHelper<TObjectType> {
    
    /**An empty collection of users*/
    protected final static Collection<User> EMPTY_USERS_COLLECTION = new ArrayList<User>(0);
    private static final MailFormatter MAIL_FORMATTER = new MailFormatter();
    
    @Override
    public final @Nonnull String getDisplayName(@CheckForNull User usr) {
        return UserStringFormatter.format(usr);
    } 
    
    @Override
    public final @CheckForNull String getOwnerEmail(@Nonnull TObjectType item) {
        OwnershipDescription descr = getOwnershipDescription(item);
        return  UserStringFormatter.formatEmail(descr.getPrimaryOwner());      
    }
      
    @Override
    public final @Nonnull String getOwnerLongString(@Nonnull TObjectType item) {
        OwnershipDescription descr = getOwnershipDescription(item);     
        return descr.isOwnershipEnabled() 
                ? UserStringFormatter.format(descr.getPrimaryOwner()) 
                : UserStringFormatter.UNKNOWN_USER_STRING;
    }
       
    @Override
    public final @Nonnull  String getOwner(@Nonnull TObjectType item) {
        OwnershipDescription descr = getOwnershipDescription(item);
        return (descr.isOwnershipEnabled()) 
                ? UserStringFormatter.formatShort(descr.getPrimaryOwnerId())
                : UserStringFormatter.UNKNOWN_USER_STRING;
    }    
    
    @Override
    public final boolean isOwnerExists(@Nonnull TObjectType item) {
        OwnershipDescription descr = getOwnershipDescription(item);
        return descr.isOwnershipEnabled() ? descr.hasPrimaryOwner() : false;
    }

    @Override
    public String getContactOwnersMailToURL(TObjectType item) {
        final Jenkins instance = Jenkins.getInstance();
        if (instance == null) {
            // Cannot construct e-mail if Jenkins has not been initialized
            return null;
        }

        OwnershipDescription ownershipDescription = getOwnershipDescription(item);
        if (!ownershipDescription.isOwnershipEnabled()) {
            return null;
        }

        // to - job owner
        List<String> to = null;
        if (ownershipDescription.hasPrimaryOwner()) {
            String email = UserStringFormatter.formatEmail(ownershipDescription.getPrimaryOwnerId());
            if (email != null) {
                to = Arrays.asList(email);
            }
        }

        // cc - job co-owners
        List<String> cc = null;
        final Set<String> coOwners = ownershipDescription.getCoownersIds();
        if (!coOwners.isEmpty()) {
            cc = new ArrayList<String>(coOwners.size());
            for (String coOwnerId : coOwners) {
                String email = UserStringFormatter.formatEmail(coOwnerId);
                if (email != null) {
                    cc.add(email);
                }
            }
        }

        // Prepare subject and body using formatters
        final User user = User.current();
        final String userName = (user != null && user != User.getUnknown())
                ? user.getFullName() : "TODO: user name";
        final String relativeUrl = getItemURL(item);
        final String itemUrl = relativeUrl != null ? instance.getRootUrl()+relativeUrl : "unknown";
        
        //TODO: make header configurable
        String subject = Messages.OwnershipPlugin_FloatingBox_ContactOwners_EmailSubjectTemplate
            ("[Jenkins] - On ", getItemSummary(item));
        String body = Messages.OwnershipPlugin_FloatingBox_ContactOwners_EmailBodyTemplate(
            getItemSummary(item), itemUrl, userName);

        try {
            final String formattedURL = MAIL_FORMATTER.createMailToString(
                to, cc, null, subject, body);
            return formattedURL;
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException("Unsupported encoding: "+MAIL_FORMATTER.getEncoding(), ex);
        }    
    }
}
