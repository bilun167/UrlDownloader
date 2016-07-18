package exception;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * Created by taihuynh on 18/7/16.
 */
public class BuildFileResponseException extends IOException {
    private static final long serialVersionUID = 1L;

    private final InputStream parsedResponse;
    private final HttpResponse rawResponse;

    public BuildFileResponseException(String message, HttpResponse rawResponse, InputStream inputStream) {
        super(message);
        this.parsedResponse = inputStream;
        this.rawResponse = rawResponse;
    }

    public InputStream getParsedResponse() {
        return parsedResponse;
    }

    public HttpResponse getRawResponse() {
        return rawResponse;
    }

    public String getLog() {
        return String.format("[BUILD_FILE_RESPONSE__EXCEPTION]-{%s}-{%s}", parsedResponse.toString(),
                super.getMessage());
    }

}