package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jmelzer.myttr.EventDetail;
import com.jmelzer.myttr.Game;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.tasks.Head2HeadAsyncTask;

import java.util.List;

/**
 * Created by cicgfp on 10.02.2018.
 */

public class DetailHelper {
    Activity activity;
    int actualPos = -1;
    List<Game> games;
    List<Player> players;

    public DetailHelper(Activity activity, ListView listview, List<Player> players) {
        this.players = players;
        init(activity, listview);
    }

    public DetailHelper(Activity activity, List<Game> games, ListView listview) {
        this.games = games;
        init(activity, listview);
    }

    void init(Activity activity, ListView listview) {
        this.activity = activity;
        registerListener(activity, listview);
    }

    private void registerListener(Activity activity, ListView listview) {
        activity.registerForContextMenu(listview);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                view.setSelected(true);
                actualPos = position;
                start(position);
            }
        });
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                actualPos = position;
                return false;
            }
        });
    }

    private void start(int position) {
        if (games != null) {
            Game game = games.get(position);
            new EventsAsyncTask(DetailHelper.this.activity, EventsActivity.class, game).execute();
        } else {
            Player p = players.get(position);
            new EventsAsyncTask(this.activity, EventsActivity.class, p).execute();
        }
    }

    public void createMenu(ContextMenu menu) {
        menu.setHeaderTitle("Aktionen");
        menu.add(1, actualPos, 1, "Spieler Statistiken");
        menu.add(2, actualPos, 1, "Head 2 Head");
    }

    public boolean onSelect(MenuItem item) {
        switch (item.getGroupId()) {
            case 1:
                callEvents(item.getItemId());
                break;
            case 2:
                callHead2Head(item.getItemId());
                break;

        }
        return true;
    }

    private void callHead2Head(int position) {
        if (games != null) {
            Game game = games.get(position);
            new Head2HeadAsyncTask(activity, game.getPlayerId(), Head2HeadActivity.class).execute();
        } else {
            Player p = players.get(position);
            new Head2HeadAsyncTask(activity, p.getPersonId(), Head2HeadActivity.class).execute();
        }
    }


    private void callEvents(int itemId) {
        start(itemId);
    }
}
