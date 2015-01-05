/*
 * The MIT License
 *
 * Copyright 2015 Oleg Nenashev <o.v.nenashev@gmail.com>.
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

package com.synopsys.arc.jenkins.plugins.ownership.util.mail;

import com.synopsys.arc.jenkins.plugins.ownership.IOwnershipHelper;
import com.synopsys.arc.jenkins.plugins.ownership.Messages;
import com.synopsys.arc.jenkins.plugins.ownership.OwnershipDescription;
import com.synopsys.arc.jenkins.plugins.ownership.OwnershipPlugin;
import com.synopsys.arc.jenkins.plugins.ownership.util.UserStringFormatter;
import hudson.model.User;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import jenkins.model.Jenkins;

/**
 *
 * @author Oleg Nenashev <o.v.nenashev@gmail.com>
 */
public class OwnershipMailHelper {

    private static final MailFormatter MAIL_FORMATTER = new MailFormatter();
    
    private static enum Mode { ContactOwners, ContactAdmins };

    /**
     * Generate a mailto URL, which allows to contact owners.
     * This URL should define default subject and body.
     * @param item 
     * @return MailTo URL or null if it is not available
     * @since 0.6
     */
    public static <TObjectType> String getContactOwnersMailToURL (TObjectType item, 
            IOwnershipHelper<TObjectType> helper) {
        return getMailToURL(item, helper, Mode.ContactOwners);
    }
    
    public static <TObjectType> String getContactAdminsMailToURL (TObjectType item, 
            IOwnershipHelper<TObjectType> helper) {
        return getMailToURL(item, helper, Mode.ContactAdmins);
    }
    
    private static <TObjectType> String getMailToURL (TObjectType item, 
            IOwnershipHelper<TObjectType> helper, Mode mode) {
    
        final Jenkins instance = Jenkins.getInstance();
        if (instance == null) {
            // Cannot construct e-mail if Jenkins has not been initialized
            return null;
        }
        OwnershipPlugin plugin = instance.getPlugin(OwnershipPlugin.class);
        if (plugin == null) {
            // Plugin is not initialized
            assert false : "Ownership plugin has not been loaded yet";
            return null;
        }
        
        OwnershipDescription ownershipDescription = helper.getOwnershipDescription(item);
        if (!ownershipDescription.isOwnershipEnabled()) {
            return null;
        }

        List<String> to = new LinkedList<String>();
        List<String> cc = new LinkedList<String>();
        
        // to - job owner
        if (ownershipDescription.hasPrimaryOwner()) {
            String email = UserStringFormatter.formatEmail(ownershipDescription.getPrimaryOwnerId());
            if (email != null) {
                switch (mode) {
                   case ContactAdmins:
                       cc.add(email);
                       break;
                   default:
                       to.add(email);
                }
            }
        }

        // cc - job co-owners
        final Set<String> coOwners = ownershipDescription.getCoownersIds();
        if (!coOwners.isEmpty()) {
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
        final String relativeUrl = helper.getItemURL(item);
        final String itemUrl = relativeUrl != null ? instance.getRootUrl()+relativeUrl : "unknown";
        final String emailSubjectPrefix = plugin.getConfiguration().getMailOptions().getEmailSubjectPrefix();
        
        //TODO: make header configurable
        String subject = Messages.OwnershipPlugin_FloatingBox_ContactOwners_EmailSubjectTemplate
            (emailSubjectPrefix, helper.getItemSummary(item));
        
        
        String body;
        switch (mode) {
            case ContactOwners:
                body = Messages.OwnershipPlugin_FloatingBox_ContactOwners_EmailBodyTemplate(
                       helper.getItemSummary(item), itemUrl, userName);
                break;
            case ContactAdmins:
                final String adminEmail = plugin.getConfiguration().getMailOptions().getAdminsContactEmail();
                to.add(adminEmail);
                final String adminEmailPrefix = plugin.getConfiguration().getMailOptions().getAdminsEmailPrefix();
                body = Messages.OwnershipPlugin_FloatingBox_ContactAdmins_EmailBodyTemplate(
                       adminEmailPrefix, itemUrl, userName);
                break;
            default:
                assert false : "Unsupported mode " + mode;
                throw new IllegalStateException("Mode "+mode+" is unsupported");
        }
        

        try {
            final String formattedURL = MAIL_FORMATTER.createMailToString(
                to, cc, null, subject, body);
            return formattedURL;
        } catch (UnsupportedEncodingException ex) {
            throw new IllegalStateException("Unsupported encoding: "+MAIL_FORMATTER.getEncoding(), ex);
        }    
    }
}
