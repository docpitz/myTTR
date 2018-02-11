package com.jmelzer.myttr.tasks;

import android.app.Activity;

import com.jmelzer.myttr.Game;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.Spieler;
import com.jmelzer.myttr.activities.BaseAsyncTask;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.MyTTClickTTParser;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.impl.MyTTClickTTParserImpl;
import com.jmelzer.myttr.model.Head2HeadResult;
import com.jmelzer.myttr.model.MyTTPlayerIds;

import java.util.List;

/**
 * task for calling head2head activity.
 */
public class Head2HeadAsyncTask extends BaseAsyncTask {

    long playerId;
    MyTischtennisParser parser = new MyTischtennisParser();

    public Head2HeadAsyncTask(Activity parent, long id, Class targetClz) {
        super(parent, targetClz);
        playerId = id;
    }


    @Override
    protected void callParser() throws NetworkException, LoginExpiredException {
        List<Head2HeadResult> results = parser.readHead2Head(playerId);
        MyApplication.setHead2Head(results);

    }

    @Override
    protected boolean dataLoaded() {
        return true;//MyApplication.getHead2Head() != null && !MyApplication.getHead2Head().isEmpty();
    }

}