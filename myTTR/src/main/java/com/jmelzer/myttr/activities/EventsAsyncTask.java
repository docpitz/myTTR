package com.jmelzer.myttr.activities;

import android.app.Activity;

import com.jmelzer.myttr.Game;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.Spieler;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.MyTTClickTTParser;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.impl.MyTTClickTTParserImpl;
import com.jmelzer.myttr.model.MyTTPlayerIds;

/**
 * task for calling events activity.
 */
public class EventsAsyncTask extends BaseAsyncTask {

    Game game = null;
    Player player;
    MyTTPlayerIds ids;
    MyTischtennisParser parser = new MyTischtennisParser();
    MyTTClickTTParser newParser = new MyTTClickTTParserImpl();

    public EventsAsyncTask(Activity parent, Class targetClz) {
        super(parent, targetClz);
    }

    public EventsAsyncTask(Activity parent, Class targetClz, Game game) {
        super(parent, targetClz);
        this.game = game;
    }

    public EventsAsyncTask(Activity parent, Class targetClz, Player p, MyTTPlayerIds ids) {
        this(parent, targetClz, p);
        this.ids = ids;
    }

    public EventsAsyncTask(Activity parent, Class targetClz, Player p) {
        super(parent, targetClz);
        this.player = p;
    }

    @Override
    protected void callParser() throws NetworkException, LoginExpiredException {
        if (game != null) {
            Player p = parser.readEventsForForeignPlayer(game.getPlayerId());
            MyApplication.setEvents(p.getEvents());
            MyApplication.selectedPlayer = p.getFullName();

        } else if (player != null) {
            if (ids != null) {
                Spieler spieler = newParser.readPopUp(player.getFullName(), ids);
                player = new Player();
                player.setPersonId(spieler.getPersonId());
            }
            Player p = parser.readEventsForForeignPlayer(player.getPersonId());
            MyApplication.setEvents(p.getEvents());
            MyApplication.selectedPlayer = p.getFullName();

        } else {
            MyApplication.setEvents(parser.readEvents().getEvents());
        }
    }

    @Override
    protected boolean dataLoaded() {
        return !MyApplication.getEvents().isEmpty();
    }

}