package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.logic.Client;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.LoginManager;
import com.jmelzer.myttr.logic.NetworkException;

/**
 * Base class for same error handling.
 */
public abstract class BaseAsyncTask extends AsyncTask<String, Void, Integer> {
    String errorMessage;
    Activity parent;
    private ProgressDialog progressDialog;
    Class targetClz;

    public BaseAsyncTask(Activity parent, Class targetClz) {
        if (parent == null) throw new IllegalArgumentException("parent must not be null");
        this.parent = parent;
        this.targetClz = targetClz;
    }

    @Override
    protected void onPreExecute() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(parent);
            progressDialog.setMessage("Lade Daten, bitte warten...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    @Override
    protected Integer doInBackground(String... params) {
        try {
            long start = System.currentTimeMillis();
            callParser();
            Log.i(Constants.LOG_TAG, "parser time " + (System.currentTimeMillis() - start) + " ms");
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
        } catch (Exception e) {
//            catch all others
            Log.e(Constants.LOG_TAG, "Error reading " + Client.lastUrl, e);
            errorMessage = "Fehler beim Lesen der Webseite \n" + shortenUrl();
        }
        return null;
    }

    private String shortenUrl() {
        if (Client.lastUrl != null && Client.lastUrl.length() > 30) {
            return Client.lastUrl.substring(0, 30) + "...";
        } else
            return Client.lastUrl;
    }

    protected abstract void callParser() throws NetworkException, LoginExpiredException;

    @Override
    protected void onPostExecute(Integer integer) {
        try {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            //see myttr-62
        }
        if (errorMessage != null) {
            new ErrorDialog(parent, errorMessage, Client.lastUrl).show();
//            Toast.makeText(parent, errorMessage, Toast.LENGTH_SHORT).show();
        } else if (!dataLoaded()) {
            Log.d(Constants.LOG_TAG, "couldn't load data in class " + getClass());
            new ErrorDialog(parent, "Konnte die Daten nicht laden (Grund unbekannt)", Client.lastUrl).show();
//            Toast.makeText(parent, "Konnte die Daten nicht laden (Grund unbekannt)",
//                    Toast.LENGTH_SHORT).show();
        } else {
            startNextActivity();
        }
    }

    protected void startNextActivity() {
        if (targetClz != null) {
            Intent target = new Intent(parent, targetClz);
            putExtra(target);
            parent.startActivityForResult(target, 1);
        }
    }

    protected void putExtra(Intent target) {

    }

    protected abstract boolean dataLoaded();
}
