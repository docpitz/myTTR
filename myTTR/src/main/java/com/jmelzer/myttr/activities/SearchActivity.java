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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.jmelzer.myttr.Club;
import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.ClubParser;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.TooManyPlayersFound;
import com.jmelzer.myttr.model.SearchPlayer;

import java.util.List;

public class SearchActivity extends BaseActivity {

    public static final String BACK_TO = "BACK_TO";
    public static final String INTENT_SP = "INTENT_SP";
    public static final String INTENT_LIGA_PLAYER = "lp";
    Class goBackToClass = TTRCalculatorActivity.class;
    ClubParser clubParser = new ClubParser();
    MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
    EditText clubEdit;
    SearchPlayer searchPlayer = new SearchPlayer();

    @Override
    protected boolean checkIfNeccessryDataIsAvaible() {
        return true;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.search);
        Intent i = getIntent();


        clubEdit = findViewById(R.id.detail_club);

        if (i != null && i.getExtras() != null && i.getExtras().getSerializable(BACK_TO) != null) {
            goBackToClass = (Class) i.getExtras().getSerializable(BACK_TO);
        }
//        clubEdit.setText("TTG St. Augustin");

        Spinner dropdown = findViewById(R.id.spinner);
        String[] items = new String[]{"Beide", "Männlich", "Weiblich"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        adapter.setDropDownViewResource(R.layout.liga_home_spinner_item);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        searchPlayer.setGender(null);
                        break;
                    case 1:
                        searchPlayer.setGender("male");
                        break;
                    case 2:
                        searchPlayer.setGender("female");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                searchPlayer.setGender(null);
            }
        });

        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (i != null && i.getExtras() != null && i.getExtras().getBoolean(INTENT_LIGA_PLAYER, false)) {
            EditText firstNameText =  findViewById(R.id.detail_firstname);
            EditText lastNameText = findViewById(R.id.detail_lastname);
            String n = MyApplication.selectedLigaSpieler.getName();
            int idx = n.indexOf(',');
            if (idx > -1) {
                firstNameText.setText(n.substring(idx+1).trim());
                lastNameText.setText(n.substring(0, idx).trim());
                clubEdit.setText(MyApplication.selectedLigaSpieler.getClubName());
                search(null);
            }
        }

        EditText editText = findViewById(R.id.yearfrom);
        if (searchPlayer.getYearFrom() > 0)
            editText.setText(searchPlayer.getYearFrom());
        editText = findViewById(R.id.yearto);
        if (searchPlayer.getYearTo() > 0)
            editText.setText(searchPlayer.getYearTo());
        editText = findViewById(R.id.ttrfrom);
        if (searchPlayer.getTtrFrom() > 0)
            editText.setText(searchPlayer.getTtrFrom());
        editText = findViewById(R.id.ttrto);
        if (searchPlayer.getTtrTo() > 0)
            editText.setText(searchPlayer.getTtrTo());
    }

    public void search(final View view) {
        String club = ((EditText) findViewById(R.id.detail_club)).getText().toString();
        Club verein = clubParser.getClubExact(club);
        if (verein == null) {
            final List<String> clubs = clubParser.getClubNameUnsharp(club, 0.3f);
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
//            if (("".equals(firstname) || "".equals(lastname)) && "".equals(clubname)) {
//                Toast.makeText(SearchActivity.this,
//                        getString(R.string.error_search_required_fields),
//                        Toast.LENGTH_SHORT).show();
//                return;
//            }
            searchPlayer.setFirstname(firstname);
            searchPlayer.setClub(verein);
            searchPlayer.setLastname(lastname);

            EditText from = findViewById(R.id.yearfrom);
            if (!from.getText().toString().isEmpty()) {
                searchPlayer.setYearFrom(Integer.parseInt(from.getText().toString()));
            }
            EditText to = findViewById(R.id.yearto);
            if (!to.getText().toString().isEmpty()) {
                searchPlayer.setYearTo(Integer.parseInt(to.getText().toString()));
            }

            from = findViewById(R.id.ttrfrom);
            if (!from.getText().toString().isEmpty()) {
                searchPlayer.setTtrFrom(Integer.parseInt(from.getText().toString()));
            }

            to = findViewById(R.id.ttrto);
            if (!to.getText().toString().isEmpty()) {
                searchPlayer.setTtrTo(Integer.parseInt(to.getText().toString()));
            }

            findPlayer();
        }

    }

    private void findPlayer() {


        AsyncTask<String, Void, Integer> task = new BaseAsyncTask(SearchActivity.this, goBackToClass) {
            Player foundSinglePlayer;

            @Override
            protected void putExtra(Intent target) {
                target.putExtra(SearchActivity.BACK_TO, goBackToClass);
                target.putExtra(SearchActivity.INTENT_SP, searchPlayer);
            }

            @Override
            protected void callParser() throws NetworkException, LoginExpiredException {
                List<Player> p = null;
                errorMessage = null;
                try {
                    p = myTischtennisParser.findPlayer(searchPlayer);
                } catch (TooManyPlayersFound tooManyPlayersFound) {
                    errorMessage = "Es wurden zu viele Spieler gefunden.";
                    return;
                }
                if (p != null) {

                    if (p.size() == 1) {

                        Player p1 = p.get(0);
                        if (MyApplication.actualPlayer != null) {
                            MyApplication.actualPlayer.copy(p1);
                        } else {
                            MyApplication.actualPlayer = p1;
                        }
                        MyApplication.addTTRCalcPlayer(p1);
                        foundSinglePlayer = p1;

                    } else if (p.size() > 1) {
                        targetClz = SearchResultActivity.class;
                        MyApplication.searchResult = p;
                    } else {
                        errorMessage = "Es wurden keine Spieler gefunden.";
                    }
                }
            }

            @Override
            protected void startNextActivity() {
                if (goBackToClass.equals(EventsActivity.class) && foundSinglePlayer != null) {
                    new EventsAsyncTask(parent, EventsActivity.class, foundSinglePlayer).execute();
                } else {
                    super.startNextActivity();
                }
            }

            @Override
            protected boolean dataLoaded() {
                return errorMessage == null;
            }

        };
        task.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(Constants.LOG_TAG, "resultCode=" + resultCode);
        if (resultCode == 1) {
            setResult(1, data);
            finish();
        }
    }

    public void reset(View view) {

        EditText editText = findViewById(R.id.yearfrom);
        editText.setText("");
        editText = findViewById(R.id.yearto);
        editText.setText("");
        editText = findViewById(R.id.detail_lastname);
        editText.setText("");
        editText = findViewById(R.id.detail_firstname);
        editText.setText("");
        editText = findViewById(R.id.ttrfrom);
        editText.setText("");
        editText = findViewById(R.id.ttrto);
        editText.setText("");
        editText = findViewById(R.id.detail_club);
        editText.setText("");
        Spinner spinner = findViewById(R.id.spinner);
        spinner.setSelection(0);
        searchPlayer = new SearchPlayer();
    }
}
