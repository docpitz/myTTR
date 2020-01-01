/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr.logic;

import android.util.Log;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.User;
import com.jmelzer.myttr.db.LoginDataBaseAdapter;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.jmelzer.myttr.logic.Client.readGzippedResponse;

public class LoginManager {
    public static final String LOGGEDINAS = "MYTT_COOKIESOK";
    public static final String MYTT_COOKIE2 = "cfid";
    public static String un;
    public static String pw;

    LoginDataBaseAdapter loginDataBaseAdapter;
    public static Cookie loginCookie;

    public LoginDataBaseAdapter getLoginDataBaseAdapter() {
        if (loginDataBaseAdapter == null) {
            loginDataBaseAdapter = new LoginDataBaseAdapter(MyApplication.getAppContext());
            loginDataBaseAdapter.open();
        }
        return loginDataBaseAdapter;
    }

    public static boolean isLoginExpired() {
        if (loginCookie != null && loginCookie.isExpired(new Date())) {
            return true;
        }
        return false;
    }

    public static boolean existLoginCookie() {
        return loginCookie != null;
    }

    public boolean relogin() throws IOException, NetworkException {
        if (un == null || pw == null) {
            User user = readUserFromDB();
            if (user != null) {
                MyApplication.setLoginUser(user);
                un = user.getUsername();
                pw = user.getPassword();
            } else {
                throw new IllegalArgumentException("login must be called before relogin");
            }
        }
        try {
            return login(un, pw) != null;
        } catch (PlayerNotWellRegistered | ValidationException | LoginException e) {
            return false;
        }

    }

    private User readUserFromDB() {
        return getLoginDataBaseAdapter().getSinlgeEntry();
    }

    public void logout() {
        Client.getCookieStore().clear();
    }

    public User login(String username, String password) throws IOException, NetworkException, PlayerNotWellRegistered, LoginException, ValidationException {
        long start = System.currentTimeMillis();
        logout();
        HttpPost httpPost = new HttpPost("https://www.mytischtennis.de/community/login");
        HttpParams gethttpParams = new BasicHttpParams();
        gethttpParams.setParameter("fromLogin", null);


        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("userNameB", username));
        nvps.add(new BasicNameValuePair("userPassWordB", password));
        nvps.add(new BasicNameValuePair("targetPage", "index?fromlogin=1?fromlogin=1"));
        nvps.add(new BasicNameValuePair("goLogin", "Einloggen"));

        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        HttpResponse response = Client.execute(httpPost);

//        Log.d(Constants.LOG_TAG, "status code login =" + response.getStatusLine().getStatusCode());
//
//        for (Cookie cookie : Client.getCookieStore().getCookies()) {
//            Log.d(Constants.LOG_TAG, "cookie = " + cookie.getName());
//        }

        String page = readGzippedResponse(response);
        response.getEntity().consumeContent();
        if (page.contains("Deine Zugangsdaten sind nicht korrekt!")) {
            throw new LoginException("Deine Zugangsdaten sind nicht korrekt");
        }

        User user = null;
        try {
            user = new MyTischtennisParser().getPointsAndRealName();
            user.setPassword(password);
            user.setUsername(username);
        } catch (LoginException e) {
            throw e;
        } catch (ValidationException e) {
            MyApplication.getLoginUser().setRegistered(false);
            throw e;
        } catch (Exception e) {
            MyApplication.getLoginUser().setRegistered(false);
            Log.e(Constants.LOG_TAG, "getPointsAndRealName", e);
            throw new LoginException("myTTR konnte deinen Namen nicht feststellen\n" +
                    "Fehlermeldung: " + e.getMessage());
        }
        for (Cookie cookie : Client.getCookieStore().getCookies()) {
            if (LOGGEDINAS.equals(cookie.getName()) || MYTT_COOKIE2.equals(cookie.getName())) {
                un = username;
                pw = password;
                loginCookie = cookie;
                Log.d(Constants.LOG_TAG, "login time = " + (System.currentTimeMillis() - start) + " ms");
                return user;
            }
        }
        Log.e(Constants.LOG_TAG, "myTTR konnte den Cookie nicht finden");
        throw new LoginException("myTTR konnte den Cookie nicht finden");
    }

    void p() {
        for (Cookie cookie : Client.getCookieStore().getCookies()) {
            Log.d(Constants.LOG_TAG, "name/value = " + cookie.getName() + "/" + cookie.getValue());
        }
    }

    public void loadUserIntoMemoryAndStore(User user,
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
    }

    public void storeClub(String name) {
        getLoginDataBaseAdapter().storeClub(name);
    }
}
