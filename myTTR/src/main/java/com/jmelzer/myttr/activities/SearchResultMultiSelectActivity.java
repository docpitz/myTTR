package com.jmelzer.myttr.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.R;

import java.util.List;

/**
 * Showing result of a player search
 * User: jmelzer
 */
public class SearchResultMultiSelectActivity extends BaseActivity {
    Class goBackToClass;

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

        setContentView(R.layout.search_result_multi_select);
        MyApplication.resetCheckedForSearchResult();

        final ListView listview = (ListView) findViewById(R.id.playerlistview);
        final SearchResultAdapter adapter = new SearchResultAdapter(this,
                android.R.layout.simple_list_item_1,
                MyApplication.searchResult);
        listview.setAdapter(adapter);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Intent i = getIntent();
        goBackToClass = TTRCalculatorActivity.class;
        if (i != null && i.getExtras() != null) {
            goBackToClass = (Class) i.getExtras().getSerializable(SearchActivity.BACK_TO);
        }

//                Player p = MyApplication.searchResult.get(position);
//
//                    Intent intent = new Intent();
//                    intent.putExtra("PLAYERS", p);
//                    setResult(1, intent);
//                    finish();

    }

    public void select(View view) {
//        List<Player> players = new ArrayList<>();
        for (Player p : MyApplication.searchResult) {
            if (p.isChecked()) {
                Log.d(Constants.LOG_TAG, "player checked=" + p);
                MyApplication.addTTRCalcPlayer(p);
            }
        }
        Intent target = new Intent(this, TTRCalculatorActivity.class);
        finish();
        startActivity(target);
    }

    class SearchResultAdapter extends ArrayAdapter<Player> {

        public SearchResultAdapter(Context context, int resource, List<Player> players) {
            super(context, resource, players);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.search_result_multiple_select_row, parent, false);
            final Player player = getItem(position);


            TextView textView = (TextView) rowView.findViewById(R.id.firstname);
            textView.setText(player.getFirstname());
            textView = (TextView) rowView.findViewById(R.id.lastname);
            textView.setText(player.getLastname());
            textView = (TextView) rowView.findViewById(R.id.club);
            textView.setText(player.getClub());
            textView = (TextView) rowView.findViewById(R.id.ttr);
            textView.setText("" + player.getTtrPoints());
            final CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.checkBoxPlayer);
            checkBox.setId(position);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    player.setChecked(isChecked);
                }
            });
            return rowView;
        }
    }
}
