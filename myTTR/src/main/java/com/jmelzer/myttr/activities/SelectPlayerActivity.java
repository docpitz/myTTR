package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.jmelzer.myttr.Game;
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
public class SelectPlayerActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectplayer);

        final ListView listview = (ListView) findViewById(R.id.playerlistview);
        final MyTeamPlayerAdapter adapter = new MyTeamPlayerAdapter(this,
                android.R.layout.simple_list_item_1,
                MyApplication.myTeamPlayers);
        listview.setAdapter(adapter);
        Button button = new Button(this);
        button.setText(R.string.select);
        MyApplication.simPlayer = null;

        getActionBar().setDisplayHomeAsUpEnabled(true);
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                view.setSelected(true);
                if (position > -1 && position < MyApplication.myTeamPlayers.size()) {
                    MyApplication.simPlayer = MyApplication.myTeamPlayers.get(position);
                    new SimPlayerAsyncTask(SelectPlayerActivity.this, HomeActivity.class, MyApplication.simPlayer).execute();
                    return true;
                }
                return false;
            }
        });

    }

    private class SimPlayerAsyncTask extends BaseAsyncTask {
        Player selectedPlayer;

        public SimPlayerAsyncTask(Activity parent, Class targetClz, Player p) {
            super(parent, targetClz);
            selectedPlayer = p;
        }

        @Override
        protected void callParser() throws NetworkException, LoginExpiredException {
            if (MyApplication.clubPlayers == null) {
                MyApplication.clubPlayers = new MyTischtennisParser().getClubList();
            }
            for (Player clubPlayer : MyApplication.clubPlayers) {
                if (clubPlayer.getFirstname().equals(selectedPlayer.getFirstname()) &&
                        clubPlayer.getLastname().equals(selectedPlayer.getLastname())) {

                    selectedPlayer.setTtrPoints(clubPlayer.getTtrPoints());
                }
            }
        }


        @Override
        protected boolean dataLoaded() {
            return MyApplication.clubPlayers != null;
        }
    }
}
