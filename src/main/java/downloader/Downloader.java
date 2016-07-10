package downloader;

import java.io.IOException;
import java.net.URI;

/**
 * Created by taihuynh on 8/7/16.
 */
public interface Downloader {
    void download(URI url) throws IOException;
}
