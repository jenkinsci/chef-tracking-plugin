package org.jenkinsci.plugins.chef_tracking.track;

import hudson.model.Fingerprint;
import org.jenkinsci.plugins.deployment.DeploymentFacet;
import org.jenkinsci.plugins.deployment.HostRecord;

/**
 * @author Kohsuke Kawaguchi
 */
public class ChefDeploymentFacet extends DeploymentFacet<HostRecord> {
    public ChefDeploymentFacet(Fingerprint fingerprint, long timestamp) {
        super(fingerprint, timestamp);
    }
}
