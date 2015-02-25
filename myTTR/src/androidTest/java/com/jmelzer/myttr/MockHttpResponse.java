package com.jmelzer.myttr;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.params.HttpParams;

public class MockHttpResponse implements HttpResponse {

    private final InputStream stream;
    private List<Header> headers;

    public MockHttpResponse(InputStream stream) {
        this.stream = stream;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }

    public HttpEntity getEntity() {
        return new MockHttpEntity(stream);
    }

    public Locale getLocale() {
        // TODO Auto-generated method stub
        return null;
    }

    public StatusLine getStatusLine() {
        return new StatusLine() {
            public int getStatusCode() {
                return 200;
            }
            public String getReasonPhrase() {
                return null;
            }

            public ProtocolVersion getProtocolVersion() {
                return null;
            }
        };
    }

    public void setEntity(HttpEntity entity) {
        throw new UnsupportedOperationException();
    }

    public void setLocale(Locale loc) {
        throw new UnsupportedOperationException();
    }

    public void setReasonPhrase(String reason) throws IllegalStateException {
        throw new UnsupportedOperationException();
    }

    public void setStatusCode(int code) throws IllegalStateException {
        throw new UnsupportedOperationException();
    }

    public void setStatusLine(StatusLine statusline) {
        throw new UnsupportedOperationException();
    }

    public void setStatusLine(ProtocolVersion ver, int code) {
        throw new UnsupportedOperationException();
    }

    public void setStatusLine(ProtocolVersion ver, int code, String reason) {
        throw new UnsupportedOperationException();
    }

    public void addHeader(Header header) {
        throw new UnsupportedOperationException();
    }

    public void addHeader(String name, String value) {
        throw new UnsupportedOperationException();
    }

    public boolean containsHeader(String name) {
        throw new UnsupportedOperationException();
    }

    public Header[] getAllHeaders() {
        throw new UnsupportedOperationException();
    }

    public Header getFirstHeader(String name) {
        if (headers == null) return null;
        for(Header header : headers) {
            if (header.getName().equals(name))
                return header;
        }
        return null;
    }

    public Header[] getHeaders(String name) {
        throw new UnsupportedOperationException();
    }

    public Header getLastHeader(String name) {
        throw new UnsupportedOperationException();
    }

    public HttpParams getParams() {
        throw new UnsupportedOperationException();
    }

    public ProtocolVersion getProtocolVersion() {
        throw new UnsupportedOperationException();
    }

    public HeaderIterator headerIterator() {
        throw new UnsupportedOperationException();
    }

    public HeaderIterator headerIterator(String name) {
        throw new UnsupportedOperationException();
    }

    public void removeHeader(Header header) {
        throw new UnsupportedOperationException();
    }

    public void removeHeaders(String name) {
        throw new UnsupportedOperationException();
    }

    public void setHeader(Header header) {
        throw new UnsupportedOperationException();
    }

    public void setHeader(String name, String value) {
        throw new UnsupportedOperationException();
    }

    public void setHeaders(Header[] headers) {
        throw new UnsupportedOperationException();
    }

    public void setParams(HttpParams params) {
        throw new UnsupportedOperationException();
    }

}