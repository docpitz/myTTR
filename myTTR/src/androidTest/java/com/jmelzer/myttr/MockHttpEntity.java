package com.jmelzer.myttr;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

public class MockHttpEntity implements HttpEntity {

    private final InputStream stream;

    public MockHttpEntity(InputStream stream) {
        this.stream = stream;
    }

    public void consumeContent() throws IOException {

    }

    public InputStream getContent() throws IOException, IllegalStateException {
        return stream;
    }

    public Header getContentEncoding() {
        return null;
    }

    public long getContentLength() {
        return 0;
    }

    public Header getContentType() {
        return null;
    }

    public boolean isChunked() {
        return false;
    }

    public boolean isRepeatable() {
        return false;
    }

    public boolean isStreaming() {
        return false;
    }

    public void writeTo(OutputStream outstream) throws IOException {
    }

}