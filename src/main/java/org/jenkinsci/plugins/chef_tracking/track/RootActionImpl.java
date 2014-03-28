package org.jenkinsci.plugins.chef_tracking.track;

import com.sun.deploy.net.HttpResponse;

import java.io.IOException;

/**
 * Exposed at /puppet to receive report submissions from puppet over HTTP.
 *
 * @author Kohsuke Kawaguchi
 */
@Extension
public class RootActionImpl implements RootAction {
    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return null;
    }

    public String getUrlName() {
        return "chef";
    }

    /**
     * Receives the submission from HTTP reporter to track fingerprints.
     */
    @RequirePOST
    public HttpResponse doReport(StaplerRequest req) throws IOException {
        // TODO: stapler YAML support

        PuppetReport.load(req.getReader()).process();

        return HttpResponses.ok();
    }

    public static RootActionImpl get() {
        return Jenkins.getInstance().getExtensionList(RootAction.class).get(RootActionImpl.class);
    }
}
