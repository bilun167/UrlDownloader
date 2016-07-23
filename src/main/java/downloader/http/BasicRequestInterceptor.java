package downloader.http;

import com.google.inject.Singleton;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

/**
 * Created by taihuynh on 18/7/16.
 */
@Singleton
public class BasicRequestInterceptor implements HttpRequestInterceptor {
    @Override
    public void process(final HttpRequest request, final HttpContext context) {
        if (!request.containsHeader("Accept")) {
            request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        }

        if (!request.containsHeader("Accept-Language")) {
            request.addHeader("Accept-Language", "en-us,en;q=0.5");
        }

        if (!request.containsHeader("Accept-Encoding")) {
            request.addHeader("Accept-Encoding", "gzip,deflate");
        }

        if (!request.containsHeader("Cache-Control")) {
            request.addHeader("Cache-Control", "no-cache");
        }
    }
}
