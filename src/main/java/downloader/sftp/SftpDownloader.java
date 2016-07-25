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
    private BasicSftpClient client;

    @Inject
    public SftpDownloader(FileNameGenerator fng, BasicSftpClient client) {
        this.fng = fng;
        this.client = client;
    }

    @Override
    protected File _getFile(URI uri) throws IOException {
        Session session = null;
        ChannelSftp sftpChannel = null;
        String fileName = fng.generate(uri);
        try {
            session = client.connect(uri);

            Channel channel = session.openChannel(uri.getScheme());
            channel.connect();
            sftpChannel = (ChannelSftp) channel;

            for (Object s : sftpChannel.ls(uri.getPath()))
                System.out.println(s);

            try (InputStream is = sftpChannel.get(uri.getPath());
                 FileOutputStream fos = new FileOutputStream(fileName)) {
                ReadableByteChannel rbc = Channels.newChannel(is);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            }
            return new File(fileName);
        } catch (JSchException|SftpException e) {
            return null;
        } finally {
            if (sftpChannel != null)
                sftpChannel.exit();
            if (session != null)
                session.disconnect();
        }

    }
}
