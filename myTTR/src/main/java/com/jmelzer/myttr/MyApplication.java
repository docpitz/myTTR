/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.jmelzer.myttr.activities.MySettingsActivity;
import com.jmelzer.myttr.db.DataBaseHelper;
import com.jmelzer.myttr.db.FavoriteLigaDataBaseAdapter;
import com.jmelzer.myttr.db.LoginDataBaseAdapter;
import com.jmelzer.myttr.db.NotificationDataBaseAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MyApplication extends Application {

    private static Context context;
    private static User loginUser;
    private static List<Player> players = new ArrayList<Player>();
    public static Player actualPlayer;
    public static List<TeamAppointment> teamAppointments;
    public static int result;
    public static List<Player> clubPlayers;
    public static List<Event> events;
    public static List<Player> foreignTeamPlayers;
    public static List<Player> myTeamPlayers;
    public static List<Player> searchResult;
    public static EventDetail currentDetail;
    public static String selectedPlayer;
    public static Player simPlayer;
    public static String manualClub;

    static {
        createEmptyUser();
    }

    private static Liga selectedLiga;
    public static Mannschaft selectedMannschaft;
    public static Mannschaftspiel selectedMannschaftSpiel;
    public static Verband selectedVerband;
    public static Spieler selectedLigaSpieler;

    public static void setLoginUser(User loginUser) {
        MyApplication.loginUser = loginUser;
    }

    public void onCreate() {
        Log.d(Constants.LOG_TAG, "myapplication oncreate");
        super.onCreate();
        MyApplication.context = getApplicationContext();
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(this);
        dataBaseHelper.registerAdapter(new FavoriteLigaDataBaseAdapter(this));
        dataBaseHelper.registerAdapter(new LoginDataBaseAdapter(this));
        dataBaseHelper.registerAdapter(new NotificationDataBaseAdapter(this));
        createEmptyUser();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static void setSelectedLiga(Liga selectedLiga) {
        MyApplication.selectedLiga = selectedLiga;
    }

    public static Liga getSelectedLiga() {
        return selectedLiga;
    }

    public static User getLoginUser() {
        return loginUser;
    }

    public static boolean userIsEmpty() {
        return loginUser == null || loginUser.getUsername().isEmpty();
    }

    public static int getPoints() {
        return loginUser != null ? loginUser.getPoints() : 0;
    }

    public static void createEmptyUser() {
        loginUser = new User("", "", "", 0, new Date(), null, 16);
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }

    public static String getTitle() {
        return "TTR: " + getPoints();
    }

    public static int calcActualDiff() {
        int diff = 0;
        if (simPlayer == null) {
            diff = result - loginUser.getPoints();
        } else {
            diff = result - simPlayer.getTtrPoints();
        }
        return diff;
    }

    public static List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public static void addPlayer(Player p) {
        players.add(p);
    }

    public static void removePlayer(Player p) {
        players.remove(p);
    }

    public static int getAk() {
        return getLoginUser().getAk();
    }

    public static String stringit() {
        return getLoginUser().toString();
    }

    /**
     * getting the index of EnterTimeactivity.interval.
     *
     * @return idx , default is 3
     */
    public int getTimerSetting() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPref.getInt(MySettingsActivity.KEY_PREF_TIMER, 3);
    }

    public void storeTimerSetting(int timer) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(MySettingsActivity.KEY_PREF_TIMER, timer);
        editor.apply();
    }


    public int getTimerSettingInMinutes() {
        //in minutes
        int[] interval = new int[]{
                1, 10, 30, 60, 24 * 60, 24 * 60 * 7};

        int idx = getTimerSetting();
        if (idx <= interval.length - 1) {
            return interval[idx] * 1000 * 60;
        } else {
            return -1;
        }
    }
}