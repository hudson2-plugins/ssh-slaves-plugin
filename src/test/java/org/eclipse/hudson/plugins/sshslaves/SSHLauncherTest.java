/*******************************************************************************
 *
 * Copyright (c) 2011 Oracle Corporation.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *
 * Kohsuke Kawaguchi, Jon Schewe, Nikita Levyankov
 *
 *******************************************************************************/
package org.eclipse.hudson.plugins.sshslaves;

import hudson.model.Node.Mode;
import hudson.model.Slave;
import hudson.slaves.DumbSlave;
import hudson.slaves.RetentionStrategy;
import java.util.Collections;
import org.jvnet.hudson.test.HudsonTestCase;

public class SSHLauncherTest extends HudsonTestCase {

    public void testConfigurationRoundtrip() throws Exception {
        SSHLauncher launcher = new SSHLauncher("localhost", 123, "test", "pass", "xyz", "def", null);
        DumbSlave slave = new DumbSlave("slave", "dummy",
            createTmpDir().getPath(), "1", Mode.NORMAL, "",
            launcher, RetentionStrategy.NOOP, Collections.EMPTY_LIST);
        hudson.addNode(slave);

        submit(createWebClient().getPage(slave, "configure").getFormByName("config"));
        Slave n = (Slave) hudson.getNode("slave");

        assertNotSame(n, slave);
        assertNotSame(n.getLauncher(), launcher);
        assertEqualDataBoundBeans(n.getLauncher(), launcher);
    }
}
