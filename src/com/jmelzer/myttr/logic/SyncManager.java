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
import android.preference.PreferenceManager;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.activities.MySettingsActivity;
import com.jmelzer.myttr.activities.NewPointsActivity;

import java.util.Timer;
import java.util.TimerTask;

public class SyncManager extends Service {

    static Intent startedIntent;
    static boolean started = false;
    private static Timer timer = new Timer();
    static public boolean notifcationSent = false;
    MyTischtennisParser parser = new MyTischtennisParser();
    public static int newTTRPoints;
    static SyncManager syncManager;

    public static SyncManager getInstance() {
        return syncManager;

    }

    public void switchSync(Boolean newValue) {
        if (newValue && !started) {
            startService();
        } else if (!newValue && started) {
            stopService(startedIntent);
            timer.cancel();
            started = false;
        }

    }

    public void startService() {
        if (!started) {
            startService(startedIntent);
            started = true;
        }
    }


    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        // todo: check the global background data setting
        boolean isActive = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getBoolean(MySettingsActivity.KEY_PREF_SYNC_TTR,
                                                                                                      true);
        if (isActive) {
            handleIntent();
        }
        started = isActive;
        startedIntent = intent;
        syncManager = this;
        return Service.START_NOT_STICKY;
    }

    private void handleIntent() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (notifcationSent) {
                    return;
                }
                int newPoints = 0;
                try {
                    newPoints = parser.getPoints();
                    int old = MyApplication.loginUser.getPoints();
                    //prevent from wrong parsing
                    if (old == newPoints || Math.abs(old - newPoints) > 200) {
                        return;
                    }
//                    Log.d(Constants.LOG_TAG, "new points received " + old + "!=" + newPoints);
                } catch (PlayerNotWellRegistered playerNotWellRegistered) {
                    //ignore
                    timer.cancel();
                    return;
                }

                Intent notIntent = new Intent(SyncManager.this, NewPointsActivity.class);

                newTTRPoints = newPoints;
//                Log.i(Constants.LOG_TAG, "save points " + newPoints);
                PendingIntent pIntent = PendingIntent.getActivity(SyncManager.this, 0, notIntent, 0);

                Notification n = new Notification.Builder(SyncManager.this)
                        .setContentTitle("Neue Punkte von myTischtennis!")
                        .setContentText("Du hast neue Punkte erhalten, schau sie dir an.")
                        .setSmallIcon(R.drawable.myttr)
                        .setContentIntent(pIntent)
                        .setAutoCancel(true)
                        .build();


                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                notificationManager.notify(0, n);
                notifcationSent = true;
            }
            // sek min h
//        }, 0, 1000 * 60 * 60);
        }, 0, 60 * 60);
        //every 1h
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
