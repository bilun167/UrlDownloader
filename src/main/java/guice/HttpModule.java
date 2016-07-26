package guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;

import downloader.ftp.FtpDownloader;
import downloader.http.*;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import utils.JacksonConfig;
import utils.JacksonConfigFormat;

/**
 * {@link Guice} module for {@link HttpDownloader}
 * 
 * Created by taihuynh on 18/7/16.
 */
public class HttpModule extends AbstractModule {
    public static final String DEFAULT_CONFIG_PATH = "conf/httpDownloadConfig.json";
    public static final String DEFAULT_SYSTEM_VAR = "httpDownloadConfig";

    private HttpDownloadConfig httpDownloadConfig = null;

    private BasicRequestInterceptor requestInterceptor = new BasicRequestInterceptor();
    private BasicResponseInterceptor responseInterceptor = new BasicResponseInterceptor();

    private PoolingClientConnectionManager cm = new PoolingClientConnectionManager();

    /**
     * If the configuration file exists, it will be loaded once and returned, else the default
     * configuration will be returned.
     * @return
     */
    protected HttpDownloadConfig readDefaultHttpDownloadConfig() {
        if (httpDownloadConfig == null) {
            // If the configuration file does not exist, default values is used
            httpDownloadConfig = JacksonConfig.readConfig(DEFAULT_CONFIG_PATH, DEFAULT_SYSTEM_VAR,
                    HttpDownloadConfig.class, JacksonConfigFormat.JSON);

            if (httpDownloadConfig == null) {
                httpDownloadConfig = new HttpDownloadConfig();
            }
        }
        return httpDownloadConfig;
    }

    @Override
    protected void configure() {
        bind(BasicRequestInterceptor.class).toInstance(requestInterceptor);
        bind(BasicResponseInterceptor.class).toInstance(responseInterceptor);

        if (httpDownloadConfig == null)
            httpDownloadConfig = readDefaultHttpDownloadConfig();
        bind(HttpDownloadConfig.class).toInstance(httpDownloadConfig);

        bind(PoolingClientConnectionManager.class).toInstance(cm);
        ConnectionManagerFactory cmf = new ConnectionManagerFactory(httpDownloadConfig, cm);
        bind(ConnectionManagerFactory.class).toInstance(cmf);

        BasicHttpClient client = new BasicHttpClient(httpDownloadConfig, requestInterceptor, responseInterceptor, cmf);
        bind(BasicHttpClient.class).toInstance(client);
    }
}
