package downloader.http;

import com.google.inject.Inject;
import downloader.AbstractDownloader;
import generator.FileNameGenerator;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * Created by taihuynh on 8/7/16.
 */
public class HttpDownloader extends AbstractDownloader {
    private static Logger logger = LoggerFactory.getLogger(HttpDownloader.class);

    private FileNameGenerator fng;
    private BasicResponseInterceptor responseInterceptor;
    private BasicRequestInterceptor requestInterceptor;
    private ConnectionManagerFactory cmf;
    private HttpDownloadConfig config;

    @Inject
    public HttpDownloader(FileNameGenerator fng,
                          HttpDownloadConfig config,
                          BasicRequestInterceptor requestInterceptor,
                          BasicResponseInterceptor responseInterceptor,
                          ConnectionManagerFactory cmf) {
        this.fng = fng;
        this.requestInterceptor = requestInterceptor;
        this.responseInterceptor = responseInterceptor;
        this.cmf = cmf;
        this.config = config;
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
        // raw = "attachment; filename=abc.jpg"
        if (presetFileName != null && presetFileName.indexOf("=") != -1) {
            fileName = presetFileName.split("=")[1]; //getting value after '='
        } else {
            fileName = fng.generate(uri);
        }
        if (!fileName.contains(".") && mimeType != null)
            fileName.concat(mimeType);

        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            ReadableByteChannel rbc = Channels.newChannel(is);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }

        is.close();
        return new File(fileName);
    }

    private void __try__(URI uri, boolean allowAutoRedirect) {
        // Set the context (init the mini browser)
        ClientConnectionManager conman = cmf.getConnectionManager();
        DefaultHttpClient httpClient = relax(new DefaultHttpClient(conman));

        httpClient.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, allowAutoRedirect);
        httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);

        // Set http protocol interceptors, preprocess the outgoing request and incoming response
        httpClient.addRequestInterceptor(requestInterceptor);
        httpClient.addResponseInterceptor(responseInterceptor);

        // Set the default parameters of http for all requests
        httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
                "Mozilla/5.0 (Windows NT 6.0; WOW64; rv:15.0) Gecko/20100101 Firefox/15.0.1");

        int connectionTimeout = config.getConnectionTimeout();
        int socketTimeout = config.getSocketTimeout();

        httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectionTimeout);
        httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, socketTimeout);
    }

    private DefaultHttpClient relax(DefaultHttpClient base) {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");

            X509TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] xcs, String string) {

                }

                public void checkServerTrusted(X509Certificate[] xcs, String string) {

                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            ctx.init(null, new TrustManager[]{tm}, new SecureRandom());

            // Create the ssl socket
            SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager cm = base.getConnectionManager();
            SchemeRegistry sr = cm.getSchemeRegistry();
            // Register the new scheme
            sr.register(new Scheme("https", 443, ssf));
            return new DefaultHttpClient(cm, base.getParams());

        } catch (Exception e) {
            logger.error("Exception!", e);
            return null;
        }
    }
}
