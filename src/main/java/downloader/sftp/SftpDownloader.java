package downloader.sftp;

import com.google.inject.Inject;
import com.jcraft.jsch.*;
import downloader.AbstractDownloader;
import downloader.Downloader;
import downloader.http.BasicHttpClient;
import generator.FileNameGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * This class implement a {@link Downloader} for Sftp protocol.
 * 
 * Created by taihuynh on 9/7/16.
 */
public class SftpDownloader extends AbstractDownloader {
    private FileNameGenerator fng;
    private BasicSftpClient client;

    /**
     * A SftpDownloader is an implementation of {@link Downloader} for
     * sftp protocol. All sftp executions will be delegated 
     * to an underlying {@link JSch} executions.
     * 
     * <p> SftpDownloader also uses a {@link FileNameGenerator} to generate file name
     * based on pattern of the url path.
     * @param fng
     * @param client
     */
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

            try (InputStream is = sftpChannel.get(uri.getPath());
                 FileOutputStream fos = new FileOutputStream(fileName)) {
                ReadableByteChannel rbc = Channels.newChannel(is);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            }
            return new File(fileName);
        } catch (JSchException|SftpException e) {
        	logger.error("Exception occurs {} ", e);
            return null;
        } finally {
            if (sftpChannel != null)
                sftpChannel.exit();
            if (session != null)
                session.disconnect();
        }

    }
}
