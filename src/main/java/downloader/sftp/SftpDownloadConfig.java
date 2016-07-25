package downloader.sftp;

import com.google.inject.Singleton;

/**
 * Created by taihuynh on 18/7/16.
 */
@Singleton
public class SftpDownloadConfig {
    private String user;
    private String password;
    private int port;
    private String knownHosts;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getKnownHosts() {
        return knownHosts;
    }

    public void setKnownHosts(String knownHosts) {
        this.knownHosts = knownHosts;
    }
}
