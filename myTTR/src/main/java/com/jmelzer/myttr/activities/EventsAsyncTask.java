package com.jmelzer.myttr.activities;

import android.app.Activity;

import com.jmelzer.myttr.Game;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;

/**
 * task for calling events activity.
 */
public class EventsAsyncTask extends BaseAsyncTask {

    Game game = null;
    Player player;

    public EventsAsyncTask(Activity parent, Class targetClz) {
        super(parent, targetClz);
    }

    public EventsAsyncTask(Activity parent, Class targetClz, Game game) {
        super(parent, targetClz);
        this.game = game;
    }
    public EventsAsyncTask(Activity parent, Class targetClz, Player p) {
        super(parent, targetClz);
        this.player = p;
    }

    @Override
    protected void callParser() throws NetworkException, LoginExpiredException {
        if (game != null) {
            Player p = new MyTischtennisParser().readEventsForForeignPlayer(game.getPlayerId());
            MyApplication.setEvents(p.getEvents());
            MyApplication.selectedPlayer = p.getFullName();

        } else if (player != null) {
            Player p = new MyTischtennisParser().readEventsForForeignPlayer(player.getPersonId());
            MyApplication.setEvents(p.getEvents());
            MyApplication.selectedPlayer = p.getFullName();

        } else {
            MyApplication.setEvents(new MyTischtennisParser().readEvents().getEvents());
        }
    }

    @Override
    protected boolean dataLoaded() {
        return !MyApplication.getEvents().isEmpty();
    }

}