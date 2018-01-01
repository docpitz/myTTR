package com.jmelzer.myttr.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
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
import com.jmelzer.myttr.db.FavoriteDataBaseAdapter;
import com.jmelzer.myttr.model.SearchPlayer;
import com.jmelzer.myttr.model.Verein;

import org.json.JSONException;

import java.util.List;

/**
 * Showing result of a player search
 * User: jmelzer
 */
public class SearchResultActivity extends BaseActivity {
    Class goBackToClass;
    private SearchPlayer searchPlayer;
    FavoriteManager favoriteManager;

    @Override
    protected boolean checkIfNeccessryDataIsAvaible() {
        return MyApplication.searchResult != null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.search_result);
        favoriteManager = new FavoriteManager(this, getApplicationContext());

        final ListView listview = (ListView) findViewById(R.id.playerlistview);
        final SearchResultAdapter adapter = new SearchResultAdapter(this,
                android.R.layout.simple_list_item_1,
                MyApplication.searchResult);
        listview.setAdapter(adapter);
        Button button = new Button(this);
        button.setText(R.string.select);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent i = getIntent();
        goBackToClass = TTRCalculatorActivity.class;
        if (i != null && i.getExtras() != null) {
            goBackToClass = (Class) i.getExtras().getSerializable(SearchActivity.BACK_TO);
            searchPlayer = (SearchPlayer) i.getExtras().getSerializable(SearchActivity.INTENT_SP);
        }

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                view.setSelected(true);
//                /TODO NOT GENERIC!!!
                Player p = MyApplication.searchResult.get(position);
                if (goBackToClass.equals(TTRCalculatorActivity.class)) {
                    if (position > -1 && position < MyApplication.searchResult.size()) {
                        if (MyApplication.actualPlayer != null) {
                            MyApplication.actualPlayer.copy(p);
                        } else {
                            MyApplication.actualPlayer = p;
                        }
                        MyApplication.addTTRCalcPlayer(MyApplication.actualPlayer);
                        Intent target = new Intent(SearchResultActivity.this, goBackToClass);
                        finish();
                        SearchResultActivity.this.startActivity(target);
                    }
                } else if (goBackToClass.equals(SelectTeamPlayerActivity.class)) {
                    Intent intent = new Intent();
                    intent.putExtra("PLAYER", p);
                    setResult(1, intent);
                    finish();
                } else {


                    new EventsAsyncTask(SearchResultActivity.this, EventsActivity.class, p).execute();
                }
            }
        });

    }

    public void filter(MenuItem item) {
        Intent intent = new Intent(this, SearchFilterActivity.class);
        intent.putExtra(SearchActivity.INTENT_SP, searchPlayer);
        startActivity(intent);
    }

    class SearchResultAdapter extends ArrayAdapter<Player> {

        public SearchResultAdapter(Context context, int resource, List<Player> players) {
            super(context, resource, players);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.search_result_row, parent, false);
            Player player = getItem(position);
            if (player != null) {
                TextView textView = rowView.findViewById(R.id.firstname);
                textView.setText(player.getFirstname());
                textView = rowView.findViewById(R.id.lastname);
                textView.setText(player.getLastname());
                textView = rowView.findViewById(R.id.club);
                textView.setText(player.getClub());
                textView = rowView.findViewById(R.id.ttr);
                textView.setText("" + player.getTtrPoints());
            }
            return rowView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.searchresult_actions, menu);
        return true;
    }

    public void favorite(MenuItem item) {
        favoriteManager.favorite(searchPlayer);
    }
}
