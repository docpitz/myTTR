/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.AppointmentParser;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.TTRCalculator;

public class TTRCalculatorActivity extends BaseActivity {

    TTRCalculator calculator = new TTRCalculator();

    AppointmentParser appointmentParser = new AppointmentParser();

    MyTischtennisParser myTischtennisParser = new MyTischtennisParser();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manual_entries);

        setTitle(MyApplication.getTitle());

        LinearLayout layout = (LinearLayout) findViewById(R.id.manual_entries_view_main);
        final ListView listview = (ListView) findViewById(R.id.playerlistview);

        final EntryPlayerAdapter adapter = new EntryPlayerAdapter(this,
                android.R.layout.simple_list_item_1,
                MyApplication.players);
        listview.setAdapter(adapter);

    }

    public void recalc(final View view) {
        if (MyApplication.players.size() == 0) {
            Toast.makeText(TTRCalculatorActivity.this,
                    "Bitte zun\u00E4chst einen Spieler ausw\u00E4hlen.", Toast.LENGTH_SHORT).show();
            return;
        }
        int newV = MyApplication.loginUser.getPoints();
        if (MyApplication.simPlayer != null) {
            newV = MyApplication.simPlayer.getTtrPoints();
        }
        for (Player player : MyApplication.players) {
            newV += calculator.calcPoints(newV, player.getTtrPoints(), player.isChecked());
        }
        MyApplication.result = newV;
        Intent target = new Intent(TTRCalculatorActivity.this, ResultActivity.class);
        startActivity(target);
    }

    public void newplayer(final View view) {
        Intent target = new Intent(this, TTRPlayerSearchActivity.class);
        MyApplication.actualPlayer = new Player();
        MyApplication.players.add(MyApplication.actualPlayer);
        startActivity(target);
    }

    public void nextAppointments(final View view) {
        readNextAppointments();

    }

    private void readNextAppointments() {
        AsyncTask<String, Void, Integer> task = new BaseAsyncTask(this, NextAppointmentsActivity.class) {


            @Override
            protected void callParser() throws NetworkException, LoginExpiredException {
                String name = myTischtennisParser.getNameOfOwnClub();
                if (name != null) {
                    MyApplication.teamAppointments = appointmentParser.read(name);
                } else {
                    errorMessage = "Konnte den Namen deines Vereins nicht ermitteln";
                }
            }

            @Override
            protected boolean dataLoaded() {
                return MyApplication.teamAppointments != null;
            }


        };
        task.execute();
    }
}
