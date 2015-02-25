package com.jmelzer.myttr.activities;

import android.app.Activity;

import com.jmelzer.myttr.Game;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.logic.ClickTTParser;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;

/**
 * task for calling events activity.
 */
public class LigenAsyncTask extends BaseAsyncTask {


    public LigenAsyncTask(Activity parent, Class targetClz) {
        super(parent, targetClz);
    }

    @Override
    protected void callParser() throws NetworkException, LoginExpiredException {
            MyApplication.topLigen = new ClickTTParser().readTopLigen();
    }

    @Override
    protected boolean dataLoaded() {
        return MyApplication.topLigen != null;
    }

}