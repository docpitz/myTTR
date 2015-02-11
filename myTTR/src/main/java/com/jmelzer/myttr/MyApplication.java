/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr;

import android.app.Application;
import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MyApplication extends Application {

    private static Context context;
    public static User loginUser = new User("", "", "", 0, new Date(), null);
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

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
        loginUser = new User("", "", "", 0, new Date(), null);
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }

    public static String getTitle() {
        return "Dein TTR: " + loginUser.getPoints();
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
}