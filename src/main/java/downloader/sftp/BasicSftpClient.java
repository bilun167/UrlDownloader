package downloader.sftp;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import utils.UriUtil;

import java.net.URI;

/**
 * Created by taihuynh on 24/7/16.
 */
public class BasicSftpClient {
    private SftpDownloadConfig config;
    private JSch sftp;

    public BasicSftpClient(SftpDownloadConfig config) {
        this.config = config;
        sftp = new JSch();
    }

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
