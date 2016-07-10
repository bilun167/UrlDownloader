package downloader;

import java.io.File;

/**
 * Created by taihuynh on 8/7/16.
 */
public interface Downloader {
    File download(String url) throws DownloadException;
}
