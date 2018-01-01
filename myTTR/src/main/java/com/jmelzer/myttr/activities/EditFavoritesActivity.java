package com.jmelzer.myttr.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jmelzer.myttr.R;
import com.jmelzer.myttr.db.FavoriteDataBaseAdapter;
import com.jmelzer.myttr.model.Favorite;

import java.util.List;

/**
 * Created by J. Melzer on 09.03.2015.
 * edit the liga favs.
 */
public class EditFavoritesActivity extends BaseActivity {
    FavAdapter adapter;
    FavoriteManager favoriteManager;

    @Override
    protected boolean checkIfNeccessryDataIsAvaible() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.edit_favorite_liga);
        favoriteManager = new FavoriteManager(this, getApplicationContext());

        final ListView listview = (ListView) findViewById(R.id.listFavs);
        adapter = new FavAdapter(this,
                android.R.layout.simple_list_item_1,
                load());
        listview.setAdapter(adapter);
    }

    private List<Favorite> load() {
        return favoriteManager.getFavorites();
    }

    class FavAdapter extends ArrayAdapter<Favorite> {

        public FavAdapter(Context context, int resource, List<Favorite> list) {
            super(context, resource, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.favorite_row, parent, false);
            final Favorite favorite = getItem(position);
            TextView textView = (TextView) rowView.findViewById(R.id.name);
            textView.setText(favorite.typeForMenu() + ": " + favorite.getName());

            final ImageView imageView = (ImageView) rowView.findViewById(R.id.imageButton);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteFromDb(favorite);

                }
            });
            return rowView;
        }
    }

    private void deleteFromDb(Favorite favorite) {
        FavoriteDataBaseAdapter dpAdapter = new FavoriteDataBaseAdapter(getApplicationContext());
        dpAdapter.open();
        dpAdapter.deleteEntry(favorite.getName());
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
