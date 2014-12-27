package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.jmelzer.myttr.Event;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;

/**
 * Reading Details
 */
public class DetailAsyncTask extends BaseAsyncTask {


    Event event;

    DetailAsyncTask(Event event, Activity parent, Class targetClz) {
        super(parent, targetClz);
        this.event = event;
    }

    @Override
    protected boolean dataLoaded() {
        return MyApplication.currentDetail != null;
    }

    @Override
    protected void callParser() throws NetworkException {
        MyApplication.currentDetail = new MyTischtennisParser().readEventDetail(event);
    }

}