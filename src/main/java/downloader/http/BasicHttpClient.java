package downloader.http;

import com.google.inject.Inject;

import downloader.ftp.FtpDownloadConfig;

import org.apache.commons.net.ftp.FTPClient;
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
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * Created by taihuynh on 23/7/16.
 */
public class BasicHttpClient {
    private static Logger logger = LoggerFactory.getLogger(BasicHttpClient.class);

    private DefaultHttpClient httpClient;
    private HttpDownloadConfig config;

    /**
     * Client for executing http request with configurable properties from 
     * {@link HttpDownloadConfig}. The config also contains setup for the 
     * underlying {@link BasicHttpClient} environment like cleanup threads, 
     * maintenance thread. This client can be decorated by the request interceptors 
     * and response interceptors as given.
     *  
     * @param config
     * @param requestInterceptor
     * @param responseInterceptor
     * @param cmf
     */
    @Inject
    public BasicHttpClient( HttpDownloadConfig config,
                            BasicRequestInterceptor requestInterceptor,
                            BasicResponseInterceptor responseInterceptor,
                            ConnectionManagerFactory cmf) {
        this.config = config;
        ClientConnectionManager conman = cmf.getConnectionManager();
        httpClient = relax(new DefaultHttpClient(conman));

        // Set http protocol interceptors, preprocess the outgoing request and incoming response
        httpClient.addRequestInterceptor(requestInterceptor);
        httpClient.addResponseInterceptor(responseInterceptor);

        httpClient.getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, config.getAutoRedirect());
        httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
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

    public DefaultHttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(DefaultHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public HttpDownloadConfig getConfig() {
        return config;
    }

    public void setConfig(HttpDownloadConfig config) {
        this.config = config;
    }
}
