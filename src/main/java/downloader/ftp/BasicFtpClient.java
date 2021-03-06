package downloader.ftp;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import utils.UriUtil;

import java.io.IOException;
import java.net.URI;

/**
 * Created by taihuynh on 24/7/16.
 */
public class BasicFtpClient {
    private FtpDownloadConfig config;
    private FTPClient ftp;

    /**
     * Client for executing ftp request with configurable properties from 
     * {@link FtpDownloadConfig}. The execution will be delegated 
     * to the underlying {@link FTPClient} execution.
     * @param config
     */
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

    /**
     * Connect to the host specified in the input URI. If certain 
     * information is missing (due to optional, or secrecy), such 
     * information is derived from conf/ftpDownloadConfig.json
     * 
     * @param uri
     * @throws IOException
     */
    public void connect(URI uri) throws IOException {
        if (uri.getPort() != - 1)
            ftp.connect(uri.getHost(), uri.getPort());
        else
            ftp.connect(uri.getHost(), config.getPort());

        if (uri.getUserInfo() != null) {
            String[] parsedInfo = UriUtil.parseUserInfo(uri.getUserInfo());
            ftp.login(parsedInfo[0], parsedInfo[1]);
        }
        else
            ftp.login(config.getUser(), config.getPassword());
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
    }

}
