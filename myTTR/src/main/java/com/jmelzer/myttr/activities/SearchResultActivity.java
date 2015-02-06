package com.jmelzer.myttr.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;

/**
 * Showing result of a player search
 * User: jmelzer
 */
public class SearchResultActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);

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
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                view.setSelected(true);
                if (position > -1 && position < MyApplication.searchResult.size()) {
                    if (MyApplication.actualPlayer != null) {
                        MyApplication.actualPlayer.copy(MyApplication.searchResult.get(position));
                    } else {
                        MyApplication.actualPlayer = MyApplication.searchResult.get(position);
                        MyApplication.players.add(MyApplication.actualPlayer);
                    }
                    //todo must be given from the client
                    Intent target = new Intent(SearchResultActivity.this, TTRCalculatorActivity.class);
                    SearchResultActivity.this.startActivity(target);
                    return true;
                }
                return false;
            }
        });

    }

}
