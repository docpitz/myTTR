package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.Liga;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.db.FavoriteDataBaseAdapter;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.NoClickTTException;
import com.jmelzer.myttr.logic.impl.MytClickTTWrapper;
import com.jmelzer.myttr.model.Favorite;
import com.jmelzer.myttr.model.SearchPlayer;
import com.jmelzer.myttr.model.Verein;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cicgfp on 31.12.2017.
 */

public class FavoriteManager {

    Context context;
    Activity parent;
    MytClickTTWrapper clickTTWrapper = new MytClickTTWrapper();

    public FavoriteManager(Activity parent, Context context) {
        this.context = context;
        this.parent = parent;
    }

    public List<Favorite> getFavorites(Class... types) {
        FavoriteDataBaseAdapter adapter = getDB();
        List<Favorite> list = new ArrayList<>();
        for (Favorite favorite : adapter.getAllEntries()) {
            if (types.length == 0)
                list.add(favorite);
            else {
                for (Class type : types) {
                    if (favorite.getClass().equals(type)) {
                        list.add(favorite);
                        break;
                    }
                }
            }
        }
        return list;
    }

    @NonNull
    private FavoriteDataBaseAdapter getDB() {
        FavoriteDataBaseAdapter adapter = new FavoriteDataBaseAdapter(context);
        adapter.open();
        return adapter;
    }

    public void startFavorite(int id, Class... types) {

        List<Favorite> list = getFavorites(types);
        if (id >= list.size())
            return;

        Favorite favorite = list.get(id);
        Log.d(Constants.LOG_TAG, "setting fav to " + favorite.getClass().getName());
        if (favorite instanceof Liga) {
            MyApplication.setSelectedLiga((Liga) favorite);
            startLiga();
        } else if (favorite instanceof Verein) {

            MyApplication.selectedVerein = (Verein) favorite;
            startVerein(MyApplication.selectedVerein);

        } else if (favorite instanceof Player) {
            Player player = null;
            try {
                player = Player.convertFromJson(favorite.getUrl());
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            MyApplication.selectedPlayer = player;
            startEvents();

        } else if (favorite instanceof SearchPlayer) {
            SearchPlayer searchPlayer = null;
            try {
                searchPlayer = SearchPlayer.convertFromJson(favorite.getUrl());
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            Intent target = new Intent(context, SearchActivity.class);
            target.putExtra(SearchActivity.BACK_TO, SearchActivity.class);
            target.putExtra(SearchActivity.INTENT_SP, searchPlayer);
            context.startActivity(target);
        }

    }

    private void startEvents() {
        new EventsAsyncTask(parent, EventsActivity.class, MyApplication.selectedPlayer).execute();
    }

    public void startLiga() {
        AsyncTask<String, Void, Integer> task = new BaseAsyncTask(parent, LigaTabelleActivity.class) {

            @Override
            protected void callParser() throws NetworkException, LoginExpiredException {
                clickTTWrapper.readLiga(MyApplication.saison, MyApplication.getSelectedLiga());
            }

            @Override
            protected boolean dataLoaded() {
                return MyApplication.getSelectedLiga().getMannschaften().size() > 0;
            }


        };
        task.execute();
    }

    public void startVerein(final Verein verein) {
        AsyncTask<String, Void, Integer> task = new BaseAsyncTask(parent, LigaVereinActivity.class) {

            @Override
            protected void callParser() throws NetworkException, LoginExpiredException, NoClickTTException {
                MyApplication.selectedVerein = clickTTWrapper.readVerein(verein.getUrl(), MyApplication.saison);
            }

            @Override
            protected boolean dataLoaded() {
                return MyApplication.selectedVerein != null;
            }


        };
        task.execute();
    }

    public void startOwnVerein() {
        AsyncTask<String, Void, Integer> task = new BaseAsyncTask(parent, LigaVereinActivity.class) {

            @Override
            protected void callParser() throws NetworkException, LoginExpiredException, NoClickTTException {
                MyApplication.selectedVerein = clickTTWrapper.readOwnVerein();
            }

            @Override
            protected boolean dataLoaded() {
                return MyApplication.selectedVerein != null;
            }


        };
        task.execute();
    }


    public void favorite(Liga liga) {
        if (liga == null) {
            Toast.makeText(parent, parent.getString(R.string.favorite_error), Toast.LENGTH_SHORT).show();
            return;
        }
        FavoriteDataBaseAdapter adapter = getDB();

        if (adapter.existsEntry(liga.getNameForFav())) {
            Toast.makeText(parent, parent.getString(R.string.favorite_exists), Toast.LENGTH_LONG).show();
        } else {
            adapter.insertEntry(liga.getNameForFav(), liga.getUrl(), Liga.class.getName());
            Toast.makeText(parent, parent.getString(R.string.favorite_added), Toast.LENGTH_LONG).show();
        }
    }
    public void favorite(Verein verein) {
        FavoriteDataBaseAdapter adapter = getDB();

        if (adapter.existsEntry(verein.getNameForFav())) {
            Toast.makeText(parent, parent.getString(R.string.favorite_exists), Toast.LENGTH_LONG).show();
        } else {
            adapter.insertEntry(verein.getNameForFav(),
                    verein.getUrl(),
                    Verein.class.getName());
            Toast.makeText(parent, parent.getString(R.string.favorite_club_added), Toast.LENGTH_LONG).show();
        }
    }

    public void favorite(SearchPlayer searchPlayer) {
        FavoriteDataBaseAdapter adapter = getDB();

        if (adapter.existsEntry(searchPlayer.createName())) {
            Toast.makeText(parent, parent.getString(R.string.favorite_exists), Toast.LENGTH_LONG).show();
        } else {
            try {
                adapter.insertEntry(searchPlayer.createName(),
                        searchPlayer.convertToJson(),
                        SearchPlayer.class.getName());
                Toast.makeText(parent, parent.getString(R.string.favorite_search_added), Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                Toast.makeText(parent, "Fehler bem Speichern", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void favorite(Player player) {
        if (player == null || player.getFullName() == null) {
            Toast.makeText(parent, parent.getString(R.string.favorite_error_name), Toast.LENGTH_LONG).show();
            return;
        }
        FavoriteDataBaseAdapter adapter = getDB();

        if (adapter.existsEntry(player.getFullName())) {
            Toast.makeText(parent, parent.getString(R.string.favorite_exists), Toast.LENGTH_LONG).show();
        } else {
            try {
                adapter.insertEntry(player.getFullName(),
                        player.convertToJson(),
                        Player.class.getName());
                Toast.makeText(parent, parent.getString(R.string.favorite_player_added), Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                Toast.makeText(parent, "Fehler bem Speichern", Toast.LENGTH_LONG).show();
            }
        }
    }
}
