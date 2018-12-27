package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.widget.Toast;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.MyTTClickTTParser;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.NoClickTTException;
import com.jmelzer.myttr.logic.NoDataException;
import com.jmelzer.myttr.logic.ValidationException;
import com.jmelzer.myttr.logic.impl.MyTTClickTTParserImpl;

import static com.jmelzer.myttr.MyApplication.selectedMannschaft;

public class ReadOwnClubTask extends BaseAsyncTask {
    public ReadOwnClubTask(Activity parent, Class targetClz) {
        super(parent, targetClz);
    }

    @Override
    protected void callParser() throws NetworkException, LoginExpiredException, NoDataException, ValidationException, NoClickTTException {
        MyTischtennisParser p = new MyTischtennisParser();
        MyTTClickTTParser newParser = new MyTTClickTTParserImpl();
        MyApplication.selectedMannschaft = p.readOwnTeam();
        if (selectedMannschaft.getVereinUrl() == null) {
            newParser.readOwnAdressen(selectedMannschaft.getLiga());
        }
        if (selectedMannschaft.getVereinUrl() == null) {
            errorMessage = "Konnte die URL des Vereines nicht ermitteln";
        } else {
            MyApplication.selectedVerein = newParser.readVerein(selectedMannschaft.getVereinUrl());
            MyApplication.setSelectedLiga(selectedMannschaft.getLiga());
        }
    }

    @Override
    protected boolean dataLoaded() {
        return selectedMannschaft.getVereinUrl() != null;
    }

}
