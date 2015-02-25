package com.jmelzer.myttr.logic;

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by J. Melzer on 24.02.2015.
 *
 */
public class CookieStoreDelegate {
    HttpClient httpClient;

    public CookieStoreDelegate() {

    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public CookieStore getCookieStore() {
        if (httpClient instanceof CookieStoreHolder) {
            return ((CookieStoreHolder)httpClient).getCookieStore();
        } else if (httpClient instanceof DefaultHttpClient) {
            return ((DefaultHttpClient)httpClient).getCookieStore();
        }
        throw new IllegalArgumentException(httpClient.getClass().getName());
    }

    public static interface CookieStoreHolder {
        CookieStore getCookieStore();
    }
}
