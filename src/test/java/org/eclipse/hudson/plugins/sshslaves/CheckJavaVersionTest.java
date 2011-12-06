/**
 * ****************************************************************************
 * <p/>
 * Copyright (c) 2011 Oracle Corporation.
 * <p/>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors:
 * <p/>
 * Nikita Levyankov
 * <p/>
 * *****************************************************************************
 */
package org.eclipse.hudson.plugins.sshslaves;

import hudson.util.Secret;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Secret.class})
public class CheckJavaVersionTest {

    @Before
    public void setUp() throws Exception {
        mockStatic(Secret.class);
        Secret secret = Whitebox.invokeConstructor(Secret.class, new Class<?>[]{String.class}, new String[]{"value"});
        expect(Secret.fromString(EasyMock.<String>anyObject())).andReturn(secret).anyTimes();
        replayAll();
    }

    @Test
    public void testCheckJavaVersionOpenJDK7NetBSD() throws Exception {
        Assert.assertTrue("OpenJDK7 on NetBSD should be supported", checkSupported("openjdk-7-netbsd.version"));
        verifyAll();
    }

    @Test
    public void testCheckJavaVersionOpenJDK6Linux() throws Exception {
        Assert.assertTrue("OpenJDK6 on Linux should be supported", checkSupported("openjdk-6-linux.version"));
        verifyAll();
    }

    @Test
    public void testCheckJavaVersionSun6Linux() throws Exception {
        Assert.assertTrue("Sun 6 on Linux should be supported", checkSupported("sun-java-1.6-linux.version"));
        verifyAll();
    }

    @Test
    public void testCheckJavaVersionSun6Mac() throws Exception {
        Assert.assertTrue("Sun 6 on Mac should be supported", checkSupported("sun-java-1.6-mac.version"));
        verifyAll();
    }

    @Test
    public void testCheckJavaVersionSun4Linux() {
        try {
            checkSupported("sun-java-1.4-linux.version");
            Assert.fail();
        } catch (IOException e) {
            //
        }
        verifyAll();
    }

    /**
     * Returns true if the version is supported.
     *
     * @param testVersionOutput the resource to find relative to this class that contains the
     * output of "java -version"
     * @return true if success.
     * @throws IOException if any.
     */
    private static boolean checkSupported(final String testVersionOutput) throws IOException {
        final String javaCommand = "testing-java";
        final InputStream versionStream = SSHLauncherTest.class.getResourceAsStream(testVersionOutput);
        final BufferedReader r = new BufferedReader(new InputStreamReader(versionStream));
        final StringWriter output = new StringWriter();
        final String result = new SSHLauncher(null, 0, null, null, null, null, null)
            .checkJavaVersion(System.out, javaCommand, r, output);
        return null != result;
    }

}
