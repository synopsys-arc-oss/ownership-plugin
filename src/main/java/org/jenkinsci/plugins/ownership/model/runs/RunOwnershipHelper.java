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

package org.jenkinsci.plugins.ownership.model.runs;

import com.synopsys.arc.jenkins.plugins.ownership.OwnershipDescription;
import com.synopsys.arc.jenkins.plugins.ownership.jobs.JobOwnerHelper;
import com.synopsys.arc.jenkins.plugins.ownership.util.AbstractOwnershipHelper;
import hudson.model.Run;

/**
 * Helper for {@link Run} ownership management.
 * @author Oleg Nenashev <o.v.nenashev@gmail.com>
 * @since 0.6
 */
public class RunOwnershipHelper extends AbstractOwnershipHelper<Run> {

    private static final RunOwnershipHelper instance = new RunOwnershipHelper();

    public static RunOwnershipHelper getInstance() {
        return instance;
    }

    @Override
    public String getItemTypeName(Run item) {
        return "run";
    }
    
    @Override
    public String getItemDisplayName(Run item) {
        return item.getFullDisplayName();
    }

    @Override
    public String getItemURL(Run item) {
        return item.getUrl();
    }

    @Override
    public OwnershipDescription getOwnershipDescription(Run item) {
        return JobOwnerHelper.Instance.getOwnershipDescription(item.getParent());
    }    
}
