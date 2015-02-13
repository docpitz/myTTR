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

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.activities.NewPointsActivity;

import java.util.Timer;
import java.util.TimerTask;

public class SyncManager extends Service {

    static Intent intent;
    static boolean started = false;
    private static Timer timer = new Timer();
    static public boolean notifcationSent = false;
    MyTischtennisParser parser = new MyTischtennisParser();
    public static int newTTRPoints;

    public void switchSync(Boolean newValue) {
        init();
        if (newValue) {
            startService();
        } else {
            this.stopService(intent);
        }

    }

    public void startService() {
        init();
        if (!started) {
            startService(intent);
            started = true;
        }
    }

    private void init() {
        if (intent == null) {
            //FIXME
//            java.lang.NullPointerException
//            at android.content.ContextWrapper.getPackageName(ContextWrapper.java:135)
//            at android.content.ComponentName.<init>(ComponentName.java:75)
//            at android.content.Intent.<init>(Intent.java:3491)
//            at com.jmelzer.myttr.logic.SyncManager.init(SyncManager.java:60)
            intent = new Intent(this, SyncManager.class);
        }
    }

    /**
     * checks wether the logged in user have new points at mytischtennis.de since the last login date.
     * maybe better then the last update in db?
     *
     * @return true/false
     */
    boolean hasNewPoints() throws PlayerNotWellRegistered {


        return false;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (notifcationSent) {
                    return;
                }
                int newPoints = 0;
                try {
                    newPoints = parser.getPoints();
                    int old = MyApplication.getPoints();
                    //prevent from wrong parsing
                    if (old == newPoints || Math.abs(old-newPoints) > 200) {
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
        }, 0, 1000*60*60);
        //every 1h
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
