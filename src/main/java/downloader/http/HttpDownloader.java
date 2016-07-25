package downloader.http;

import com.google.inject.Inject;
import downloader.AbstractDownloader;
import generator.FileNameGenerator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Created by taihuynh on 8/7/16.
 */
public class HttpDownloader extends AbstractDownloader {
    private static Logger logger = LoggerFactory.getLogger(HttpDownloader.class);

    private FileNameGenerator fng;
    private BasicHttpClient client;

    @Inject
    public HttpDownloader(FileNameGenerator fng, BasicHttpClient client) {
        this.fng = fng;
        this.client = client;
    }

    @Override
    protected File _getFile(URI uri) throws IOException {
        HttpGet httpget = new HttpGet(uri);
        HttpResponse response = client.getHttpClient().execute(httpget);
        HttpEntity entity = response.getEntity();
        /*
        String userpass = username + ":" + password;
        String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
        conn.setRequestProperty ("Authorization", basicAuth);
        */

        String fileName = fng.generate(uri);
        try (InputStream is = entity.getContent();
             FileOutputStream fos = new FileOutputStream(fileName)) {
            ReadableByteChannel rbc = Channels.newChannel(is);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }

        httpget.releaseConnection();
        return new File(fileName);
    }


}
