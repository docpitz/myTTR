/* 
* Copyright (C) allesklar.com AG
* All rights reserved.
*
* Author: juergi
* Date: 29.12.13 
*
*/


package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.EditText;
import com.jmelzer.myttr.Club;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.parser.ClubParser;
import com.jmelzer.myttr.parser.LoginManager;
import com.jmelzer.myttr.parser.TTRPointParser;

import java.util.List;

public class PlayerDetailActivity extends Activity {

    ClubParser clubParser = new ClubParser();
    TTRPointParser ttrPointParser = new TTRPointParser();
    EditText clubEdit;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_detail);
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
            findPlayer(club, firstname, lastname);
        }

    }

    private void findPlayer(final String club, final String firstname, final String lastname) {
        AsyncTask<String, Void, Integer> task = new AsyncTask<String, Void, Integer>() {
            ProgressDialog progressDialog;
            long start;
            int ttr = 0;
            @Override
            protected void onPreExecute() {
                start = System.currentTimeMillis();
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(PlayerDetailActivity.this);
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
                System.out.println("time = " + (System.currentTimeMillis() - start) + "ms" );
                if (ttr == 0) {
                    AlertDialog ad = new AlertDialog.Builder(PlayerDetailActivity.this).create();
                    ad.setCancelable(false); // This blocks the 'BACK' button
                    ad.setMessage("TTR punkte = " + ttr);
                    ad.setButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    ad.show();
                } else {
                    MyApplication.actualPlayer.setTtrPoints(ttr);
                    System.out.println("ttr = " + ttr);
//                    navigateUpTo()
                    NavUtils.navigateUpFromSameTask(PlayerDetailActivity.this);
                }
            }

            @Override
            protected Integer doInBackground(String... params) {

                Player p  = ttrPointParser.findPlayer(firstname, lastname, club);
                MyApplication.actualPlayer.copy(p);
                if (p != null) {
                    ttr = p.getTtrPoints();
                }
                return null;
            }
        };
        task.execute();
    }
}
