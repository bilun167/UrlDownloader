package downloader.http;

import com.google.inject.Singleton;
import org.apache.http.*;
import org.apache.http.client.entity.DeflateDecompressingEntity;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.protocol.HttpContext;

/**
 * Created by taihuynh on 18/7/16.
 */
@Singleton
public class BasicResponseInterceptor implements HttpResponseInterceptor {
    @Override
    public void process(final HttpResponse response, final HttpContext context) {
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            Header ceheader = entity.getContentEncoding();
            if (ceheader != null) {
                HeaderElement[] codecs = ceheader.getElements();
                for (int i = 0; i < codecs.length; i++) {
                    if (codecs[i].getName().equalsIgnoreCase("gzip")) {
                        response.setEntity(new GzipDecompressingEntity(response.getEntity()));
                    } else if (codecs[i].getName().equalsIgnoreCase("deflate")) {
                        response.setEntity(new DeflateDecompressingEntity(response.getEntity()));
                    } else
                        ;
                }
            }
        }
    }
}
