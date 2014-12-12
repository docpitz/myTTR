/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr.logic;

import android.util.Log;
import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.User;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class LoginManager {

    public boolean login(String username, String password) throws IOException {

        HttpPost httpPost = new HttpPost("http://www.mytischtennis.de/community/login");

        HttpGet httpGet2 = new HttpGet("http://www.mytischtennis.de/community/index");
        HttpParams gethttpParams = new BasicHttpParams();
        gethttpParams.setParameter("fromLogin", null);
        httpGet2.setParams(gethttpParams);

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("userNameB", username));
        nvps.add(new BasicNameValuePair("userPassWordB", password));

//        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            HttpResponse response = Client.client.execute(httpPost);
            Log.d(Constants.LOG_TAG, "status code 1" + response.getStatusLine().getStatusCode());
            response = Client.client.execute(httpGet2);
            Log.d(Constants.LOG_TAG, "status code 2" + response.getStatusLine().getStatusCode());
            p();
            for (Cookie cookie : Client.client.getCookieStore().getCookies()) {
//                System.out.println("cookie.name = " + cookie.getName());
//                System.out.println("cookie.value = " + cookie.getValue());
                if ("LOGGEDINAS".equals(cookie.getName())) {
                    return true;
                }
            }

//        } catch (IOException e) {
//            Log.e(Constants.LOG_TAG, "", e);
//        }


        return false;
    }
    void p() {
        for (Cookie cookie : Client.client.getCookieStore().getCookies()) {
            Log.d(Constants.LOG_TAG, "name/value = " + cookie.getName() + "/" + cookie.getValue());
        }
    }
}
