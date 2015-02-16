package com.jmelzer.myttr.activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.User;
import com.jmelzer.myttr.logic.LoginManager;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.PlayerNotWellRegistered;

import java.io.IOException;
import java.util.Date;

/**
 * Task that executes the request against mytischtennis.de
 */
public class LoginTask extends AsyncTask<String, Void, Integer> {

    ProgressDialog progressDialog;
    LoginManager loginManager = new LoginManager();
    long start;
    boolean loginSuccess;
    String errorMessage;
    private boolean playerNotWellRegistered = false;
    String username;
    String password;
    int ttr = 0;
    LoginActivity parent;
    LoginDataBaseAdapter loginDataBaseAdapter;

    public LoginTask(LoginActivity parent, String username, String password) {
        this.parent = parent;
        this.username = username;
        this.password = password;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        loginDataBaseAdapter.close();
        if (playerNotWellRegistered) {
            MyApplication.getLoginUser().setPoints(-1);
            parent.gotoNextActivity();
            return;
        }
        if (loginSuccess && ttr == 0) {
            Toast.makeText(parent, "Login war erfolgreich konnte aber die Punkte nicht finden.",
                    Toast.LENGTH_SHORT).show();
        } else if (!loginSuccess && errorMessage != null) {
            Toast.makeText(parent, errorMessage, Toast.LENGTH_SHORT).show();
        } else if (!loginSuccess) {
            Toast.makeText(parent, "Login war nicht erfolgreich. Hast du einen Premiumaccount?",
                    Toast.LENGTH_SHORT).show();
        } else {
            MyApplication.getLoginUser().setPoints(ttr);
            parent.gotoNextActivity();
        }
    }

    @Override
    protected void onPreExecute() {
        start = System.currentTimeMillis();
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(parent);
            progressDialog.setMessage("Login zu mytischtennis.de, bitte warten...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        loginDataBaseAdapter = new LoginDataBaseAdapter(parent);
        loginDataBaseAdapter = loginDataBaseAdapter.open();
    }

    @Override
    protected Integer doInBackground(String... params) {

        errorMessage = null;
        try {
            login(username, password);
        } catch (IOException e) {
            errorMessage = NetworkException.translate(e);
            Log.d(Constants.LOG_TAG, "", e);
        }

        return null;
    }

    private void login(String username, String pw) throws IOException {
        if (loginManager.login(username, pw)) {

            loginSuccess = true;
            MyTischtennisParser myTischtennisParser = new MyTischtennisParser();

            try {
                ttr = myTischtennisParser.getPoints();
            } catch (PlayerNotWellRegistered e) {
                playerNotWellRegistered = true;
            }
            store(username, pw, myTischtennisParser);
        }
    }

    private void store(String username, String pw, MyTischtennisParser myTischtennisParser) {
        String name = myTischtennisParser.getRealName();
        User userDb = loginDataBaseAdapter.getSinlgeEntry();
        int ak = 16;
        if (userDb != null) {
            MyApplication.manualClub = userDb.getClubName();
            ak = userDb.getAk();
        }
        MyApplication.setLoginUser(new User(name, username, pw, ttr, new Date(), MyApplication.manualClub, ak));
        loginDataBaseAdapter.deleteEntry(username);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(parent);
        Boolean saveUser = sharedPref.getBoolean(MySettingsActivity.KEY_PREF_SAVE_USER, true);
        if (saveUser) {
            loginDataBaseAdapter.insertEntry(name, username, pw, ttr, MyApplication.manualClub, ak);
        }
    }
}