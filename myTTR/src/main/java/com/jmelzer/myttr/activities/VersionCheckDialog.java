package com.jmelzer.myttr.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.Client;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.VersionChecker;
import com.jmelzer.myttr.model.LastNotification;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 */
public class VersionCheckDialog extends BaseInfoDialog {
    private VersionChecker versionChecker = new VersionChecker();

    public VersionCheckDialog(Context context) {
        super(context);
    }

    /**
     * This is the standard Android on create method that gets called when the activity initialized.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.versions_check);

        final View btn = findViewById(R.id.button_start);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new VersionAsyncTask(VersionCheckDialog.this).execute();
            }
        });
        TextView txt = findViewById(R.id.textViewDate);
        LastNotification lastNotification = versionChecker.getLastCheck();
        if (lastNotification != null) {
            DateFormat formatter = SimpleDateFormat.getDateTimeInstance();
            txt.setText(formatter.format( lastNotification.getChangedAt()));
        }
    }

    protected void onTaskCompleted(String[] ahref) {
        final TextView result = findViewById(R.id.result);
        result.setVisibility(View.VISIBLE);
        if (ahref == null)
            result.setText("Du hast bereits die aktuellste Version");
        else
            result.setText("Es gibt eine neue Version:\n" +
                    ahref[1] + "\nDu kannst sie hier downloaden:\n" + ahref[0]);
    }
}

class VersionAsyncTask extends AsyncTask<String, Void, Integer> {


    VersionCheckDialog v;
    String[] ahref;

    public VersionAsyncTask(VersionCheckDialog v) {
        this.v = v;
    }

    @Override
    protected Integer doInBackground(String... params) {
        try {
            long start = System.currentTimeMillis();
            ahref = new VersionChecker().readVersionInfo();

            Log.i(Constants.LOG_TAG, "parser time " + (System.currentTimeMillis() - start) + " ms");
        } catch (NetworkException e) {
        } catch (Exception e) {
//            catch all others
            Log.e(Constants.LOG_TAG, "Error reding " + Client.lastUrl, e);
//            errorMessage = "Fehler beim Lesen der Webseite " + Client.lastUrl;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        v.onTaskCompleted(ahref);
    }

}
