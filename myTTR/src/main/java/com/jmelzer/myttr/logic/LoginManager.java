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

    public boolean relogin() throws IOException, NetworkException {
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
        try {
            return login(un, pw) != null;
        } catch (PlayerNotWellRegistered playerNotWellRegistered) {
            //shall not be possible on relogin
            return false;
        }

    }

    private User readUserFromDB() {
        return getLoginDataBaseAdapter().getSinlgeEntry();
    }

    public void logout() {
        Client.getCookieStore().clear();
    }

    public User login(String username, String password) throws IOException, NetworkException, PlayerNotWellRegistered {
        long start = System.currentTimeMillis();
        logout();
        HttpPost httpPost = new HttpPost("http://www.mytischtennis.de/community/login");

//        HttpGet httpGet2 = new HttpGet("http://www.mytischtennis.de/community/index");
        HttpParams gethttpParams = new BasicHttpParams();
        gethttpParams.setParameter("fromLogin", null);
//        httpGet2.setParams(gethttpParams);

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("userNameB", username));
        nvps.add(new BasicNameValuePair("userPassWordB", password));

        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        HttpResponse response = Client.execute(httpPost);
        Log.d(Constants.LOG_TAG, "status code login =" + response.getStatusLine().getStatusCode());
        response.getEntity().consumeContent();

        User user = new MyTischtennisParser().getPointsAndRealName();
        user.setPassword(password);
        user.setUsername(username);
//        response = Client.client.execute(httpGet2);
//        Log.d(Constants.LOG_TAG, "status code 2=" + response.getStatusLine().getStatusCode());
//        response.getEntity().consumeContent();
        for (Cookie cookie : Client.getCookieStore().getCookies()) {
            if (LOGGEDINAS.equals(cookie.getName())) {
                un = username;
                pw = password;
                Log.d(Constants.LOG_TAG, "login time = " + (System.currentTimeMillis() - start) + " ms");
                return user;
            }
        }

        return null;
    }

    void p() {
        for (Cookie cookie : Client.getCookieStore().getCookies()) {
            Log.d(Constants.LOG_TAG, "name/value = " + cookie.getName() + "/" + cookie.getValue());
        }
    }

    public User loadUserIntoMemoryAndStore(User user,
                                           Boolean saveUser, MyTischtennisParser myTischtennisParser) {
//        String name = myTischtennisParser.getRealName();
        User userDb = getLoginDataBaseAdapter().getSinlgeEntry();
        int ak = 16;
        if (userDb != null) {
            MyApplication.manualClub = userDb.getClubName();
            user.setClubName(userDb.getClubName());
            ak = userDb.getAk();
        }
        user.setAk(ak);
        MyApplication.setLoginUser(user);
        getLoginDataBaseAdapter().deleteEntry(user.getUsername());

        if (saveUser) {
            getLoginDataBaseAdapter().insertEntry(user.getRealName(), user.getUsername(),
                    user.getPassword(), user.getPoints(), MyApplication.manualClub, ak);
        }
        return user;
    }

    public void storeClub(String name) {
        getLoginDataBaseAdapter().storeClub(name);
    }
}
