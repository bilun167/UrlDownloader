package downloader.sftp;

import com.google.inject.Inject;
import com.jcraft.jsch.*;
import downloader.AbstractDownloader;
import generator.FileNameGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Created by taihuynh on 9/7/16.
 */
public class SftpDownloader extends AbstractDownloader {
    private FileNameGenerator fng;

    @Inject
    public SftpDownloader(FileNameGenerator fng) {
        this.fng = fng;
    }

    @Override
    protected File _getFile(URI uri) throws IOException {
        JSch jsch = new JSch();
        String knownHostsFilename = "/Users/" + uri.getUserInfo() + "/.ssh/known_hosts";
        appendKnownHosts(jsch, knownHostsFilename);

        Session session = null;
        ChannelSftp sftpChannel = null;
        String fileName = fng.generate(uri);
        try {
            session = jsch.getSession(uri.getUserInfo(), uri.getHost());
            //session.setPassword(ui.getPassword());

            session.connect();

            Channel channel = session.openChannel(uri.getScheme());
            channel.connect();

            sftpChannel = (ChannelSftp) channel;

            try {
                sftpChannel.get(uri.getPath(), fileName);
            } catch (SftpException e) {
                try (InputStream is = sftpChannel.get(uri.getPath());
                        FileOutputStream fos = new FileOutputStream(fileName)) {
                    ReadableByteChannel rbc = Channels.newChannel(is);
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                }
            }

        } catch (JSchException|SftpException e) {
            return null;
        } finally {
            sftpChannel.exit();
            session.disconnect();
        }

        return new File(fileName);
    }

    private void appendKnownHosts(JSch jsch, String knownHostsFilename) {
        try {
            jsch.setKnownHosts(knownHostsFilename);
        } catch (JSchException e) {
            // do nothing
            e.printStackTrace();
        }
    }
}
