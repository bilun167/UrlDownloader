package downloader.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import com.google.inject.Inject;

import downloader.AbstractDownloader;
import downloader.Downloader;
import generator.FileNameGenerator;

/**
 * This class implement a {@link Downloader} for Http protocol.
 * 
 * Created by taihuynh on 8/7/16.
 */
public class HttpDownloader extends AbstractDownloader {

    private FileNameGenerator fng;
    private BasicHttpClient client;

    /**
     * A HttpDownloader is an implementation of {@link Downloader} for http protocol.
     * HttpDownloader uses a {@link BasicHttpClient}, which can be configured 
     * with configuration and interceptor. All http executions will be delegated 
     * to the underlying {@link BasicHttpClient} instance.
     * 
     * <p> HttpDownloader also uses a {@link FileNameGenerator} to generate file name
     * based on pattern of the url path.
     * @param fng
     * @param client
     */
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
