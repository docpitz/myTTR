/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.jmelzer.myttr.Club;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.ClubParser;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.TooManyPlayersFound;

import java.util.List;

public class TTRPlayerSearchActivity extends BaseActivity {

    ClubParser clubParser = new ClubParser();
    MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
    EditText clubEdit;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ttr_player_search);
//        http://developer.android.com/training/implementing-navigation/ancestral.html
        EditText editText = (EditText) findViewById(R.id.detail_firstname);
        editText.setText(MyApplication.actualPlayer.getFirstname());
        editText = (EditText) findViewById(R.id.detail_lastname);
        editText.setText(MyApplication.actualPlayer.getLastname());
        clubEdit = (EditText) findViewById(R.id.detail_club);
        clubEdit.setText(MyApplication.actualPlayer.getClub());

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void search(final View view) {
        String club = ((EditText) findViewById(R.id.detail_club)).getText().toString();
        Club verein = clubParser.getClubExact(club);
        if (verein == null) {
            final List<String> clubs = clubParser.getClubNameUnsharp(club);
            if (clubs.size() == 0) {
                AlertDialog ad = new AlertDialog.Builder(this).create();
                ad.setCancelable(false); // This blocks the 'BACK' button
                ad.setMessage("Der Verein wurde nicht gefunden. ");
                ad.setButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                ad.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setItems(clubs.toArray(new String[clubs.size()]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clubEdit.setText(clubs.get(which));
                    }
                });
                AlertDialog dlg = builder.create();
                dlg.show();
            }
        } else {
            String firstname = ((EditText) findViewById(R.id.detail_firstname)).getText().toString();
            String lastname = ((EditText) findViewById(R.id.detail_lastname)).getText().toString();
            club = ((EditText) findViewById(R.id.detail_club)).getText().toString();
            if ("".equals(firstname) || "".equals(lastname)) {
                Toast.makeText(TTRPlayerSearchActivity.this,
                               "Bitte Vornamen und nach Namen angeben!",
                               Toast.LENGTH_SHORT).show();
                return;
            }
            findPlayer(club, firstname, lastname);
        }

    }

    private void findPlayer(final String club, final String firstname, final String lastname) {
        //todo change base class
        AsyncTask<String, Void, Integer> task = new AsyncTask<String, Void, Integer>() {
            ProgressDialog progressDialog;
            long start;
            int ttr = 0;
            @Override
            protected void onPreExecute() {
                start = System.currentTimeMillis();
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(TTRPlayerSearchActivity.this);
                    progressDialog.setMessage("Suche Spieler, bitte warten...");
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
                if (ttr == 0) {
                    Toast.makeText(TTRPlayerSearchActivity.this,
                                   "Es wurde kein Spieler mit dem Namen gefunden.",
                                   Toast.LENGTH_SHORT).show();
                }
                else if (ttr == -1) {
                    Toast.makeText(TTRPlayerSearchActivity.this,
                                   "Es wurden mehrere Spieler gefunden. Bitte Verein angeben",
                                   Toast.LENGTH_SHORT).show();
                } else {
                    MyApplication.actualPlayer.setTtrPoints(ttr);
                    Intent target = new Intent(TTRPlayerSearchActivity.this, TTRCalculatorActivity.class);
                    startActivity(target);
//                    NavUtils.navigateUpFromSameTask(TTRPlayerSearchActivity.this);
                }
            }

            @Override
            protected Integer doInBackground(String... params) {

                List<Player> p  = null;
                try {
                    p = myTischtennisParser.findPlayer(firstname, lastname, club);
                } catch (TooManyPlayersFound tooManyPlayersFound) {
                    ttr = -1;
                    return null;
                } catch (NetworkException e) {
                    //todo
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                if (p.size() > 0) {
                    Player p1 = p.get(0);
                    MyApplication.actualPlayer.copy(p1);
                    ttr = p1.getTtrPoints();
                } else {
                    ttr = 0;
                }
                return null;
            }
        };
        task.execute();
    }
}
