package downloader.http;

import exception.BuildFileResponseException;
import org.apache.http.ContentTooLongException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.entity.ContentBufferEntity;
import org.apache.http.nio.protocol.AbstractAsyncResponseConsumer;
import org.apache.http.nio.util.HeapByteBufferAllocator;
import org.apache.http.nio.util.SimpleInputBuffer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Asserts;
import org.apache.http.util.EntityUtils;

import java.io.*;

/**
 * Created by taihuynh on 18/7/16.
 */
public class HttpAsyncResponseConsumer extends AbstractAsyncResponseConsumer<FileResponse> {

    private volatile HttpResponse response;
    private volatile SimpleInputBuffer buf;

    public HttpAsyncResponseConsumer() {
        super();
    }

    @Override
    protected void onResponseReceived(final HttpResponse response) throws IOException {
        System.out.println("Receiving");
        this.response = response;
    }

    @Override
    protected void onEntityEnclosed(
            final HttpEntity entity, final ContentType contentType) throws IOException {
        long len = entity.getContentLength();
        if (len > Integer.MAX_VALUE) {
            throw new ContentTooLongException("Entity content is too long: " + len);
        }
        if (len < 0) {
            len = 4096;
        }
        this.buf = new SimpleInputBuffer((int) len, new HeapByteBufferAllocator());
        this.response.setEntity(new ContentBufferEntity(entity, this.buf));
    }

    @Override
    protected void onContentReceived(
            final ContentDecoder decoder, final IOControl ioctrl) throws IOException {
        Asserts.notNull(this.buf, "Content buffer");
        this.buf.consumeContent(decoder);
    }

    @Override
    protected void releaseResources() {
        this.response = null;
        this.buf = null;
    }

    @Override
    protected FileResponse buildResult(final HttpContext context) throws IOException {
        byte[] content = "".getBytes();
        String filePath = (String) context.getAttribute("filePath");
        if (filePath == null)
            throw new FileNotFoundException();

        File f = new File(filePath);
        try(FileOutputStream fos = new FileOutputStream(f)) {
            content = EntityUtils.toByteArray(response.getEntity());
            fos.write(content);
            fos.close();

            EntityUtils.consume(response.getEntity());
            return new FileResponse(response, f);
        } catch (IOException e) {
            e.printStackTrace();
            InputStream is = new ByteArrayInputStream(content);
            throw new BuildFileResponseException("Cannot build content", response, is);
        }
    }

}
