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

import com.crashlytics.android.Crashlytics;
import com.jmelzer.myttr.activities.MySettingsActivity;
import com.jmelzer.myttr.activities.UnCaughtException;
import com.jmelzer.myttr.db.DataBaseHelper;
import com.jmelzer.myttr.db.FavoriteDataBaseAdapter;
import com.jmelzer.myttr.db.LoginDataBaseAdapter;
import com.jmelzer.myttr.db.NotificationDataBaseAdapter;
import com.jmelzer.myttr.model.Cup;
import com.jmelzer.myttr.model.Head2HeadResult;
import com.jmelzer.myttr.model.Saison;
import com.jmelzer.myttr.model.Verein;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.fabric.sdk.android.Fabric;

import static com.jmelzer.myttr.Constants.ACTUAL_SAISON;

public class MyApplication extends Application {

    public static boolean nobadPeopleVerificationNeeded = false;
    private static Context context;
    private static User loginUser;
    private static List<Player> ttrCalcPlayer = new ArrayList<Player>();
    public static Player actualPlayer;
    public static List<TeamAppointment> teamAppointments;
    public static int result;
    public static List<Player> clubPlayers;
    public static List<MyTTLiga> myTTLigen;
    public static List<Tournament> myTournaments;
    private static List<Event> events;
    public static List<Player> foreignTeamPlayers;
    public static List<Player> myTeamPlayers;
    public static List<Player> searchResult;
    public static EventDetail currentDetail;
    public static String selectedPlayerName;
    public static Player selectedPlayer;
    public static Player simPlayer;
    public static String manualClub;
    public static boolean actualTTR = true;

    // The following line should be changed to include the correct property id.
    private static final String PROPERTY_ID = "UA-59824393-1";
    public static Competition selectedCompetition;
    public static Participant selectedParticipant;

    static {
        createEmptyUser();
    }

    private static Liga selectedLiga;
    public static Mannschaft selectedMannschaft;
    public static Saison saison = ACTUAL_SAISON;
    public static Tournament selectedTournament;
    public static Verein selectedVerein;
    public static Mannschaftspiel selectedMannschaftSpiel;
    public static Verband selectedVerband;
    public static Cup selectedCup;
    static Spieler selectedLigaSpieler;
    private static List<Head2HeadResult> head2Head;

    public static void setLoginUser(User loginUser) {
        MyApplication.loginUser = loginUser;
    }

    public static void setHead2Head(List<Head2HeadResult> head2Head) {
        MyApplication.head2Head = head2Head;
    }

    public static Spieler getSelectedLigaSpieler() {
        return selectedLigaSpieler;
    }

    public static void setSelectedLigaSpieler(Spieler selectedLigaSpieler) {
        MyApplication.selectedLigaSpieler = selectedLigaSpieler;
    }

    public static List<Head2HeadResult> getHead2Head() {
        return head2Head;
    }

    public void onCreate() {
        Log.d(Constants.LOG_TAG, "myapplication oncreate");
        super.onCreate();
        try {
            Fabric fabric = new Fabric.Builder(this).kits(new Crashlytics()).debuggable(true).build();
            Fabric.with(fabric);
        } catch (Exception e) {
            //ignore
        }
        MyApplication.context = getApplicationContext();
        DataBaseHelper dataBaseHelper = DataBaseHelper.getInstance(this);
//        dataBaseHelper.registerAdapter(new FavoriteLigaDataBaseAdapter(this));
        dataBaseHelper.registerAdapter(new LoginDataBaseAdapter(this));
        dataBaseHelper.registerAdapter(new NotificationDataBaseAdapter(this));
        dataBaseHelper.registerAdapter(new FavoriteDataBaseAdapter(this));
        createEmptyUser();

        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static void setSelectedLiga(Liga selectedLiga) {
        MyApplication.selectedLiga = selectedLiga;
    }

    public static List<Mannschaftspiel> getSpieleForActualMannschaft() {
        if (selectedMannschaft != null) {
            if (selectedMannschaft.getSpiele().size() > 0) {
                return selectedMannschaft.getSpiele();
            }
            if (selectedLiga != null) {
                return selectedLiga.getSpieleFor(selectedMannschaft.getName());
            }
        }
        return new ArrayList<>();
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

    public static void resetCheckedForSearchResult() {
        for (Player p : searchResult) {
            p.setChecked(false);
        }
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
        return "Dein TTR: " + getPoints();
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

    public static List<Player> getTtrCalcPlayer() {
        return Collections.unmodifiableList(ttrCalcPlayer);
    }

    public static void addTTRCalcPlayer(final Player p) {
        if (ttrCalcPlayer.contains(p)) {
            return;
        }
        Player toAdd = new Player();
        toAdd.copy(p);
        toAdd.setChecked(false);
        ttrCalcPlayer.add(toAdd);
    }

    public static void removePlayer(Player p) {
        ttrCalcPlayer.remove(p);
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
     * @return idx , default is 4 (daily)
     */
    public int getTimerSetting() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPref.getInt(MySettingsActivity.KEY_PREF_TIMER, 4);
    }

    public void storeTimerSetting(int timer) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(MySettingsActivity.KEY_PREF_TIMER, timer);
        editor.apply();
    }

    public static int getSelectedTTR() {
        if (actualPlayer != null) {
            return actualPlayer.getTtrPoints();
        } else {
            return loginUser.getPoints();
        }
    }

    public int getTimerSettingInMinutes() {
        //in minutes
        int[] interval = new int[]{
                5, 10, 30, 60, 24 * 60, 24 * 60 * 7};

        int idx = getTimerSetting();
        if (idx <= interval.length - 1) {
            return interval[idx] * 1000 * 60;
        } else {
            return -1;
        }
    }

    public static String getStatistikTextForPlayer() {
        if (MyApplication.selectedPlayerName != null) {
            return selectedPlayerName;
        } else {
            return MyApplication.getLoginUser().getInfo();
        }
    }

    public static List<Event> getEvents() {
        if (events == null) {
            return new ArrayList<>();
        }
        return events;
    }

    public static void setEvents(List<Event> events) {
        MyApplication.events = events;
    }

}