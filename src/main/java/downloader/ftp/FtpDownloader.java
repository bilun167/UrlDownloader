package downloader.ftp;

import com.google.inject.Inject;
import com.jcraft.jsch.JSch;

import downloader.AbstractDownloader;
import downloader.Downloader;
import generator.FileNameGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.commons.net.ftp.FTPClient;

/**
 * This class implement a {@link Downloader} for Ftp protocol.
 * 
 * Created by taihuynh on 9/7/16.
 */
public class FtpDownloader extends AbstractDownloader {
    private FileNameGenerator fng;
    private BasicFtpClient client;

    /**
     * A FtpDownloader is an implementation of {@link Downloader} for
     * ftp protocol. All ftp executions will be delegated 
     * to an underlying {@link FTPClient} executions.
     * 
     * <p> FtpDownloader also uses a {@link FileNameGenerator} to generate file name
     * based on pattern of the url path.
     * @param fng
     * @param client
     */
    @Inject
    public FtpDownloader(FileNameGenerator fng, BasicFtpClient client) {
        this.fng = fng;
        this.client = client;
    }

    @Override
    protected File _getFile(URI uri) throws IOException {
        String fileName = fng.generate(uri);

        client.connect(uri);
        // After connection attempt, you should check the reply code to verify success.
        logger.debug("Connected to {}. Code {}.", uri.getHost(), client.getFtp().getReplyString());

        try (InputStream is = client.getFtp().retrieveFileStream(uri.getPath());
             FileOutputStream fos = new FileOutputStream(fileName)) {
            ReadableByteChannel rbc = Channels.newChannel(is);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

            boolean success = client.getFtp().completePendingCommand();

            if (success) {
                client.getFtp().disconnect();
                return new File(fileName);
            }
            else {
                client.getFtp().logout();
                return null;
            }
        }
    }
}
