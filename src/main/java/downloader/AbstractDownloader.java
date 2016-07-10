package downloader;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by taihuynh on 10/7/16.
 */
public abstract class AbstractDownloader implements Downloader {
    protected URI parseURL(String url) throws URISyntaxException {
        return new URI(url);
    }

    @Override
    public File download(String url) throws DownloadException {
        try {
            URI uri = parseURL(url);
            return _getFile(uri);
        } catch (URISyntaxException|IOException e) {
            throw new DownloadException(e.getMessage());
        }
    }

    protected abstract File _getFile(URI uri) throws IOException;
}
