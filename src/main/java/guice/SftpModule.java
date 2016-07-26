package guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;

import downloader.ftp.FtpDownloader;
import downloader.sftp.BasicSftpClient;
import downloader.sftp.SftpDownloadConfig;
import utils.JacksonConfig;
import utils.JacksonConfigFormat;

/**
 * {@link Guice} module for {@link SftpDownloader}
 * 
 * Created by taihuynh on 18/7/16.
 */
public class SftpModule extends AbstractModule {
    public static final String DEFAULT_CONFIG_PATH = "conf/sftpDownloadConfig.json";
    public static final String DEFAULT_SYSTEM_VAR = "sftpDownloadConfig";

    private SftpDownloadConfig sftpDownloadConfig = null;

    /**
     * If the configuration file exists, it will be loaded once and returned, else the default
     * configuration will be returned.
     * @return
     */
    protected SftpDownloadConfig readDefaultHttpDownloadConfig() {
        if (sftpDownloadConfig == null) {
            // If the configuration file does not exist, default values is used
            sftpDownloadConfig = JacksonConfig.readConfig(DEFAULT_CONFIG_PATH, DEFAULT_SYSTEM_VAR,
                    SftpDownloadConfig.class, JacksonConfigFormat.JSON);

            if (sftpDownloadConfig == null) {
                sftpDownloadConfig = new SftpDownloadConfig();
            }
        }
        return sftpDownloadConfig;
    }

    @Override
    protected void configure() {
        if (sftpDownloadConfig == null)
            sftpDownloadConfig = readDefaultHttpDownloadConfig();
        bind(SftpDownloadConfig.class).toInstance(sftpDownloadConfig);

        BasicSftpClient client = new BasicSftpClient(sftpDownloadConfig);
        bind(BasicSftpClient.class).toInstance(client);
    }
}
