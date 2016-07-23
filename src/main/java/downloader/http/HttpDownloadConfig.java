package downloader.http;

import com.google.inject.Singleton;

/**
 * Created by taihuynh on 18/7/16.
 */
@Singleton
public class HttpDownloadConfig {
    // Parameters used to configure HttpClient
    private int connectionTimeout;
    private int socketTimeout;

    // Parameters used to configure http connection pool
    private int maxTotalConnection;
    private int maxPerRoute;
    private long maxIdleAllowed;

    // Scheduled interval to maintenance the connection pool
    private long maintainInterval;

    private boolean autoRedirect;

    public HttpDownloadConfig() {
        connectionTimeout = 30000;
        socketTimeout = 30000;
        maxTotalConnection = 1000;
        maxPerRoute = 100;
        maxIdleAllowed = 30000;
        maintainInterval = 10000;
        autoRedirect = false;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public int getMaxTotalConnection() {
        return maxTotalConnection;
    }

    public void setMaxTotalConnection(int maxTotalConnection) {
        this.maxTotalConnection = maxTotalConnection;
    }

    public int getMaxPerRoute() {
        return maxPerRoute;
    }

    public void setMaxPerRoute(int maxPerRoute) {
        this.maxPerRoute = maxPerRoute;
    }

    public long getMaxIdleAllowed() {
        return maxIdleAllowed;
    }

    public void setMaxIdleAllowed(long maxIdleAllowed) {
        this.maxIdleAllowed = maxIdleAllowed;
    }

    public long getMaintainInterval() {
        return maintainInterval;
    }

    public void setMaintainInterval(long maintainInterval) {
        this.maintainInterval = maintainInterval;
    }

    public boolean getAutoRedirect() {
        return autoRedirect;
    }

    public void setAutoRedirect(boolean autoRedirect) {
        autoRedirect = autoRedirect;
    }
}
