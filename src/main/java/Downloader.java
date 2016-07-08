import java.io.IOException;
import java.net.URL;

/**
 * Created by taihuynh on 8/7/16.
 */
public interface Downloader {
    void download(URL url) throws IOException;
}
