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
 * Kohsuke Kawaguchi, Nikita Levyankov
 *
 *******************************************************************************/
package org.eclipse.hudson.plugins.sshslaves;

import ch.ethz.ssh2.Connection;
import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.model.Hudson;
import hudson.model.TaskListener;
import hudson.slaves.SlaveComputer;
import java.util.Collections;
import java.util.List;

/**
 * Guess where Java is.
 */
public abstract class JavaProvider implements ExtensionPoint {
    /**
     * @deprecated Override {@link #getJavas(SlaveComputer, TaskListener, Connection)} instead.
     */
    public List<String> getJavas(TaskListener listener, Connection connection) {
        return Collections.emptyList();
    }

    /**
     * Returns the list of possible places where java executable might exist.
     *
     * @return Can be empty but never null. Absolute path to the possible locations of Java.
     */
    public List<String> getJavas(SlaveComputer computer, TaskListener listener, Connection connection) {
        return getJavas(listener, connection);
    }

    /**
     * All regsitered instances.
     */
    public static ExtensionList<JavaProvider> all() {
        return Hudson.getInstance().getExtensionList(JavaProvider.class);
    }

}
