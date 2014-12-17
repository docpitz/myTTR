package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.logic.NetworkException;

/**
 * Created by cicgfp on 17.12.2014.
 */
public abstract class BaseAsyncTask extends AsyncTask<String, Void, Integer> {
    String errorMessage;
    Activity parent;
    ProgressDialog progressDialog;
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
        }
        return null;
    }

    protected abstract void callParser() throws NetworkException;

    @Override
    protected void onPostExecute(Integer integer) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (errorMessage != null) {
            Toast.makeText(parent, errorMessage,
                    Toast.LENGTH_SHORT).show();
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
