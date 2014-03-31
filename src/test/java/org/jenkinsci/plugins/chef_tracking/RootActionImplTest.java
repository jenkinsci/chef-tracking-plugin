package org.jenkinsci.plugins.chef_tracking;

import hudson.model.Fingerprint;
import hudson.model.FreeStyleProject;
import hudson.tasks.Fingerprinter;
import hudson.tasks.Shell;
import hudson.util.IOUtils;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.deployment.HostRecord;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * @author Kohsuke Kawaguchi
 */
public class RootActionImplTest extends Assert {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void basic() throws Exception {
        FreeStyleProject p = j.createFreeStyleProject();
        p.getBuildersList().add(new Shell("echo hello > foo.txt"));
        p.getPublishersList().add(new Fingerprinter("foo.txt",false));
        j.assertBuildStatusSuccess(p.scheduleBuild2(0));

        RootActionImpl a = RootActionImpl.get();
        a.report(JSONObject.fromObject(IOUtils.toString(getClass().getResourceAsStream("data.json"))));

        Fingerprint f = j.jenkins._getFingerprint("b1946ac92492d2347c6235b4d2611184");
        assertEquals(1, f.getFacets().size());

        ChefDeploymentFacet df = (ChefDeploymentFacet) f.getFacets().iterator().next();
        assertEquals(1, df.records.size());

        HostRecord hr = df.records.get(0);
        assertEquals("_default",hr.getEnv());
        assertEquals("elf",hr.getHost());
        assertEquals("/tmp/foo.txt",hr.getPath());
    }
}
