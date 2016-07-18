package downloader.http;

import com.google.inject.Inject;
import downloader.AbstractDownloader;
import generator.FileNameGenerator;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.conn.NHttpClientConnectionManager;
import org.apache.http.nio.protocol.BasicAsyncRequestProducer;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by taihuynh on 8/7/16.
 */
public class HttpDownloader extends AbstractDownloader implements FutureCallback<FileResponse> {
    private FileNameGenerator fng;
    private NHttpClientConnectionManager connectionManager;
    @Inject
    public HttpDownloader(FileNameGenerator fng, NHttpClientConnectionManager connectionManager) {
        this.fng = fng;
        this.connectionManager = connectionManager;
    }

    @Override
    protected File _getFile(URI uri) throws IOException {
        URLConnection conn = uri.toURL().openConnection();
        /*
        String userpass = username + ":" + password;
        String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
        conn.setRequestProperty ("Authorization", basicAuth);
        */
        InputStream is = conn.getInputStream();
        String mimeType = URLConnection.guessContentTypeFromStream(is);
        String presetFileName = conn.getHeaderField("Content-Disposition");

        String fileName;
        if (presetFileName != null && presetFileName.indexOf("=") != -1) {
            fileName = presetFileName.split("=")[1]; //getting value after '='
        } else {
            fileName = fng.generate(uri);
        }
        if (!fileName.contains(".") && mimeType != null)
            fileName.concat(mimeType);
        is.close();

        try {
            return __tryDownloadAsync__(uri, fileName).get().content();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Future<FileResponse> __tryDownloadAsync__(URI uri, String fileName) throws IOException {
        try (CloseableHttpAsyncClient httpClient = HttpAsyncClients.custom().setConnectionManager(connectionManager)
                .setRedirectStrategy(new LaxRedirectStrategy()).build()) {
            httpClient.start();
            final HttpGet request = new HttpGet(uri);
            final HttpHost host = URIUtils.extractHost(uri);
            HttpAsyncRequestProducer producer = new BasicAsyncRequestProducer(host, request);

            HttpClientContext context = HttpClientContext.create();
            context.setAttribute("filePath", fileName);

            return httpClient.execute(producer, new HttpAsyncResponseConsumer(), context, this);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void completed(FileResponse result) {
        System.out.println("OK");
        // do nothing for now
    }

    @Override
    public void failed(Exception ex) {
        ex.printStackTrace();
        // do no exception handling for async execution. This will
        // be classified as Download exception at upper layer.
    }

    @Override
    public void cancelled() {

    }
}
