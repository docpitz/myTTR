/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
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

        //todo refactor to listview
        int i = 0;
        for (final Player player : MyApplication.players) {

            View ruler = new View(this);
            ruler.setBackgroundColor(Color.WHITE);
            layout.addView(ruler, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));

            LinearLayout layoutH = new LinearLayout(ManualEntriesActivity.this);
            layoutH.setOrientation(LinearLayout.HORIZONTAL);

            TextView tvPlayer = new TextView(this);
            tvPlayer.setText(player.nameAndClub());
            tvPlayer.setWidth(300);
            tvPlayer.setHeight(150);
            layoutH.addView(tvPlayer);

//            EditText ttrText = new EditText(this);
            TextView ttrText = new TextView(this);
            ttrText.setText("" + player.getTtrPoints());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(20, 0, 0, 0);
            ttrText.setLayoutParams(layoutParams);
            layoutH.addView(ttrText);

            CheckBox checkBox = new CheckBox(this);
            checkBox.setId(i);
            layoutParams = new LinearLayout.LayoutParams(100, 100);
            layoutParams.setMargins(40, 0, 0, 0);
            checkBox.setLayoutParams(layoutParams);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    MyApplication.players.get(buttonView.getId()).setChecked(isChecked);
                }
            });
            layoutH.addView(checkBox);

            ImageButton button = new ImageButton(this);
            button.setImageResource(R.drawable.edit);
            layoutParams = new LinearLayout.LayoutParams(100, 100);
            button.setLayoutParams(layoutParams);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent target = new Intent(ManualEntriesActivity.this, PlayerDetailActivity.class);
                    MyApplication.actualPlayer = player;
                    startActivity(target);
                }
            });
            layoutH.addView(button);

            ImageButton deleteBtn = new ImageButton(this);
            deleteBtn.setImageResource(R.drawable.delete);
            layoutParams = new LinearLayout.LayoutParams(100, 100);
            deleteBtn.setLayoutParams(layoutParams);
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyApplication.players.remove(player);
                    ManualEntriesActivity.this.recreate();
                }
            });

            layoutH.addView(deleteBtn);
            layout.addView(layoutH);
            i++;

        }

        Button buttonCalc = new Button(this);
        buttonCalc.setText("Berechnen");
        buttonCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.players.size() == 0) {
                    Toast.makeText(ManualEntriesActivity.this,
                            "Bitte zun�chst einen Spieler ausw�hlen.", Toast.LENGTH_SHORT).show();
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
        });
        layout.addView(buttonCalc);
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
                if (teamAppointments == null) {
                    Toast.makeText(ManualEntriesActivity.this,
                            "Es wurde keine Termine gefunden.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    MyApplication.teamAppointments = teamAppointments;
//                    NavUtils.navigateUpFromSameTask(PlayerDetailActivity.this);
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
