package downloader.http;

import org.apache.http.HttpResponse;

import java.io.File;

/**
 * Created by taihuynh on 18/7/16.
 */
public class FileResponse extends WrappedResponse implements IResponse<File> {
    private final File content;

    public FileResponse(final HttpResponse raw, final File content) {
        super(raw);
        this.content = content;
    }

    @Override
    public File content() {
        return content;
    }

}
