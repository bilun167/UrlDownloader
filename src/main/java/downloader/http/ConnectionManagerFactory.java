package downloader.http;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


/**
 * Created by taihuynh on 18/7/16.
 */
@Singleton
public class ConnectionManagerFactory {
    private Logger logger = LoggerFactory.getLogger(ConnectionManagerFactory.class);

    private HttpDownloadConfig config;
    private Thread maintainThread;
    private boolean maintaining;

    // We only provide a shared pooling connection manager now
    private PoolingClientConnectionManager cm;

    /**
     * A factory to instantiate connection manager and register the 2 always-running
     * maintenance thread and clean up thread to enhance httpClient execution.
     * @param config
     * @param cm
     */
    @Inject
    public ConnectionManagerFactory(HttpDownloadConfig config, PoolingClientConnectionManager cm) {
        this.config = config;
        this.cm = cm;

        // Set max total connection
        cm.setMaxTotal(config.getMaxTotalConnection());
        // Set max connection per route
        cm.setDefaultMaxPerRoute(config.getMaxPerRoute());

        // Start maintain thread
        maintaining = true;
        maintainThread = new MaintainThread();
        maintainThread.start();
        // Add clean up thread
        Runtime.getRuntime().addShutdownHook(new CleanupThread());
    }

    public ClientConnectionManager getConnectionManager() {
        return cm;
    }

    private class MaintainThread extends Thread {
        @Override
        public void run() {
            while(maintaining) {
                try {
                    // Close expired connections
                    cm.closeExpiredConnections();
                    // Close idle connections
                    cm.closeIdleConnections(config.getMaxIdleAllowed(), TimeUnit.MICROSECONDS);

                    Thread.sleep(config.getMaintainInterval());
                } catch(Exception ex) {
                    logger.error("Maintain thread interrupted", ex);
                }
            }
        }
    }

    private class CleanupThread extends Thread {
        @Override
        public void run() {
            maintaining = false;
            if(maintainThread.getState() == State.WAITING) {
                maintainThread.interrupt();
            }

            cm.shutdown();
        }
    }

}