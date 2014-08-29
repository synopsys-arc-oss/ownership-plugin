/*
 * The MIT License
 *
 * Copyright 2014 Oleg Nenashev <o.v.nenashev@gmail.com>.
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

package com.synopsys.arc.jenkins.plugins.ownership.util.ui;

import com.synopsys.arc.jenkins.plugins.ownership.OwnershipPlugin;
import com.synopsys.arc.jenkins.plugins.ownership.util.HTMLFormatter;
import javax.annotation.Nonnull;

/**
 * Formats layouts for UI interfaces.
 * This extension is being used by {@link OwnershipLayoutFormatterProvider}.
 * @param <TObjectType> Type of object, for which ownership should be resolved
 * @see OwnershipLayoutFormatterProvider
 * @since 0.5
 * @author Oleg Nenashev <o.v.nenashev@gmail.com>
 */
public abstract class OwnershipLayoutFormatter<TObjectType> {
    
    public abstract String formatUser(@Nonnull TObjectType item, String userId);
    
    public String formatOwner(@Nonnull TObjectType item, String userId) {
        return formatUser(item, userId);
    }
    
    public String formatCoOwner(@Nonnull TObjectType item, String userId) {
        return formatUser(item, userId);
    }
      
    /**
     * Default user formatter for {@link OwnershipPlugin}.
     * @param <TObjectType> 
     */
    public static class DefaultJobFormatter<TObjectType> extends OwnershipLayoutFormatter<TObjectType> {

        @Override
        public String formatUser(TObjectType item, String userId) {
            final String userURI = HTMLFormatter.formatUserURI(userId, true);
            final String userEmail = HTMLFormatter.formatEmailURI(userId);
            final String userInfoHTML = userURI + (userEmail != null ? " " + userEmail : "");
            return userInfoHTML;
        }
    }
}
