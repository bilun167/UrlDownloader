package downloader.http;

import org.apache.http.HttpResponse;

/**
 * Extends the apache HttpResponse to include other information and method to
 * build the content from the actual response. In this homework, we only need
 * FileResponse as concrete implementation of this interface.
 *
 * @param <T>
 *            expected type of the response content (String, File or Json)
 *
 * Created by taihuynh on 18/7/16.
 */
public interface IResponse<T> extends HttpResponse {
    /**
     * <p>
     * Retrieve the contents from the response
     * </p>
     * <p>
     * Examples: contents can be a string, or a file, or maybe json
     * </p>
     *
     * @return the content build from the actual response
     */
    public T content();

}
