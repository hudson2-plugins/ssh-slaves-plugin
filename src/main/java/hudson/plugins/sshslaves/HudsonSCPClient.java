package hudson.plugins.sshslaves;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.util.StringEncoder;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * //TODO class description
 * <p/>
 * Date: 8/5/11
 *
 * @author Nikita Levyankov
 */
public class HudsonSCPClient extends SCPClient {

    private Connection connection;

    public HudsonSCPClient(Connection conn) {
        super(conn);
        this.connection = conn;
    }

    /**
     * Create a remote file and copy the contents of the passed byte array into it.
     * The method use the specified mode when creating the file on the remote side.
     *
     * @param data the data to be copied into the remote file.
     * @param remoteFileName The name of the file which will be created in the remote target directory.
     * @param remoteTargetDirectory Remote target directory. Use an empty string to specify the default directory.
     * @param mode a four digit string (e.g., 0644, see "man chmod", "man open")
     * @throws java.io.IOException if any
     */
    public void put(byte[] data, String remoteFileName, String remoteTargetDirectory, String mode) throws IOException {
        Session sess = null;

        if ((remoteFileName == null) || (remoteTargetDirectory == null) || (mode == null)) {
            throw new IllegalArgumentException("Null argument.");
        }

        if (mode.length() != 4) {
            throw new IllegalArgumentException("Invalid mode.");
        }

        for (int i = 0; i < mode.length(); i++) {
            if (!Character.isDigit(mode.charAt(i))) {
                throw new IllegalArgumentException("Invalid mode.");
            }
        }

        remoteTargetDirectory = remoteTargetDirectory.trim();
        remoteTargetDirectory = (remoteTargetDirectory.length() > 0) ? remoteTargetDirectory : ".";

        String cmd = "scp -t -d " + remoteTargetDirectory;

        try {
            sess = connection.openSession();
            sess.execCommand(cmd);
            sendBytes(sess, data, remoteFileName, mode);
        } catch (IOException e) {
            throw (IOException) new IOException("Error during SCP transfer.").initCause(e);
        } finally {
            if (sess != null) {
                sess.close();
            }
        }
    }

    private void sendBytes(Session sess, byte[] data, String fileName, String mode) throws IOException {
        OutputStream os = sess.getStdin();
        InputStream is = new BufferedInputStream(sess.getStdout(), 512);

        readResponse(is);

        String cline = "C" + mode + " " + data.length + " " + fileName + "\n";

        os.write(StringEncoder.GetBytes(cline));
        os.flush();

        readResponse(is);

        os.write(data, 0, data.length);
        os.write(0);
        os.flush();

        readResponse(is);

        os.write(StringEncoder.GetBytes("E\n"));
        os.flush();
    }

}
