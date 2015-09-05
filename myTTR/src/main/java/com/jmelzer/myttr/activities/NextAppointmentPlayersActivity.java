package com.jmelzer.myttr.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.TooManyPlayersFound;

/**
 * Shows the player for the next appointment,
 * User: jmelzer
 */
public class NextAppointmentPlayersActivity extends BaseActivity {

    MyTischtennisParser myTischtennisParser = new MyTischtennisParser();

    @Override
    protected boolean checkIfNeccessryDataIsAvaible() {
        return MyApplication.foreignTeamPlayers != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.nextappointmentplayer);

        final ListView listview = (ListView) findViewById(R.id.nextappointmentplayerlistview);
        final TeamPlayerAdapter adapter = new TeamPlayerAdapter(this,
                android.R.layout.simple_list_item_1,
                MyApplication.foreignTeamPlayers);
        listview.setAdapter(adapter);
        Button button = new Button(this);
        button.setText(R.string.select);

        getActionBar().setDisplayHomeAsUpEnabled(true);


//        listview.addFooterView(button);
    }

    public void select(final View view) {
        findPlayer();
    }

    public void loadPlayerFromClub(final View view) {
        AsyncTask<String, Void, Integer> task = new BaseAsyncTask(this, SearchResultActivity.class) {

            @Override
            protected void callParser() throws NetworkException, LoginExpiredException {
                MyApplication.searchResult = null;
                try {
                    if (MyApplication.foreignTeamPlayers.size() > 0) {
                        MyApplication.searchResult = myTischtennisParser.findPlayer(null, null,
                                MyApplication.foreignTeamPlayers.get(0).getClub());
                    }
                } catch (TooManyPlayersFound tooManyPlayersFound) {
                    //ok
                }
            }

            @Override
            protected boolean dataLoaded() {
                return MyApplication.searchResult != null;
            }


        };
        task.execute();
    }

    private void findPlayer() {
        AsyncTask<String, Void, Integer> task = new BaseAsyncTask(this, TTRCalculatorActivity.class) {
            boolean completed = false;

            @Override
            protected void callParser() throws NetworkException, LoginExpiredException {
                Player p = null;
                for (Player teamPlayer : MyApplication.foreignTeamPlayers) {
                    if (!teamPlayer.isChecked()) {
                        continue;
                    }
                    p = myTischtennisParser.completePlayerWithTTR(teamPlayer);
                    if (p != null) {
                        teamPlayer.setTtrPoints(p.getTtrPoints());
                        MyApplication.addPlayer(teamPlayer);
                        teamPlayer.setChecked(false);
                    }
                }
                completed = true;
            }

            @Override
            protected boolean dataLoaded() {
                return completed;
            }


        };
        task.execute();
    }
}
