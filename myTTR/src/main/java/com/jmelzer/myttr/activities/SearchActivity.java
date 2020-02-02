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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.jmelzer.myttr.Club;
import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.ClubParser;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.model.Favorite;
import com.jmelzer.myttr.model.SearchPlayer;

import java.util.List;

public class SearchActivity extends BaseActivity {

    public static final String BACK_TO = "BACK_TO";
    public static final String TARGET = "TARGET";
    public static final String INTENT_SP = "INTENT_SP";
    public static final String INTENT_MULTISELECT = "INTENT_MS";
    public static final String INTENT_LIGA_PLAYER = "lp";
    Class goBackToClass = TTRCalculatorActivity.class;
    private Class targetClz = EventsActivity.class;
    ClubParser clubParser = new ClubParser();
    MyTischtennisParser myTischtennisParser = new MyTischtennisParser();
    EditText clubEdit;
    boolean multiSelect = false;
    SearchPlayer searchPlayer = new SearchPlayer();
    FavoriteManager favoriteManager;

    @Override
    protected boolean checkIfNeccessryDataIsAvailable() {
        return true;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        multiSelect = false;
        favoriteManager = new FavoriteManager(this, getApplicationContext());
        setContentView(R.layout.search);
        Intent i = getIntent();


        clubEdit = findViewById(R.id.detail_club);

        if (i != null && i.getExtras() != null ) {

            if (i.getExtras().getSerializable(BACK_TO) != null) {
                goBackToClass = (Class) i.getExtras().getSerializable(BACK_TO);
            }
            if (i.getExtras().getSerializable(INTENT_SP) != null) {
                searchPlayer = (SearchPlayer) i.getExtras().getSerializable(INTENT_SP);
            }
            if (i.getExtras().getSerializable(TARGET) != null) {
                targetClz = (Class) i.getExtras().getSerializable(TARGET);
            }
        }
//        clubEdit.setText("TTG St. Augustin");

        Spinner dropdown = findViewById(R.id.spinner);
        String[] items = new String[]{"Beide", "Männlich", "Weiblich"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        adapter.setDropDownViewResource(R.layout.liga_home_spinner_item);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(genderListener());

        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (i != null && i.getExtras() != null ) {
            multiSelect = i.getExtras().getBoolean(INTENT_MULTISELECT, false);
        }
        if (i != null && i.getExtras() != null && i.getExtras().getBoolean(INTENT_LIGA_PLAYER, false)) {
            EditText firstNameText = findViewById(R.id.detail_firstname);
            EditText lastNameText = findViewById(R.id.detail_lastname);
            String n = MyApplication.getSelectedLigaSpieler().getName();
            int idx = n.indexOf(',');
            if (idx > -1) {
                firstNameText.setText(n.substring(idx + 1).trim());
                lastNameText.setText(n.substring(0, idx).trim());
                clubEdit.setText(MyApplication.getSelectedLigaSpieler().getClubName());
                search(null);
            }
        }

        EditText searchField = findViewById(R.id.detail_lastname);
        configureSearchField(searchField);
        EditText clubField = findViewById(R.id.detail_club);
        configureSearchField(clubField);

        prefillFields(clubField);
    }

    private void prefillFields(EditText clubField) {
        EditText editText = findViewById(R.id.yearfrom);
        if (searchPlayer.getYearFrom() > 0)
            editText.setText(searchPlayer.getYearFrom());
        editText = findViewById(R.id.yearto);
        if (searchPlayer.getYearTo() > 0)
            editText.setText(searchPlayer.getYearTo());

        editText = findViewById(R.id.ttrfrom);
        if (searchPlayer.getTtrFrom() > 0)
            editText.setText("" + searchPlayer.getTtrFrom());
        editText = findViewById(R.id.ttrto);
        if (searchPlayer.getTtrTo() > 0)
            editText.setText("" + searchPlayer.getTtrTo());
        if (searchPlayer.getClub() != null) {
            clubField.setText(searchPlayer.getClubName());
        }
    }

    @NonNull
    private AdapterView.OnItemSelectedListener genderListener() {
        return new AdapterView.OnItemSelectedListener() {
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
        };
    }

    private void configureSearchField(EditText searchField) {
        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search(null);
                    return true;
                }
                return false;
            }
        });
    }

    public void search(final View view) {
        String club = ((EditText) findViewById(R.id.detail_club)).getText().toString();
        Club verein = clubParser.getClubExact(club);
        if (!club.isEmpty() && verein == null) {
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
        AsyncTask<String, Void, Integer> task = new SearchAsyncTask(this, goBackToClass, searchPlayer, targetClz, multiSelect);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_actions, menu);
        SubMenu subm = menu.getItem(0).getSubMenu(); // get my MenuItem with placeholder submenu
        subm.clear(); // delete place holder
        List<Favorite> list = favoriteManager.getFavorites(SearchPlayer.class);
        int id = 0;
        for (Favorite favorite : list) {
            subm.add(0, id++, Menu.NONE, favorite.getName());
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        favoriteManager.startFavorite(item.getItemId(), SearchPlayer.class);
        return super.onOptionsItemSelected(item);
    }

}
