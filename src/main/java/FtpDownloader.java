import com.google.inject.Inject;
import generator.FileNameGenerator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * A program demonstrates how to upload files from local computer to a remote
 * FTP server using Apache Commons Net API.
 * @author www.codejava.net
 */
public class FtpDownloader implements Downloader {
    private FileNameGenerator fng;

    @Inject
    public FtpDownloader(FileNameGenerator fng) {
        this.fng = fng;
    }

    @Override
    public void download(URI uri) throws IOException {
        URLConnection conn = uri.toURL().openConnection();
        /*
        String userpass = username + ":" + password;
        String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
        conn.setRequestProperty ("Authorization", basicAuth);
        */
        InputStream is = conn.getInputStream();
        String mimeType = URLConnection.guessContentTypeFromStream(is);

        String fileName = fng.generate(uri);
        if (!fileName.contains(".") && mimeType != null)
            fileName.concat(mimeType);

        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            ReadableByteChannel rbc = Channels.newChannel(is);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }

        is.close();
    }
}
