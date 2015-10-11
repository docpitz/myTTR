/*
 * Copyright (c) Juergen Melzer
 *
 * 2014.
 */

/*
* Author: J. Melzer
* Date: 22.02.14 
*
*/


package com.jmelzer.myttr.logic;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.Event;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.activities.EventsActivity;
import com.jmelzer.myttr.db.NotificationDataBaseAdapter;
import com.jmelzer.myttr.model.LastNotification;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SyncManager extends Service {

    private Timer timer;
    MyTischtennisParser parser = new MyTischtennisParser();
    private NotificationDataBaseAdapter adapter;
    public static boolean testIsRun = false;

    NotificationDataBaseAdapter getDBAdapter() {
        if (adapter == null) {
            adapter = new NotificationDataBaseAdapter(MyApplication.getAppContext());
            adapter.open();
        }
        return adapter;
    }

    /**
     * checks wether the logged in user have new points at mytischtennis.de since the last update in the db.
     *
     * @return true/false
     */
    boolean hasNewEventsOrPoints(boolean reloginDone) {
        try {
            LastNotification lastNotification = getDBAdapter().getEntryByType(LastNotification.EVENT_TYPE);
            if (lastNotification == null) {
                List<Event> events = parser.readEvents().getEvents();
                int ttr = parser.getPoints();
                storeNewEvent(events, ttr);
                return false;
            } else {
                int oldSize = lastNotification.convertEventSizeFromJson();
                int oldTTR = lastNotification.convertTTRFromJson();
                //tod
                List<Event> events = parser.readEvents().getEvents();
                int newSize = events.size();
                int newTTR = parser.getPoints();
                MyApplication.setEvents(events);
                if (newTTR != 0 && oldSize != newSize || oldTTR != newTTR) {
                    storeNewEvent(events, newTTR);
                    return true;
                }
            }

        } catch (LoginExpiredException e) {
            if (reloginDone) {
                return false;
            }
            try {
                new LoginManager().relogin();
                Log.d(Constants.LOG_TAG, "relogin success");
                return hasNewEventsOrPoints(true);
            } catch (NetworkException | IOException e1) {
                //ignore
                Log.d(Constants.LOG_TAG, "relogin error", e1);
            }
        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "error getting needed informations", e);
        }

        return false;
    }

    private void storeNewEvent(List<Event> events, int ttr) throws NetworkException, LoginExpiredException, PlayerNotWellRegistered, JSONException {

        getDBAdapter().insertEntry(LastNotification.EVENT_TYPE, LastNotification.convertToJson(events, ttr));
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        int period = ((MyApplication) getApplication()).getTimerSettingInMinutes();
        Log.d(Constants.LOG_TAG, "timer set to " + period);

        if (period == -1 || testIsRun) {
            return Service.START_NOT_STICKY;
        }

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.d(Constants.LOG_TAG, "timer received, checking for new events ...");

                if (hasNewEventsOrPoints(false)) {
                    Intent eventIntent = new Intent(SyncManager.this, EventsActivity.class);
                    eventIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    PendingIntent pIntent = PendingIntent.getActivity(SyncManager.this, 0, eventIntent, 0);

                    Notification n = new Notification.Builder(SyncManager.this)
                            .setContentTitle("Neue Ergebnisse von myTischtennis!")
                            .setContentText("Du hast neue Ergebnisse erhalten, schau sie dir an.")
                            .setSmallIcon(R.drawable.myttr)
                            .setContentIntent(pIntent)
                            .setAutoCancel(true)
                            .build();


                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                    notificationManager.notify(0, n);
                }
            }
            // sek min h
        }, 0, period);

        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            Log.d(Constants.LOG_TAG, "timer canceled");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
