package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.content.Intent;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.TooManyPlayersFound;
import com.jmelzer.myttr.logic.ValidationException;
import com.jmelzer.myttr.model.SearchPlayer;

import java.util.List;

/**
 * Created by jmelzer on 14.05.2018.
 */
class SearchAsyncTask extends BaseAsyncTask {
    Class goBackToClass;
    SearchPlayer searchPlayer;
    Player foundSinglePlayer;
    boolean multiSelect;

    public SearchAsyncTask(Activity parent, Class goBackToClass, SearchPlayer searchPlayer, Class targetClz, boolean multiSelect) {
        super(parent, targetClz);
        this.goBackToClass = goBackToClass;
        this.searchPlayer = searchPlayer;
        this.multiSelect = multiSelect;
    }


    @Override
    protected void putExtra(Intent target) {
        target.putExtra(SearchActivity.BACK_TO, goBackToClass);
        target.putExtra(SearchActivity.INTENT_SP, searchPlayer);
    }

    @Override
    protected void callParser() throws NetworkException, LoginExpiredException, ValidationException {
        List<Player> p = null;
        errorMessage = null;
        try {
            p = new MyTischtennisParser().findPlayer(searchPlayer);
        } catch (TooManyPlayersFound tooManyPlayersFound) {
            errorMessage = "Es wurden zu viele Spieler gefunden.";
            return;
        }
        if (p != null) {

            if (p.size() == 1) {

                Player p1 = p.get(0);
                if (MyApplication.actualPlayer != null) {
                    MyApplication.actualPlayer.copy(p1);
                } else {
                    MyApplication.actualPlayer = p1;
                }
                MyApplication.addTTRCalcPlayer(p1);
                foundSinglePlayer = p1;

            } else if (p.size() > 1) {
                if (!multiSelect) {
                    targetClz = SearchResultActivity.class;
                } else {
                    targetClz = SearchResultMultiSelectActivity.class;
                }
                MyApplication.searchResult = p;
            } else {
                errorMessage = "Es wurden keine Spieler gefunden.";
            }
        }
    }

    @Override
    protected void startActivityAfterParsing() {
        if (goBackToClass.equals(EventsActivity.class) && foundSinglePlayer != null) {
            new EventsAsyncTask(parent, EventsActivity.class, foundSinglePlayer).execute();
        } else {
            super.startActivityAfterParsing();
        }
    }

    @Override
    protected boolean dataLoaded() {
        return errorMessage == null;
    }

}
