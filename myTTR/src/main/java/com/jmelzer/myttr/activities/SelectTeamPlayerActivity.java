package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.NiceGuysException;
import com.jmelzer.myttr.logic.TooManyPlayersFound;
import com.jmelzer.myttr.logic.ValidationException;

import java.util.List;

/**
 * Showing own team players
 * User: jmelzer
 */
public class SelectTeamPlayerActivity extends BaseActivity {

    @Override
    protected boolean checkIfNeccessryDataIsAvailable() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == 1) {
            Player p = (Player) data.getSerializableExtra("PLAYER");
            Toast.makeText(this, p.getFullName(), Toast.LENGTH_LONG).show();
            MyApplication.simPlayer = p;
            new SimPlayerAsyncTask(SelectTeamPlayerActivity.this, HomeActivity.class, MyApplication.simPlayer).execute();
        }
    }

    public void cluplist(View view) {
        AsyncTask<String, Void, Integer> task = new BaseAsyncTask(this, SearchResultActivity.class) {

            @Override
            protected void callParser() throws NetworkException, LoginExpiredException, ValidationException {
                MyApplication.searchResult = null;
                try {
                    MyApplication.searchResult = new MyTischtennisParser().findPlayer(null, null,
                            MyApplication.myTeamPlayers.get(0).getClub());
                } catch (TooManyPlayersFound tooManyPlayersFound) {
                    //ok
                }
            }

            @Override
            protected boolean dataLoaded() {
                return MyApplication.searchResult != null;
            }

            @Override
            protected void putExtra(Intent target) {
                target.putExtra(SearchActivity.BACK_TO, SelectTeamPlayerActivity.class);
            }
        };
        task.execute();
    }

    public void search(View view) {
        Intent target = new Intent(this, SearchActivity.class);
        target.putExtra(SearchActivity.BACK_TO, SelectTeamPlayerActivity.class);
        startActivityForResult(target, 1);
    }

    private class SimPlayerAsyncTask extends BaseAsyncTask {
        Player selectedPlayer;

        public SimPlayerAsyncTask(Activity parent, Class targetClz, Player p) {
            super(parent, targetClz);
            selectedPlayer = p;
        }

        @Override
        protected void callParser() throws NetworkException, LoginExpiredException, NiceGuysException {
            if (MyApplication.clubPlayers == null) {
                MyApplication.clubPlayers = new MyTischtennisParser().getClubList(true);
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
