/*******************************************************************************
 * Copyright (c) 2009 Thales Corporate Services SAS                             *
 * Author : Gregory Boissinot                                                   *
 *                                                                              *
 * Permission is hereby granted, free of charge, to any person obtaining a copy *
 * of this software and associated documentation files (the "Software"), to deal*
 * in the Software without restriction, including without limitation the rights *
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell    *
 * copies of the Software, and to permit persons to whom the Software is        *
 * furnished to do so, subject to the following conditions:                     *
 *                                                                              *
 * The above copyright notice and this permission notice shall be included in   *
 * all copies or substantial portions of the Software.                          *
 *                                                                              *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR   *
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,     *
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE  *
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER       *
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,*
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN    *
 * THE SOFTWARE.                                                                *
 *******************************************************************************/

package com.thalesgroup.hudson.plugins.jobrevision;

import hudson.Extension;
import hudson.model.*;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;


@ExportedBean
public class JobRevision extends JobProperty<AbstractProject<?, ?>> {

    private boolean on;

    private String revision;

    public DescriptorImpl getDescriptor() {
        return DESCRIPTOR;
    }

    public JobRevision(String revision) {
        this.on = true;
        this.revision = revision;
    }

    @Extension
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    public static final class DescriptorImpl extends JobPropertyDescriptor {
        public DescriptorImpl() {
            super(JobRevision.class);
            load();
        }

        public boolean isApplicable(Class<? extends Job> jobType) {
            return AbstractProject.class.isAssignableFrom(jobType);
        }

        public String getDisplayName() {
            return Messages.plugin_DisplayName();
        }

        public JobRevision newInstance(org.kohsuke.stapler.StaplerRequest req, JSONObject formData) throws Descriptor.FormException {
            Object onObject = formData.get("on");
            if (onObject != null) {
                if (onObject instanceof JSONObject) {
                    String revision = ((JSONObject) onObject).getString("revision");
                    if ((revision != null) && (revision.trim().length() != 0))
                        return new JobRevision(revision);
                }
            }
            return null;
        }
    }

    @Override
    public boolean prebuild(hudson.model.AbstractBuild<?, ?> abstractBuild, hudson.model.BuildListener buildListener) {
        abstractBuild.addAction(new JobRevisionEnvironmentAction(revision));
        return true;
    }

    @SuppressWarnings("unused")
    @Exported
    public String getRevision() {
        return revision;
    }

    @SuppressWarnings("unused")
    public boolean isOn() {
        return on;
    }

    @SuppressWarnings("unused")
    private Object readResolve() {
        if (!on) {
            on = true;
        }
        return this;
    }
}
