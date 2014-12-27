package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.LoginManager;
import com.jmelzer.myttr.logic.NetworkException;

import java.io.IOException;

/**
 * Base class for same error handling.
 */
public abstract class BaseAsyncTask extends AsyncTask<String, Void, Integer> {
    String errorMessage;
    Activity parent;
    private ProgressDialog progressDialog;
    Class targetClz;

    public BaseAsyncTask(Activity parent, Class targetClz) {
        this.parent = parent;
        this.targetClz = targetClz;
    }

    @Override
    protected void onPreExecute() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(parent);
            progressDialog.setMessage("Lade Daten von myTischtennis.de, bitte warten...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    @Override
    protected Integer doInBackground(String... params) {
        try {
            callParser();
        } catch (NetworkException e) {
            errorMessage = e.getMessage();
        } catch (LoginExpiredException e) {
            try {
                new LoginManager().relogin();
                callParser();
            } catch (Exception e2) {
                errorMessage = "Das erneute Anmelden war nicht erfolgreich";
                Log.e(Constants.LOG_TAG, "", e2);
            }
        }
        return null;
    }

    protected abstract void callParser() throws NetworkException, LoginExpiredException;

    @Override
    protected void onPostExecute(Integer integer) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (errorMessage != null) {
            Toast.makeText(parent, errorMessage, Toast.LENGTH_SHORT).show();
        } else if (!dataLoaded()) {
            Toast.makeText(parent, "Konnte die Daten nicht laden (Grund unbekannt)",
                    Toast.LENGTH_SHORT).show();
        } else {
            Intent target = new Intent(parent, targetClz);
            parent.startActivity(target);
        }
    }

    protected abstract boolean dataLoaded();
}
