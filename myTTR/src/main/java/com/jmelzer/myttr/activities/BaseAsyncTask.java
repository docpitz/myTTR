package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.db.LoginDataBaseAdapter;
import com.jmelzer.myttr.logic.Client;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.LoginManager;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.NiceGuysException;
import com.jmelzer.myttr.logic.NoClickTTException;
import com.jmelzer.myttr.logic.NoDataException;
import com.jmelzer.myttr.logic.ValidationException;
import com.jmelzer.myttr.logic.impl.MyTTClickTTParserImpl;

import java.util.Timer;
import java.util.TimerTask;

import static com.jmelzer.myttr.activities.BadPeopleUtil.handleBadPeople;


/**
 * Base class for same error handling.
 */
public abstract class BaseAsyncTask extends AsyncTask<String, Void, Integer> {
    String errorMessage;
    Activity parent;
    private ProgressDialog progressDialog;
    Class targetClz;
    boolean notSoNice = false;

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
        } catch (ValidationException e) {
            errorMessage = e.getMessage();
            Log.i(Constants.LOG_TAG, e.getMessage());
        } catch (LoginExpiredException e) {
            try {
                new LoginManager().relogin();
                callParser();
            } catch (Exception e2) {
                errorMessage = "Das erneute Anmelden war nicht erfolgreich";
                Log.e(Constants.LOG_TAG, "", e2);
                Crashlytics.setString("last_url", Client.lastUrls());
                Crashlytics.logException(e2);
            }
        } catch (NoClickTTException e) {
            Log.d(Constants.LOG_TAG, "second try after no data msg");
            try {
                callParser();
                Log.d(Constants.LOG_TAG, "success");
            } catch (NoClickTTException e2) {
                logError(e);
                errorMessage = new MyTTClickTTParserImpl().parseError(Client.lastHtml);
            }
            catch (Exception e2) {
                logError(e);
                errorMessage = "Fehler beim Lesen der Webseite \n" + Client.shortenUrl();
            }
        } catch (NetworkException e) {
            errorMessage = "Das Netzwerk antwortet zu langsam oder ist ausgeschaltet";
        }
        catch (NoDataException ed) {
            logError(ed);
            errorMessage = "Mytischtennis.de meldet: " + ed.getMessage();
        }
        catch (NiceGuysException e) {
            Crashlytics.log(e.getMessage() + " ausgelogged: " + MyApplication.getLoginUser().getRealName());
            Crashlytics.logException(e);
            notSoNice = true;

        }catch (Exception e) {
//            catch all others
            logError(e);
            errorMessage = "Fehler beim Lesen der Webseite \n" + Client.shortenUrl();
        }
        return null;
    }

    private void logError(Exception e) {
        Log.e(Constants.LOG_TAG, "Error reading " + Client.lastUrl(), e);
        Log.e(Constants.LOG_TAG, getLastH1());
        Crashlytics.setString("last_url", Client.lastUrls());
        Crashlytics.logException(e);
        Crashlytics.log(getLastH1());

    }

    private String getLastH1() {
        if (Client.lastHtml != null)
            try {
            int start = Client.lastHtml.indexOf("<p class=\"alert alert-danger\"");
                return Client.lastHtml.substring(Client.lastHtml.indexOf("<p class=\"alert alert-danger\""),
                        Client.lastHtml.indexOf("</h1>"));
            } catch (Exception e) {
                return "no h1 found";
            }
        return "--";
    }


    protected abstract void callParser() throws NoDataException, NetworkException, LoginExpiredException, ValidationException, NoClickTTException, NiceGuysException;

    @Override
    protected void onPostExecute(Integer integer) {
        try {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            //see myttr-62
        }
        if (notSoNice) {
            handleBadPeople(parent);
            return;
        }
        if (errorMessage != null) {
            new ErrorDialog(parent, errorMessage, Client.lastUrl()).show();
        } else if (!dataLoaded()) {
            Log.d(Constants.LOG_TAG, "couldn't load data in class " + getClass());
            new ErrorDialog(parent, "Konnte die Daten nicht laden (Grund unbekannt)", Client.lastUrl()).show();
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
