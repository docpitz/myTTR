package com.jmelzer.myttr;

import com.jmelzer.myttr.logic.CookieStoreDelegate;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.cookie.Cookie;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by J. Melzer on 24.02.2015.
 */
public class MockHttpClient implements HttpClient, CookieStoreDelegate.CookieStoreHolder {

    private final BasicHttpParams params = new BasicHttpParams();
    private CookieStore cookieStore;

    @Override
    public HttpResponse execute(HttpUriRequest request) throws IOException,
            ClientProtocolException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(MockResponses.forRequest(request));
//        InputStream mockInputStream = context.getAssets().open(MockResponses.forRequest(request));
        return new MockHttpResponse(in);
    }

    @Override
    public HttpParams getParams() {
        return params;
    }


    @Override
    public ClientConnectionManager getConnectionManager() {
        return null;
    }

    @Override
    public HttpResponse execute(HttpUriRequest httpUriRequest, HttpContext httpContext) throws IOException, ClientProtocolException {
        return null;
    }

    @Override
    public HttpResponse execute(HttpHost httpHost, HttpRequest httpRequest) throws IOException, ClientProtocolException {
        return null;
    }

    @Override
    public HttpResponse execute(HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext) throws IOException, ClientProtocolException {
        return null;
    }

    @Override
    public <T> T execute(HttpUriRequest httpUriRequest, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        return null;
    }

    @Override
    public <T> T execute(HttpUriRequest httpUriRequest, ResponseHandler<? extends T> responseHandler, HttpContext httpContext) throws IOException, ClientProtocolException {
        return null;
    }

    @Override
    public <T> T execute(HttpHost httpHost, HttpRequest httpRequest, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
        return null;
    }

    @Override
    public <T> T execute(HttpHost httpHost, HttpRequest httpRequest, ResponseHandler<? extends T> responseHandler, HttpContext httpContext) throws IOException, ClientProtocolException {
        return null;
    }

    @Override
    public CookieStore getCookieStore() {
        if (cookieStore != null) {
            return cookieStore;
        }
        
        cookieStore = new CookieStore() {
            List<Cookie> cookies = new ArrayList<>();

            @Override
            public void addCookie(Cookie cookie) {
                cookies.add(cookie);
            }

            @Override
            public List<Cookie> getCookies() {
                return cookies;
            }

            @Override
            public boolean clearExpired(Date date) {
                return false;
            }

            @Override
            public void clear() {

            }
        };
        return cookieStore;
    }
}