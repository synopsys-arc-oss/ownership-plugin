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
package com.synopsys.arc.jenkins.plugins.ownership.security.itemspecific;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Job;
import hudson.model.JobPropertyDescriptor;
import hudson.security.AuthorizationMatrixProperty;
import hudson.security.GlobalMatrixAuthorizationStrategy;
import hudson.security.Permission;
import hudson.util.FormValidation;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Implements item-specific property map.
 * @author Oleg Nenashev <nenashev@synopsys.com>, Synopsys Inc.
 * @since 0.3
 */
public class ItemSpecificSecurityGlobal extends ItemSpecificSecurity {
    private GlobalMatrixAuthorizationConfig permissionsMatrix;
    
    @DataBoundConstructor
    public ItemSpecificSecurityGlobal(GlobalMatrixAuthorizationConfig permissionsMatrix) {
        super(null);
        this.permissionsMatrix = permissionsMatrix;
    }

    @Override
    public GlobalMatrixAuthorizationConfig getPermissionsMatrix() {
        return permissionsMatrix; 
    }
    
    @Override
    public GlobalItemSpecificDescriptor getDescriptor() {
        return GLOBAL_DESCRIPTOR; 
    }
        
    public static final GlobalItemSpecificDescriptor GLOBAL_DESCRIPTOR = new GlobalItemSpecificDescriptor();
    
    @Extension
    public static class GlobalItemSpecificDescriptor extends ItemSpecificDescriptor {
        @Override
        public ItemSpecificSecurityGlobal newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            GlobalMatrixAuthorizationConfig prop = null;
            if (formData.containsKey("permissionsMatrix")) {
                Descriptor d= Jenkins.getInstance().getDescriptor(GlobalMatrixAuthorizationConfig.class);
                prop = (GlobalMatrixAuthorizationConfig)d.newInstance(req, formData.getJSONObject("permissionsMatrix"));
            }
            return new ItemSpecificSecurityGlobal(prop);
        }
    }
    
    public static class GlobalMatrixAuthorizationConfig extends AuthorizationMatrixProperty {
        public GlobalMatrixAuthorizationConfig(Map<Permission, Set<String>> grantedPermissions) {
            super(grantedPermissions);
        }

        @Override
        public JobPropertyDescriptor getDescriptor() {
            return DESCRIPTOR;
        }
        
        public static final AuthorizationMatrixPropertyDescriptor DESCRIPTOR = 
                new AuthorizationMatrixPropertyDescriptor();
        
        @Extension
        public static class AuthorizationMatrixPropertyDescriptor extends AuthorizationMatrixProperty.DescriptorImpl {

            @Override
            public GlobalMatrixAuthorizationConfig newInstance(StaplerRequest req, JSONObject formData) throws FormException {
                AuthorizationMatrixProperty prop = (AuthorizationMatrixProperty)super.newInstance(req, formData);
                return new GlobalMatrixAuthorizationConfig(prop.getGrantedPermissions());
            }
        
            @Override
            public FormValidation doCheckName(Job job, String string) throws IOException, ServletException {
                return GlobalMatrixAuthorizationStrategy.DESCRIPTOR.doCheckName_(string, Jenkins.getInstance(), Jenkins.ADMINISTER); 
            }          
        }
    }  
}
