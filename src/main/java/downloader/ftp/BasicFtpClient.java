package downloader.ftp;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
import java.net.URI;

/**
 * Created by taihuynh on 24/7/16.
 */
public class BasicFtpClient {
    private FtpDownloadConfig config;
    private FTPClient ftp;

    public BasicFtpClient(FtpDownloadConfig config) {
        this.config = config;
        ftp = new FTPClient();
        ftp.enterLocalPassiveMode();
    }

    public FtpDownloadConfig getConfig() {
        return config;
    }

    public void setConfig(FtpDownloadConfig config) {
        this.config = config;
    }

    public FTPClient getFtp() {
        return ftp;
    }

    public void setFtp(FTPClient ftp) {
        this.ftp = ftp;
    }

    public void connect(URI uri) throws IOException {
        if (uri.getPort() != - 1)
            ftp.connect(uri.getHost(), uri.getPort());
        else
            ftp.connect(uri.getHost(), config.getPort());

        if (uri.getUserInfo() != null) {
            String[] parsedInfo = parseUserInfo(uri.getUserInfo());
            ftp.login(parsedInfo[0], parsedInfo[1]);
        }
        else
            ftp.login(config.getUser(), config.getPassword());
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
    }

    protected String[] parseUserInfo(String userInfo) {
        String[] parsedInfo = new String[2];
        int splitIndex = 0;
        if ((splitIndex = userInfo.indexOf(":")) == - 1)
            return parsedInfo;

        parsedInfo[0] = userInfo.substring(0, splitIndex);
        parsedInfo[1] = userInfo.substring(splitIndex);
        return parsedInfo;
    }
}
