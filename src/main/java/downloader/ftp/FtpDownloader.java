package downloader.ftp;

import com.google.inject.Inject;
import downloader.AbstractDownloader;
import generator.FileNameGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class FtpDownloader extends AbstractDownloader {
    private FileNameGenerator fng;
    private BasicFtpClient client;

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
        System.out.println("Connected to " + uri.getHost() + ".");
        System.out.print(client.getFtp().getReplyString());

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
