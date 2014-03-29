/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.TeamAppointment;
import com.jmelzer.myttr.logic.AppointmentParser;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.TTRCalculator;

import java.util.List;

public class ManualEntriesActivity extends Activity {

    TTRCalculator calculator = new TTRCalculator();

    AppointmentParser appointmentParser = new AppointmentParser();

    MyTischtennisParser myTischtennisParser = new MyTischtennisParser();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manual_entries);

        setTitle(MyApplication.getTitle());

        LinearLayout layout = (LinearLayout) findViewById(R.id.manual_entries_view);
        final ListView listview = (ListView) findViewById(R.id.playerlistview);

        final EntryPlayerAdapter adapter = new EntryPlayerAdapter(this,
                android.R.layout.simple_list_item_1,
                MyApplication.players);
        listview.setAdapter(adapter);

    }

    public void recalc(final View view) {
        if (MyApplication.players.size() == 0) {
            Toast.makeText(ManualEntriesActivity.this,
                    "Bitte zun\u00E4chst einen Spieler ausw\u00E4hlen.", Toast.LENGTH_SHORT).show();
            return;
        }
        int newV = MyApplication.loginUser.getPoints();
        for (Player player : MyApplication.players) {
            newV += calculator.calcPoints(newV, player.getTtrPoints(), player.isChecked());
        }
        MyApplication.result = newV;
        Intent target = new Intent(ManualEntriesActivity.this, ResultActivity.class);
        startActivity(target);
    }

    public void newplayer(final View view) {
        Intent target = new Intent(this, PlayerDetailActivity.class);
        MyApplication.actualPlayer = new Player();
        MyApplication.players.add(MyApplication.actualPlayer);
        startActivity(target);
    }

    public void nextAppointments(final View view) {
        readNextAppointments();

    }

    private void readNextAppointments() {
        AsyncTask<String, Void, Integer> task = new AsyncTask<String, Void, Integer>() {
            ProgressDialog progressDialog;

            long start;

            List<TeamAppointment> teamAppointments = null;

            @Override
            protected void onPreExecute() {
                start = System.currentTimeMillis();
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(ManualEntriesActivity.this);
                    progressDialog.setMessage("Suche nächste Spiele, bitte warten...");
                    progressDialog.setIndeterminate(false);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            }

            @Override
            protected void onPostExecute(Integer integer) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (teamAppointments == null || teamAppointments.size() == 0) {
                    Toast.makeText(ManualEntriesActivity.this,
                            "Es wurde keine Termine gefunden.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    MyApplication.teamAppointments = teamAppointments;
                    Intent target = new Intent(ManualEntriesActivity.this, NextAppointmentsActivity.class);
                    startActivity(target);
                }
            }

            @Override
            protected Integer doInBackground(String... params) {

                Player p = null;

                String name = myTischtennisParser.getNameOfOwnClub();
                teamAppointments = appointmentParser.read(name);
                return null;
            }
        };
        task.execute();
    }
}
