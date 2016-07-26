package downloader.sftp;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import downloader.ftp.FtpDownloadConfig;
import utils.UriUtil;

import java.io.IOException;
import java.net.URI;

import org.apache.commons.net.ftp.FTPClient;

/**
 * Created by taihuynh on 24/7/16.
 */
public class BasicSftpClient {
    private SftpDownloadConfig config;
    private JSch sftp;

    /**
     * Client for executing sftp request with configurable properties from 
     * {@link SftpDownloadConfig}. The execution will be delegated 
     * to the underlying {@link JSch} execution.
     * @param config
     */
    public BasicSftpClient(SftpDownloadConfig config) {
        this.config = config;
        sftp = new JSch();
    }

    /**
     * Connect to the host specified in the input URI. If certain 
     * information is missing (due to optional, or secrecy), such 
     * information is derived from conf/sftpDownloadConfig.json
     * 
     * @param uri
     * @throws IOException
     */
    public Session connect(URI uri) throws JSchException {
        Session session = null;
        sftp.setKnownHosts(config.getKnownHosts());

        if (uri.getUserInfo() != null) {
            String[] parsedInfo = UriUtil.parseUserInfo(uri.getUserInfo());
            session = sftp.getSession(parsedInfo[0], uri.getHost());
            session.setPassword(parsedInfo[1]);
        }
        else {
            session = sftp.getSession(config.getUser(), uri.getHost());
            session.setPassword(config.getPassword());
        }

        session.connect();
        return session;
    }

    public JSch getSftp() {
        return sftp;
    }

    public void setSftp(JSch sftp) {
        this.sftp = sftp;
    }
}
