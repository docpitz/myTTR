package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;

import java.util.List;

/**
 * Showing own team players
 * User: jmelzer
 */
public class SelectTeamPlayerActivity extends BaseActivity {

    @Override
    protected boolean checkIfNeccessryDataIsAvaible() {
        return MyApplication.myTeamPlayers != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.selectplayer);

        final ListView listview = (ListView) findViewById(R.id.playerlistview);
        final MyTeamPlayerAdapter adapter = new MyTeamPlayerAdapter(this,
                android.R.layout.simple_list_item_1,
                MyApplication.myTeamPlayers);
        listview.setAdapter(adapter);
        Button button = new Button(this);
        button.setText(R.string.select);
        MyApplication.simPlayer = null;

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                view.setSelected(true);
                if (position > -1 && position < MyApplication.myTeamPlayers.size()) {
                    MyApplication.simPlayer = MyApplication.myTeamPlayers.get(position);
                    new SimPlayerAsyncTask(SelectTeamPlayerActivity.this, HomeActivity.class, MyApplication.simPlayer).execute();
                }
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

    class MyTeamPlayerAdapter extends ArrayAdapter<Player> {

        public MyTeamPlayerAdapter(Context context, int resource, List<Player> players) {
            super(context, resource, players);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.myplayerrow, parent, false);
            Player player = getItem(position);


            TextView textView = (TextView) rowView.findViewById(R.id.firstname);
            textView.setText(player.getFirstname());
            textView = (TextView) rowView.findViewById(R.id.lastname);
            textView.setText(player.getLastname());
            return rowView;
        }
    }
}
