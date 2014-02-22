/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr.parser;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginManager {

    public boolean login(String username, String password) {


        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
        HttpConnectionParams.setSoTimeout(httpParams, 20000);
        httpParams.setParameter("http.protocol.handle-redirects",false);

        Client.client = new DefaultHttpClient(httpParams);
        Client.client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0");
        HttpPost httpPost = new HttpPost("http://www.mytischtennis.de/community/login");

        HttpGet httpGet2 = new HttpGet("http://www.mytischtennis.de/community/index");
        HttpParams gethttpParams = new BasicHttpParams();
        gethttpParams.setParameter("fromLogin", null);
        httpGet2.setParams(gethttpParams);

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("userName", username));
        nvps.add(new BasicNameValuePair("userPassWord", password));

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            Client.client.execute(httpPost);
            Client.client.execute(httpGet2);

            for (Cookie cookie : Client.client.getCookieStore().getCookies()) {
//                System.out.println("cookie.name = " + cookie.getName());
//                System.out.println("cookie.value = " + cookie.getValue());
                if ("LOGGEDINAS".equals(cookie.getName())) {
                    return true;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        return false;
    }
    void p() {
        for (Cookie cookie : Client.client.getCookieStore().getCookies()) {
            System.out.println("cookie.name = " + cookie.getName());
            System.out.println("cookie.value = " + cookie.getValue());
        }
    }
}
