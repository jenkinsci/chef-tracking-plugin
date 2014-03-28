package org.jenkinsci.plugins.chef_tracking.track;

import hudson.model.Fingerprint;
import org.jenkinsci.plugins.deployment.DeploymentFacet;

/**
 * @author Kohsuke Kawaguchi
 */
public class ChefDeploymentFacet extends DeploymentFacet {
    public ChefDeploymentFacet(Fingerprint fingerprint, long timestamp) {
        super(fingerprint, timestamp);
    }
}
