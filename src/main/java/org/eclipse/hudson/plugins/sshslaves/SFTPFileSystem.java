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
 * Kohsuke Kawaguchi, Anton Kozak, Nikita Levyankov
 *
 *******************************************************************************/
package org.eclipse.hudson.plugins.sshslaves;

import ch.ethz.ssh2.SFTPv3DirectoryEntry;
import hudson.tools.JDKInstaller.FileSystem;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link FileSystem} implementation over SFTP.
 *
 * @author Kohsuke Kawaguchi
 */
class SFTPFileSystem implements FileSystem {

    private static final int BUFFER_SIZE = 32768;

    private final SFTPClient sftp;

    public SFTPFileSystem(SFTPClient sftp) {
        this.sftp = sftp;
    }

    public void delete(String file) throws IOException, InterruptedException {
        sftp.rm(file);
    }

    public void chmod(String file, int mode) throws IOException, InterruptedException {
        sftp.chmod(file, mode);
    }

    public InputStream read(String file) throws IOException {
        return new BufferedInputStream(sftp.read(file), BUFFER_SIZE);
    }

    public List<String> listSubDirectories(String dir) throws IOException, InterruptedException {
        List<String> r = new ArrayList<String>();
        for (SFTPv3DirectoryEntry e : (List<SFTPv3DirectoryEntry>) sftp.ls(dir)) {
            r.add(e.filename);
        }
        return r;
    }

    public void pullUp(String from, String to) throws IOException, InterruptedException {
        for (SFTPv3DirectoryEntry e : (List<SFTPv3DirectoryEntry>) sftp.ls(from)) {
            if (e.filename.equals(".") || e.filename.equals("..")) {
                continue;
            }
            sftp.mv(from + '/' + e.filename, to + '/' + e.filename);
        }
        sftp.rmdir(from);
    }
}
