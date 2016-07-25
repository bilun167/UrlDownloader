package downloader;

import exception.DownloadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by taihuynh on 10/7/16.
 */
public abstract class AbstractDownloader implements Downloader {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected URI parseURL(String url) throws URISyntaxException {
        return new URI(url);
    }

    @Override
    public File download(String url) throws DownloadException {
        try {
            URI uri = parseURL(url);
            return _download(uri, 3);
        } catch (URISyntaxException e) {
            logger.error("Cannot parse url {}", url);
            return null;
        }
    }

    protected File _download(URI uri, int retry) throws DownloadException {
        if (retry <= 0)
            return null;

        try {
            File file = _getFile(uri);

            if (file == null && retry > 0) {
                logger.debug("Download file unsuccessfully. Retrying. Number of retry left {}", retry - 1);
                return _download(uri, retry - 1);
            }

            return file;
        } catch (IOException e) {
            if (retry > 0) {
                logger.debug("Download file unsuccessfully. Retrying. Number of retry left {}. Exception: {}",
                        retry - 1, e.getMessage());
                return _download(uri, retry - 1);
            }
            throw new DownloadException(e.getMessage());
        }
    }

    protected abstract File _getFile(URI uri) throws IOException;
}
