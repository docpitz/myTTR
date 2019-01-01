/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.AppointmentParser;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.TTRCalculator;

import java.util.ArrayList;
import java.util.List;

public class TTRCalculatorActivity extends BaseActivity {

    TTRCalculator calculator = new TTRCalculator();

    AppointmentParser appointmentParser = new AppointmentParser();

    MyTischtennisParser myTischtennisParser = new MyTischtennisParser();

    @Override
    protected boolean checkIfNeccessryDataIsAvaible() {
        return MyApplication.getTtrCalcPlayer() != null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.ttr_calc);

        final ListView listview = findViewById(R.id.playerlistview);

        final EntryPlayerAdapter adapter = new EntryPlayerAdapter(this,
                android.R.layout.simple_list_item_1,
                MyApplication.getTtrCalcPlayer());
        listview.setAdapter(adapter);

        if (MyApplication.getTtrCalcPlayer() == null || MyApplication.getTtrCalcPlayer().isEmpty()) {
            TextView textView = findViewById(R.id.txt_player_list);
            textView.setText(R.string.txt_player_list_empty);
        } else {
            TextView textView = findViewById(R.id.txt_player_list);
            textView.setText(R.string.txt_player_list);

        }
    }


    public void recalc(final View view) {
        if (MyApplication.getTtrCalcPlayer().size() == 0) {
            Toast.makeText(TTRCalculatorActivity.this,
                    getString(R.string.select_player_first), Toast.LENGTH_SHORT).show();
            return;
        }
        int actualPoints = MyApplication.getPoints();
        if (MyApplication.simPlayer != null) {
            actualPoints = MyApplication.simPlayer.getTtrPoints();
        }
        List<TTRCalculator.Game> list = new ArrayList<>();

        for (Player player : MyApplication.getTtrCalcPlayer()) {
            list.add(new TTRCalculator.Game(player.getTtrPoints(), player.isChecked()));
        }
        actualPoints += calculator.calcPoints(actualPoints, list, MyApplication.getAk());
        MyApplication.result = actualPoints;
        Intent target = new Intent(TTRCalculatorActivity.this, ResultActivity.class);
        startActivity(target);
    }

    public void nextAppointments(MenuItem item) {
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
                    errorMessage = "Konnte den Namen deines Vereins nicht ermitteln. Wahrscheinlich ein Fehler bei mytischtennis.de." +
                            "Du kannst ihn aber in den Einstellungen selbst eingeben.";
                    Intent target = new Intent(TTRCalculatorActivity.this, EnterClubNameActivity.class);
                    startActivity(target);
                }
            }

            @Override
            protected boolean dataLoaded() {
                return MyApplication.teamAppointments != null;
            }


        };
        task.execute();
    }

    public void newplayer(MenuItem item) {
        MyApplication.actualPlayer = new Player();
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(SearchActivity.INTENT_LIGA_PLAYER, false);
        intent.putExtra(SearchActivity.BACK_TO, TTRCalculatorActivity.class);
        intent.putExtra(SearchActivity.TARGET, TTRCalculatorActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.ttr_actions, menu);
        return true;
    }
}
