package com.jmelzer.myttr.logic;

import android.util.Log;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.db.NotificationDataBaseAdapter;
import com.jmelzer.myttr.model.LastNotification;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by J. Melzer on 03.09.2017.
 */

public class VersionChecker extends AbstractBaseParser {
    static final String GITHUB_URL = "https://github.com";
//    public static final String LAST_VERSION = "3.0.1 Suche verbessert";
    public static final String LAST_VERSION = "3.0.2 - Mecklenburg hinzugefügt";
    public static final String THIS_VERSION = "4.0.0 - Mytischtennis/click-tt";

    NotificationDataBaseAdapter adapter;

    NotificationDataBaseAdapter getDBAdapter() {
        if (adapter == null) {
            adapter = new NotificationDataBaseAdapter(MyApplication.getAppContext());
            adapter.open();
        }
        return adapter;
    }

    public LastNotification getLastCheck() {
        return getDBAdapter().getEntryByType(LastNotification.VERSION_TYPE);
    }

    public boolean shallCheck() {
        LastNotification lastNotification = getLastCheck();
        if (lastNotification == null) {
            return true;
        }
        Calendar thatDay = Calendar.getInstance();
        thatDay.setTime(lastNotification.getChangedAt());
        Calendar today = Calendar.getInstance();
        long diff = today.getTimeInMillis() - thatDay.getTimeInMillis();
        long days = diff / (24 * 60 * 60 * 1000);
        if (days > 7)
            return true;

        return false;
    }

    public boolean newVersionAvailable() throws NetworkException {
        String page = Client.getPage(GITHUB_URL + "/chokdee/myTTR/releases");
        return compareVersions(page, THIS_VERSION);
    }

    public boolean compareVersions(String page, String versionThis) {
        String[] ahref = parseLastVersion(page);
        return ahref != null && !ahref[1].equals(versionThis);
    }

    public String[] readVersionInfo() throws NetworkException {
        String page = Client.getPage(GITHUB_URL + "/chokdee/myTTR/releases");
        String[] ahref = parseLastVersion(page);
        if (ahref != null && !ahref[1].equals(THIS_VERSION))
            return ahref;
        else
            return null;

    }

    String[] parseLastVersion(String page) {
        try {
            ParseResult result = readBetween(page, 0, "<div class=\"release-header\">", "</div>");
            String[] ahref = readHrefAndATag(result.result);
            ahref[0] = GITHUB_URL + ahref[0];
            storeLastRead();
            return ahref;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(Constants.LOG_TAG, "error parsing " + e.getMessage());
        }

        return null;
    }

    private void storeLastRead() {
        getDBAdapter().insertEntry(LastNotification.VERSION_TYPE, "");
    }
}
