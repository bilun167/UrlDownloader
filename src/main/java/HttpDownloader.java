import com.google.inject.Inject;
import generator.FileNameGenerator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Created by taihuynh on 8/7/16.
 */
public class HttpDownloader implements Downloader {
    private FileNameGenerator fng;

    @Inject
    public HttpDownloader(FileNameGenerator fng) {
        this.fng = fng;
    }

    @Override
    public void download(URL url) throws IOException {
        URLConnection conn = url.openConnection();
        InputStream is = conn.getInputStream();
        String mimeType = URLConnection.guessContentTypeFromStream(is);
        String presetFileName = conn.getHeaderField("Content-Disposition");

        String fileName;
        // raw = "attachment; filename=abc.jpg"
        if (presetFileName != null && presetFileName.indexOf("=") != -1) {
            fileName = presetFileName.split("=")[1]; //getting value after '='
        } else {
            fileName = fng.generate(url);
        }
        if (!fileName.contains(".") && mimeType != null)
            fileName.concat(mimeType);
        
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            ReadableByteChannel rbc = Channels.newChannel(is);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
    }
}
