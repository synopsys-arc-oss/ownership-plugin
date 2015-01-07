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

import com.synopsys.arc.jenkins.plugins.ownership.OwnershipPlugin;
import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Stores mailing options for {@link OwnershipPlugin}.
 * @author Oleg Nenashev <o.v.nenashev@gmail.com>
 * @since 0.6
 */
public class MailOptions implements Describable<MailOptions> {
    
    private final @CheckForNull String emailSubjectPrefix;
    private final @CheckForNull String emailListSeparator;
    private final @CheckForNull String adminsContactEmail;
    private final @CheckForNull String adminsEmailPrefix;
    
    private static final String DEFAULT_SUBJECT_PREFIX = "[Jenkins] - ";
    private static final String DEFAULT_LIST_SEPARATOR = ";";
    private static final String DEFAULT_ADMINS_EMAIL_PREFIX = "Dear Jenkins admins,";
     
    public static final MailOptions DEFAULT = new MailOptions(DEFAULT_SUBJECT_PREFIX, null, 
            DEFAULT_ADMINS_EMAIL_PREFIX, DEFAULT_LIST_SEPARATOR);
    
    @DataBoundConstructor
    public MailOptions(String emailSubjectPrefix, String adminsContactEmail, String adminsEmailPrefix, 
            String emailListSeparator) {
        this.emailSubjectPrefix = hudson.Util.fixEmptyAndTrim(emailSubjectPrefix);
        this.adminsContactEmail = hudson.Util.fixEmptyAndTrim(adminsContactEmail);
        this.adminsEmailPrefix = hudson.Util.fixEmptyAndTrim(adminsEmailPrefix);
        this.emailListSeparator=hudson.Util.fixEmptyAndTrim(emailListSeparator);
    }

    public @Nonnull String getEmailSubjectPrefix() {
        return emailSubjectPrefix != null ? emailSubjectPrefix : DEFAULT_SUBJECT_PREFIX;
    }
    
    public @Nonnull String getEmailListSeparator() {
        return emailListSeparator != null ? emailListSeparator : DEFAULT_LIST_SEPARATOR;
    }

    public @CheckForNull String getAdminsContactEmail() {
        return adminsContactEmail;
    }

    public @Nonnull String getAdminsEmailPrefix() {
        return adminsEmailPrefix != null ? adminsEmailPrefix : DEFAULT_ADMINS_EMAIL_PREFIX;
    }
   
    @Extension
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    @Override
    public DescriptorImpl getDescriptor() {
        return DESCRIPTOR;
    }
  
    public static class DescriptorImpl extends Descriptor<MailOptions> {
        
        @Override
        public String getDisplayName() {
            return "N/A";
        }
    }
}
