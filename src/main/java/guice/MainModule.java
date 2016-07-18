package guice;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import generator.FileNameGenerator;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.conn.NHttpClientConnectionManager;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ThreadFactory;

/**
 * Created by taihuynh on 8/7/16.
 */
public class MainModule extends AbstractModule {
    private FileNameGenerator fng = new FileNameGenerator();
    //private HttpConfig httpConfig;

    protected static final Logger log = LoggerFactory.getLogger(MainModule.class);

    @Provides
    public PoolingNHttpClientConnectionManager provideConnectionManager() {
        try {
            /**
             * SSL context for secure connections can be created either based on
             * system or application specific properties.
             */
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (certs, authType) -> true).build();

            /**
             * Use custom hostname verifier to customize SSL hostname
             * verification.
             */
            HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

            /**
             * Create a registry of custom connection session strategies for
             * supported protocol schemes.
             */
            Registry<SchemeIOSessionStrategy> sessionStrategyRegistry = RegistryBuilder
                    .<SchemeIOSessionStrategy> create().register("http", NoopIOSessionStrategy.INSTANCE)
                    .register("https", new SSLIOSessionStrategy(sslContext, hostnameVerifier)).build();
            ThreadFactory tf = new ThreadFactoryBuilder().setNameFormat("ConnectionManager-%d").setDaemon(true).build();
            IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                    .setIoThreadCount(Runtime.getRuntime().availableProcessors()).setTcpNoDelay(true).build();

            PoolingNHttpClientConnectionManager pool = new PoolingNHttpClientConnectionManager(
                    new DefaultConnectingIOReactor(ioReactorConfig, tf), sessionStrategyRegistry);
            //pool.setDefaultMaxPerRoute(httpConfig.getMaxPerRoute());
            //pool.setMaxTotal(httpConfig.getMaxTotalConnection());

            ConnectionManagerData threadData = new ConnectionManagerData(pool);
            MaintenanceThread maintainThread = new MaintenanceThread(threadData);
            maintainThread.start();
            Runtime.getRuntime().addShutdownHook(new CleanupThread(threadData, maintainThread));
            return pool;
        } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException | IOReactorException e) {
            return null;
        }
    }
    protected class ConnectionManagerData {
        private boolean isMaintaining = true;
        private NHttpClientConnectionManager pool;

        public ConnectionManagerData(NHttpClientConnectionManager pool) {
            this.pool = pool;
        }

        public boolean isMaintaining() {
            return isMaintaining;
        }

        public void setMaintaining(boolean isMaintaining) {
            this.isMaintaining = isMaintaining;
        }

        public NHttpClientConnectionManager getPool() {
            return pool;
        }

        public void setPool(NHttpClientConnectionManager pool) {
            this.pool = pool;
        }
    }

    protected class MaintenanceThread extends Thread {
        private ConnectionManagerData data;

        public MaintenanceThread(ConnectionManagerData data) {
            this.data = data;
        }

        public ConnectionManagerData getData() {
            return data;
        }

        public void setData(ConnectionManagerData data) {
            this.data = data;
        }

        @Override
        public void run() {
            while (data.isMaintaining()) {
                try {
                    data.getPool().closeExpiredConnections();
                    //data.getPool().closeIdleConnections(httpConfig.getMaxIdleAllowed(), TimeUnit.MICROSECONDS);
                } catch (Exception e) {
                    log.error("Maintain thread interrupted", e);
                }
            }
        }
    }

    protected class CleanupThread extends Thread {
        private ConnectionManagerData data;
        private MaintenanceThread maintenanceThread;

        public CleanupThread(ConnectionManagerData data, MaintenanceThread maintenanceThread) {
            this.data = data;
            this.maintenanceThread = maintenanceThread;
        }

        @Override
        public void run() {
            synchronized (data) {
                data.setMaintaining(false);
                if (maintenanceThread.getState() == State.WAITING) {
                    maintenanceThread.interrupt();
                }

                try {
                    data.getPool().shutdown();
                } catch (IOException e) {
                    log.error("Cleanup thread cannot shut down ConnectionManager gracefully", e);
                }
            }
        }
    }

    @Override
    protected void configure() {
        bind(FileNameGenerator.class).toInstance(fng);
        bind(NHttpClientConnectionManager.class).to(PoolingNHttpClientConnectionManager.class);
    }
}
