package de.ssp.service.mytischtennis.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.User;
import com.jmelzer.myttr.activities.MySettingsActivity;
import com.jmelzer.myttr.db.LoginDataBaseAdapter;
import com.jmelzer.myttr.logic.LoginException;
import com.jmelzer.myttr.logic.LoginManager;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.PlayerNotWellRegistered;
import com.jmelzer.myttr.logic.ValidationException;

import java.io.IOException;

import de.ssp.service.mytischtennis.caller.ServiceFinish;

/**
 * Task that executes the request against mytischtennis.de
 */
public class MyTischtennisLoginService extends AsyncTask<String, Void, Integer> {

    ProgressDialog progressDialog;
    public LoginManager loginManager = new LoginManager();
    long start;
    boolean loginSuccess;
    String errorMessage;
    private boolean playerNotWellRegistered = false;
    String username;
    String password;
    public int ttr = 0;
    Context context;
    ServiceFinish<String, User> loginServiceFinish;
    private User user;
    LoginDataBaseAdapter loginDataBaseAdapter;

    public MyTischtennisLoginService(Context context, ServiceFinish<String, User> loginServiceFinish, String username, String password) {
        this.context = context;
        this.username = username;
        this.password = password;
        this.loginServiceFinish = loginServiceFinish;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        try {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            //ignore see myttr-82
        }

        if (playerNotWellRegistered) {
            errorMessage = "Login erfolgreich, aber anscheinend nicht komplett registriert.";
            return;
        }
        if (loginSuccess && ttr == 0)
        {
            errorMessage = "Login war erfolgreich konnte aber die Punkte nicht finden.";
        }
        else if (!loginSuccess && errorMessage == null)
        {
            errorMessage = "Login war nicht erfolgreich. Hast du einen Premiumaccount?";
        }
        loginServiceFinish.serviceFinished(username, loginSuccess, user, errorMessage);
    }

    @Override
    protected void onPreExecute() {
        start = System.currentTimeMillis();
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Anmelden bei myTischtennis, bitte warten...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        loginDataBaseAdapter = new LoginDataBaseAdapter(context);
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
        } catch (NetworkException e) {
            errorMessage = e.getMessage();
            Log.d(Constants.LOG_TAG, "", e);
        }
        return null;
    }

    private void login(String username, String pw) throws IOException, NetworkException {
        User user = null;
        try {
            if ((user = loginManager.login(username, pw)) != null) {

                loginSuccess = true;
                ttr = user.getPoints();
                store(user, new MyTischtennisParser());
                this.user = user;
            }
        } catch (PlayerNotWellRegistered playerNotWellRegistered1) {
            playerNotWellRegistered = true;
            store(new User(username, pw), new MyTischtennisParser());
        } catch (ValidationException e) {
            errorMessage = e.getMessage();
            loginSuccess = false;
        } catch (LoginException e) {
            errorMessage = e.getErrorMessage();
            loginSuccess = false;
        }
    }

    private void store(User user, MyTischtennisParser myTischtennisParser) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean saveUser = sharedPref.getBoolean(MySettingsActivity.KEY_PREF_SAVE_USER, true);

        loginManager.loadUserIntoMemoryAndStore(user, saveUser, myTischtennisParser);
    }

}