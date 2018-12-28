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

public class ReadOwnTeamTask extends BaseAsyncTask {
    public ReadOwnTeamTask(Activity parent, Class targetClz) {
        super(parent, targetClz);
    }

    @Override
    protected void callParser() throws NetworkException, LoginExpiredException, NoDataException, ValidationException, NoClickTTException {
        MyTischtennisParser p = new MyTischtennisParser();
        MyApplication.selectedMannschaft = p.readOwnTeam();
        MyApplication.setSelectedLiga(selectedMannschaft.getLiga());
    }

    @Override
    protected boolean dataLoaded() {
        return MyApplication.selectedMannschaft != null;
    }

}
