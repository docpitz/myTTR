/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr.activities;

import android.app.AlertDialog;
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
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.TooManyPlayersFound;

import java.util.List;

public class SearchActivity extends BaseActivity {

    public static final String BACK_TO = "BACK_TO";
    Class goBackToClass = TTRCalculatorActivity.class;
    ClubParser clubParser = new ClubParser();
    MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
    EditText clubEdit;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ttr_player_search);
        EditText editText = (EditText) findViewById(R.id.detail_firstname);
        editText.setText("");
        editText = (EditText) findViewById(R.id.detail_lastname);
        editText.setText("");
        clubEdit = (EditText) findViewById(R.id.detail_club);
//        clubEdit.setText(MyApplication.actualPlayer.getClub());
        clubEdit.setText("");

        Intent i = getIntent();
        if (i != null && i.getExtras() != null) {
            goBackToClass = (Class) i.getExtras().getSerializable(BACK_TO);
        }

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
            String clubname = ((EditText) findViewById(R.id.detail_club)).getText().toString();
            club = ((EditText) findViewById(R.id.detail_club)).getText().toString();
            if (("".equals(firstname) || "".equals(lastname)) && "".equals(clubname)) {
                Toast.makeText(SearchActivity.this,
                        getString(R.string.error_search_required_fields),
                        Toast.LENGTH_SHORT).show();
                return;
            }
            findPlayer(club, firstname, lastname);
        }

    }

    private void findPlayer(final String club, final String firstname, final String lastname) {
        AsyncTask<String, Void, Integer> task = new BaseAsyncTask(SearchActivity.this, TTRCalculatorActivity.class) {
            int ttr = 0;

            @Override
            protected void putExtra(Intent target) {
                target.putExtra(SearchActivity.BACK_TO, goBackToClass);
            }

            @Override
            protected void callParser() throws NetworkException, LoginExpiredException {
                List<Player> p = null;
                errorMessage = null;
                try {
                    p = myTischtennisParser.findPlayer(firstname, lastname, club);
                } catch (TooManyPlayersFound tooManyPlayersFound) {
                    errorMessage = "Es wurden zu viele Spieler gefunden.";
                    ttr = 0;
                    return;
                }
//                if(true)
//                throw new RuntimeException();
                if (p != null) {
                    if (p.size() == 1) {

                        Player p1 = p.get(0);
                        if (MyApplication.actualPlayer != null) {
                            MyApplication.actualPlayer.copy(p1);
                        } else {
                            MyApplication.actualPlayer = p1;
                        }
                        MyApplication.addPlayer(p1);
                        ttr = p1.getTtrPoints();
                    } else if (p.size() > 1) {
                        targetClz = SearchResultActivity.class;
                        MyApplication.searchResult = p;
                        ttr = 1;
                    } else {
                        ttr = 0;
                        errorMessage = "Es wurden keine Spieler gefunden.";
                    }
                }
//                MyApplication.actualPlayer.setTtrPoints(ttr);
            }

            @Override
            protected boolean dataLoaded() {
                return ttr > 0;
            }

        };
        task.execute();
    }
}
