package downloader.http;

import org.apache.http.*;
import org.apache.http.params.HttpParams;

import java.util.Locale;

/**
 * This class is a wrapper for a raw HttpResponse so that
 * we can build the typed-response IResponse<T> to store its content.
 *
 * Created by taihuynh on 18/7/16.
 */
public class WrappedResponse implements HttpResponse {
    private HttpResponse raw;

    public WrappedResponse(HttpResponse raw) {
        this.raw = raw;
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
        return raw.getProtocolVersion();
    }

    @Override
    public boolean containsHeader(String name) {
        return raw.containsHeader(name);
    }

    @Override
    public Header[] getHeaders(String name) {
        return raw.getHeaders(name);
    }

    @Override
    public Header getFirstHeader(String name) {
        return raw.getFirstHeader(name);
    }

    @Override
    public Header getLastHeader(String name) {
        return raw.getLastHeader(name);
    }

    @Override
    public Header[] getAllHeaders() {
        return raw.getAllHeaders();
    }

    @Override
    public void addHeader(Header header) {
        raw.addHeader(header);
    }

    @Override
    public void addHeader(String name, String value) {
        raw.addHeader(name, value);
    }

    @Override
    public void setHeader(Header header) {
        raw.setHeader(header);
    }

    @Override
    public void setHeader(String name, String value) {
        raw.setHeader(name, value);
    }

    @Override
    public void setHeaders(Header[] headers) {
        raw.setHeaders(headers);
    }

    @Override
    public void removeHeader(Header header) {
        raw.removeHeader(header);
    }

    @Override
    public void removeHeaders(String name) {
        raw.removeHeaders(name);
    }

    @Override
    public HeaderIterator headerIterator() {
        return raw.headerIterator();
    }

    @Override
    public HeaderIterator headerIterator(String name) {
        return raw.headerIterator(name);
    }

    @Override
    public HttpParams getParams() {
        return raw.getParams();
    }

    @Override
    public void setParams(HttpParams params) {
        raw.setParams(params);
    }

    @Override
    public StatusLine getStatusLine() {
        return raw.getStatusLine();
    }

    @Override
    public void setStatusLine(StatusLine statusline) {
        raw.setStatusLine(statusline);
    }

    @Override
    public void setStatusLine(ProtocolVersion ver, int code) {
        raw.setStatusLine(ver, code);
    }

    @Override
    public void setStatusLine(ProtocolVersion ver, int code, String reason) {
        raw.setStatusLine(ver, code, reason);
    }

    @Override
    public void setStatusCode(int code) throws IllegalStateException {
        raw.setStatusCode(code);
    }

    @Override
    public void setReasonPhrase(String reason) throws IllegalStateException {
        raw.setReasonPhrase(reason);
    }

    @Override
    public HttpEntity getEntity() {
        return raw.getEntity();
    }

    @Override
    public void setEntity(HttpEntity entity) {
        raw.setEntity(entity);
    }

    @Override
    public Locale getLocale() {
        return raw.getLocale();
    }

    @Override
    public void setLocale(Locale loc) {
        raw.setLocale(loc);
    }

}