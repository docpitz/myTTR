package com.jmelzer.myttr.activities;

import android.app.ProgressDialog;
import android.content.Intent;
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

/**
 * User: jmelzer
 * Date: 22.03.14
 * Time: 13:06
 */
public class NextAppointmentPlayersActivity extends BaseActivity {
    MyTischtennisParser myTischtennisParser = new MyTischtennisParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    private void findPlayer() {
        //todo
        AsyncTask<String, Void, Integer> task = new AsyncTask<String, Void, Integer>() {
            ProgressDialog progressDialog;

            String errorMessage;
            long start;

            @Override
            protected void onPreExecute() {
                start = System.currentTimeMillis();
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(NextAppointmentPlayersActivity.this);
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
//                NavUtils.navigateUpFromSameTask(NextAppointmentPlayersActivity.this);
                Intent target = new Intent(NextAppointmentPlayersActivity.this, TTRCalculatorActivity.class);
                startActivity(target);
            }

            @Override
            protected Integer doInBackground(String... params) {
                errorMessage = null;
                for (Player teamPlayer : MyApplication.foreignTeamPlayers) {

                    if (!teamPlayer.isChecked()) {
                        continue;
                    }
                    Player p = null;
                    try {
                        p = myTischtennisParser.completePlayerWithTTR(teamPlayer);
                    } catch (LoginExpiredException e) {
                        return null;
                    } catch (NetworkException e) {
                        break;
                    }
                    if (p != null) {
                        teamPlayer.setTtrPoints(p.getTtrPoints());
                        MyApplication.players.add(teamPlayer);
                        teamPlayer.setChecked(false);
                    }
                }
                return null;
            }
        };
        task.execute();
    }
}
