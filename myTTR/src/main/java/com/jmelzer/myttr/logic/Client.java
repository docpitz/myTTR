/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr.logic;

import android.util.Log;
import com.jmelzer.myttr.Constants;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
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
import java.util.zip.GZIPInputStream;

public class Client {
    static {
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
        HttpConnectionParams.setSoTimeout(httpParams, 90000);
        HttpConnectionParams.setTcpNoDelay(httpParams, true);
        httpParams.setParameter("http.protocol.handle-redirects", true);

        Client.client = new DefaultHttpClient(httpParams);
        Client.client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64; " +
                "rv:34.0) Gecko/20100101 Firefox/34.0");
        Client.client.getParams().setParameter("Accept-Encoding" , "gzip, deflate");
    }
    public static DefaultHttpClient client;

    public static String getPage(String url) throws NetworkException {
        long start = System.currentTimeMillis();
        HttpGet httpGet = prepareGet(url);
        try {
            Log.i(Constants.LOG_TAG, "calling url '"  + url + "'");
            HttpResponse response = Client.client.execute(httpGet);
            Log.i(Constants.LOG_TAG, "execute time "  + (System.currentTimeMillis() - start)  + " ms");
            String s = readGzippedResponse(response);
            response.getEntity().consumeContent();
            Log.i(Constants.LOG_TAG, "request time " + (System.currentTimeMillis() - start)
                    + " ms , returncode = " + response.getStatusLine().getStatusCode());
            return s;
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "", e);
            throw new NetworkException(e);
        }
    }

    private static String readGzippedResponse(HttpResponse response) throws IOException {

        InputStream instream = response.getEntity().getContent();
        Header contentEncoding = response.getFirstHeader("Content-Encoding");
        if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
            instream = new GZIPInputStream(instream, 20000);
        }
        BufferedReader rd = null;
        InputStreamReader in = null;
        StringBuilder page = new StringBuilder(20000);
        try {
            in = new InputStreamReader(instream);
            rd = new BufferedReader(in, 8000);

            String line = "";
            while ((line = rd.readLine()) != null) {
                page .append(line);
            }

        } finally {
            close(in);
            close(instream);
            close(rd);
        }

//        Log.d(Constants.LOG_TAG, page);
        return page.toString();
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
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate");
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        return httpGet;
    }
}
