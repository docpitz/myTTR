/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr;

import android.app.Application;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application {

    private static Context context;
    public static int ttrValue;
    public static List<Player> players = new ArrayList<Player>();
    public static Player actualPlayer;
    public static int result;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
    public static String getTitle() {
        return "Dein TTR: " + ttrValue;
    }
}