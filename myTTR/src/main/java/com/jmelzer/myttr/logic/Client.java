/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr.logic;

import android.util.Log;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.utils.StringUtils;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.GZIPInputStream;

public class Client {
    static HttpClient client;
    public static CookieStoreDelegate cookieStoreDelegate;
    public static String lastUrl = "undefined";
    static final Lock lock = new ReentrantLock();


    static {
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
        HttpConnectionParams.setSoTimeout(httpParams, 90000);
        HttpConnectionParams.setTcpNoDelay(httpParams, true);
        httpParams.setParameter("http.protocol.handle-redirects", true);

        client = new DefaultHttpClient(httpParams);
        cookieStoreDelegate = new CookieStoreDelegate();
        cookieStoreDelegate.setHttpClient(client);
        Client.client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64; " +
                "rv:35.0) Gecko/20100101 Firefox/35.0");
        Client.client.getParams().setParameter("Accept-Encoding", "gzip, deflate");
//        Client.client.getParams().setParameter("Content-Type", "application/x-www-form-urlencoded");
//        Client.client.getParams().setParameter("Accept-Language", "de,en-US;q=0.7,en;q=0.3");
        client.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
        client.getParams().setParameter("http.protocol.content-charset", "UTF-8");
    }



    public synchronized static String getPage(String url) throws NetworkException {
        long start = System.currentTimeMillis();
        HttpGet httpGet = prepareGet(url);
        try {
            lastUrl = url;
            Log.i(Constants.LOG_TAG, "calling url '" + url + "'");
            lock.lock();
            HttpResponse response = Client.client.execute(httpGet);
            Log.i(Constants.LOG_TAG, "execute time " + (System.currentTimeMillis() - start) + " ms");
            String s = readGzippedResponse(response);
            response.getEntity().consumeContent();
            Log.i(Constants.LOG_TAG, "request time " + (System.currentTimeMillis() - start)
                    + " ms , returncode = " + response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == 500) {
                throw new NetworkException("die Webseite meldet zur Zeit einen Fehler zurück :-(");
            }
            return s;
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "", e);
            throw new NetworkException(e);
        } finally {
            lock.unlock();
        }
    }

    public static String readGzippedResponse(HttpResponse response) throws IOException {

        InputStream instream = response.getEntity().getContent();
        Header contentEncoding = response.getFirstHeader("Content-Encoding");
        if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
            instream = new GZIPInputStream(instream, 20000);
        }
        BufferedReader rd = null;
        InputStreamReader in = null;
        StringBuilder page = new StringBuilder(20000);
        String pageS = null;
        try {
            in = new InputStreamReader(instream, "UTF-8");
            rd = new BufferedReader(in, 8000);

            long start = System.currentTimeMillis();
            String line = "";
            while ((line = rd.readLine()) != null) {
//                line = StringEscapeUtils.unescapeHtml4(line);
//                line = StringUtils.unescapeHtml3(line);
//                Log.d(Constants.LOG_TAG, line);
                page.append(line);
            }
            pageS = StringUtils.unescapeHtml3(page.toString());
            Log.d(Constants.LOG_TAG, "read from stream takes " + (System.currentTimeMillis() - start) + " ms");
        } finally {
            close(in);
            close(instream);
            close(rd);
        }


//        return page.toString();
        return pageS;
    }

    static void close(Closeable c) {
        if (c == null) {
            return;
        }
        try {
            c.close();
        } catch (IOException e) {
            //log the exception
        }
    }

    private static HttpGet prepareGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept-Language", "de,en-US;q=0.7,en;q=0.3");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate");
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        return httpGet;
    }

    public static HttpResponse execute(HttpPost httpPost) throws IOException {
        long start = System.currentTimeMillis();
        lock.lock();
        HttpResponse response;
        try {
            response = client.execute(httpPost);
        } finally {
            lock.unlock();
        }
        Log.d(Constants.LOG_TAG, "post execute takes " + (System.currentTimeMillis() - start) + " ms");
        return response;
    }

    public static void setHttpClient(HttpClient sClient) {
        client = sClient;
        cookieStoreDelegate.setHttpClient(client);
    }

    public static CookieStore getCookieStore() {
        return cookieStoreDelegate.getCookieStore();
    }
}
