package guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;

import downloader.ftp.BasicFtpClient;
import downloader.ftp.FtpDownloadConfig;
import downloader.ftp.FtpDownloader;
import utils.JacksonConfig;
import utils.JacksonConfigFormat;

/**
 * {@link Guice} module for {@link FtpDownloader}
 * 
 * Created by taihuynh on 18/7/16.
 */
public class FtpModule extends AbstractModule {
    public static final String DEFAULT_CONFIG_PATH = "conf/ftpDownloadConfig.json";
    public static final String DEFAULT_SYSTEM_VAR = "ftpDownloadConfig";

    private FtpDownloadConfig ftpDownloadConfig = null;

    /**
     * If the configuration file exists, it will be loaded once and returned, else the default
     * configuration will be returned.
     * @return
     */
    protected FtpDownloadConfig readDefaultHttpDownloadConfig() {
        if (ftpDownloadConfig == null) {
            // If the configuration file does not exist, default values is used
            ftpDownloadConfig = JacksonConfig.readConfig(DEFAULT_CONFIG_PATH, DEFAULT_SYSTEM_VAR,
                    FtpDownloadConfig.class, JacksonConfigFormat.JSON);

            if (ftpDownloadConfig == null) {
                ftpDownloadConfig = new FtpDownloadConfig();
            }
        }
        return ftpDownloadConfig;
    }

    @Override
    protected void configure() {
        if (ftpDownloadConfig == null)
            ftpDownloadConfig = readDefaultHttpDownloadConfig();
        bind(FtpDownloadConfig.class).toInstance(ftpDownloadConfig);

        BasicFtpClient client = new BasicFtpClient(ftpDownloadConfig);
        bind(BasicFtpClient.class).toInstance(client);
    }
}
