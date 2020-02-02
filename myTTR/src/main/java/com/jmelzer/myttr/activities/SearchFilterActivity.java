package com.jmelzer.myttr.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.TooManyPlayersFound;
import com.jmelzer.myttr.logic.ValidationException;
import com.jmelzer.myttr.model.SearchPlayer;

import java.util.List;

/**
 * Created by J. Melzer on 28.09.2017.
 */

public class SearchFilterActivity extends BaseActivity {
    private SearchPlayer searchPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.search_filter);
        setTitle("Filter");

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

        Intent i = getIntent();
        if (i != null && i.getExtras() != null) {
            searchPlayer = (SearchPlayer) i.getExtras().getSerializable(SearchActivity.INTENT_SP);
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

    @Override
    protected boolean checkIfNeccessryDataIsAvailable() {
        return true;
    }

    public void doFilter(View view) {

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

        AsyncTask<String, Void, Integer> task = new BaseAsyncTask(this,
                SearchResultActivity.class) {


            @Override
            protected void callParser() throws NetworkException, LoginExpiredException, ValidationException {
                List<Player> p;
                errorMessage = null;
                try {
                    p = new MyTischtennisParser().findPlayer(searchPlayer);
                } catch (TooManyPlayersFound tooManyPlayersFound) {
                    errorMessage = "Es wurden zu viele Spieler gefunden.";
                    return;
                }
                if (p != null) {

                    if (p.size() > 1) {
                        MyApplication.searchResult = p;
                    } else {
                        errorMessage = "Es wurden keine Spieler gefunden.";
                    }
                }
            }

            @Override
            protected void putExtra(Intent target) {
                target.putExtra(SearchActivity.INTENT_SP, searchPlayer);

            }

            @Override
            protected boolean dataLoaded() {
                return errorMessage == null;
            }

        };
        task.execute();
    }
}
