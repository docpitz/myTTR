package com.jmelzer.myttr.activities;

import android.app.Activity;

import com.jmelzer.myttr.db.LoginDataBaseAdapter;

import java.util.Timer;
import java.util.TimerTask;

public class BadPeopleUtil {
    public static void handleBadPeople(Activity parent) {
        final ErrorDialog dialog = new ErrorDialog(parent,
                "Nur nette Menschen oder Vereine dürfen meine App benutzen ....",
                5000);
        dialog.show();
        final Timer timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            public void run() {
                dialog.dismiss();
                timer2.cancel(); //this will cancel the timer of the system
                new LoginDataBaseAdapter(parent).deleteAllEntries();
                parent.finishAffinity();
//                    System.exit(-1);
            }
        }, 5000); // the timer will count 5 seconds....
    }
}
