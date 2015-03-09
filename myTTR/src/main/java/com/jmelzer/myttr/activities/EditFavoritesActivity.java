package com.jmelzer.myttr.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.db.FavoriteLigaDataBaseAdapter;

import java.util.List;

/**
 * Created by J. Melzer on 09.03.2015.
 * edit the liga favs.
 */
public class EditFavoritesActivity extends BaseActivity {
    FavAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_favorite_liga);

        final ListView listview = (ListView) findViewById(R.id.listFavs);
        adapter = new FavAdapter(this,
                android.R.layout.simple_list_item_1,
                load());
        listview.setAdapter(adapter);
    }

    private List<Liga> load() {
        FavoriteLigaDataBaseAdapter adapter = new FavoriteLigaDataBaseAdapter(getApplicationContext());
        adapter.open();
        return adapter.getAllEntries();
    }

    class FavAdapter extends ArrayAdapter<Liga> {

        public FavAdapter(Context context, int resource, List<Liga> list) {
            super(context, resource, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.favorite_row, parent, false);
            final Liga liga = getItem(position);
            TextView textView = (TextView) rowView.findViewById(R.id.name);
            textView.setText(liga.getName());

            final ImageView imageView = (ImageView) rowView.findViewById(R.id.imageButton);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteFromDb(liga);

                }
            });
            return rowView;
        }
    }

    private void deleteFromDb(Liga liga) {
        FavoriteLigaDataBaseAdapter dpAdapter = new FavoriteLigaDataBaseAdapter(getApplicationContext());
        dpAdapter.open();
        dpAdapter.deleteEntry(liga.getName());
        adapter.clear();
        adapter.addAll(load());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        Intent target = new Intent(this, LigaHomeActivity.class);
        startActivity(target);
    }
}
