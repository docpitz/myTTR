/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr.logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.User;
import com.jmelzer.myttr.activities.MySettingsActivity;
import com.jmelzer.myttr.db.LoginDataBaseAdapter;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoginManager {
    public static final String LOGGEDINAS = "LOGGEDINAS";
    public static String un;
    public static String pw;

    LoginDataBaseAdapter loginDataBaseAdapter;

    public LoginDataBaseAdapter getLoginDataBaseAdapter() {
        if (loginDataBaseAdapter == null) {
            loginDataBaseAdapter = new LoginDataBaseAdapter(MyApplication.getAppContext());
            loginDataBaseAdapter.open();
        }
        return loginDataBaseAdapter;
    }

    public boolean relogin() throws IOException {
        if (un == null || pw == null) {
            User user = readUserFromDB();
            if (user != null) {
                MyApplication.setLoginUser(user);
                un = user.getUsername();
                pw = user.getPassword();
            } else {
                throw new IllegalArgumentException("login must be called befor relogin");
            }
        }
        return login(un, pw);

    }

    private User readUserFromDB() {
        return getLoginDataBaseAdapter().getSinlgeEntry();
    }

    public void logout() {
        Client.getCookieStore().clear();
    }

    public boolean login(String username, String password) throws IOException {
        logout();
        HttpPost httpPost = new HttpPost("http://www.mytischtennis.de/community/login");

        HttpGet httpGet2 = new HttpGet("http://www.mytischtennis.de/community/index");
        HttpParams gethttpParams = new BasicHttpParams();
        gethttpParams.setParameter("fromLogin", null);
        httpGet2.setParams(gethttpParams);

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("userNameB", username));
        nvps.add(new BasicNameValuePair("userPassWordB", password));

        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        HttpResponse response = Client.execute(httpPost);
        Log.d(Constants.LOG_TAG, "status code 1=" + response.getStatusLine().getStatusCode());
        response.getEntity().consumeContent();
        response = Client.client.execute(httpGet2);
        Log.d(Constants.LOG_TAG, "status code 2=" + response.getStatusLine().getStatusCode());
        response.getEntity().consumeContent();
        for (Cookie cookie : Client.getCookieStore().getCookies()) {
            if (LOGGEDINAS.equals(cookie.getName())) {
                un = username;
                pw = password;
                return true;
            }
        }

        return false;
    }

    void p() {
        for (Cookie cookie : Client.getCookieStore().getCookies()) {
            Log.d(Constants.LOG_TAG, "name/value = " + cookie.getName() + "/" + cookie.getValue());
        }
    }

    public User loadUserIntoMemoryAndStore(String username, String pw, int ttr,
                                           Boolean saveUser, MyTischtennisParser myTischtennisParser) {
        String name = myTischtennisParser.getRealName();
        User userDb = getLoginDataBaseAdapter().getSinlgeEntry();
        int ak = 16;
        if (userDb != null) {
            MyApplication.manualClub = userDb.getClubName();
            ak = userDb.getAk();
        }
        MyApplication.setLoginUser(new User(name, username, pw, ttr, new Date(), MyApplication.manualClub, ak));
        getLoginDataBaseAdapter().deleteEntry(username);

        if (saveUser) {
            getLoginDataBaseAdapter().insertEntry(name, username, pw, ttr, MyApplication.manualClub, ak);
        }
        return new User(name, username, pw, ttr, new Date(), MyApplication.manualClub, ak);
    }

    public void storeClub(String name) {
        getLoginDataBaseAdapter().storeClub(name);
    }
}
