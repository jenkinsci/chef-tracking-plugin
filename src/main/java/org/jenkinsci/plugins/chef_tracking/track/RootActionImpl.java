package org.jenkinsci.plugins.chef_tracking.track;

import hudson.Extension;
import hudson.Util;
import hudson.model.Fingerprint;
import hudson.model.RootAction;
import hudson.util.HttpResponses;
import hudson.util.IOUtils;
import jenkins.model.FingerprintFacet;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.deployment.DeploymentFacet;
import org.jenkinsci.plugins.deployment.HostRecord;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.interceptor.RequirePOST;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Exposed at /puppet to receive report submissions from Chef over HTTP.
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
        JSONObject o = JSONObject.fromObject(IOUtils.toString(req.getReader()));

        report(o);

        return HttpResponses.ok();
    }

    public void report(JSONObject o) throws IOException {
        Jenkins.getInstance().checkPermission(DeploymentFacet.RECORD);

        String node = o.getString("node");
        String env = o.getString("environment");
        Date t = new Date(o.getString("end_time"));

        for (JSONObject upd : (List<JSONObject>)(List)o.getJSONArray("updates")) {
            String path = upd.optString("path");
            String md5 = upd.optString("md5");

            if (md5==null || path==null)    continue;   // defensive check

            ChefDeploymentFacet f = getDeploymentFacet(md5);
            if (f!=null) {
                f.add(new HostRecord(t.getTime(), node, env, path, null));
            }
        }
    }

    public static RootActionImpl get() {
        return Jenkins.getInstance().getExtensionList(RootAction.class).get(RootActionImpl.class);
    }

    /**
     * Resolve {@link DeploymentFacet} to attach the record to, or null if there's none.
     *
     * This is a convenience method for subtypes.
     */
    protected ChefDeploymentFacet getDeploymentFacet(String md5) throws IOException {
        if (md5 == null) return null;

        Fingerprint f = Jenkins.getInstance().getFingerprintMap().get(md5);
        if (f == null) return null;

        Collection<FingerprintFacet> facets = f.getFacets();
        ChefDeploymentFacet df = findDeploymentFacet(facets);
        if (df == null) {
            df = new ChefDeploymentFacet(f, System.currentTimeMillis());
            facets.add(df);
        }
        return df;
    }

    private ChefDeploymentFacet findDeploymentFacet(Collection<FingerprintFacet> facets) {
        for (ChefDeploymentFacet df : Util.filter(facets, ChefDeploymentFacet.class)) {
            return df;
        }
        return null;
    }
}
