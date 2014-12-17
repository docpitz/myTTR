/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */

package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.User;
import com.jmelzer.myttr.logic.LoginManager;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.PlayerNotWellRegistered;

import java.io.IOException;

public class LoginActivity extends Activity {
    Button btnSignIn;

    LoginDataBaseAdapter loginDataBaseAdapter;

    LoginManager loginManager = new LoginManager();

    boolean loginSuccess;

    String errorMessage;

    private boolean playerNotWellRegistered = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.login);

        final EditText userNameTextField = (EditText) findViewById(R.id.username);
        //todo
        userNameTextField.setText("chokdee");

        final EditText pwTextField = (EditText) findViewById(R.id.password);
        pwTextField.setText("fuckyou");


        loginDataBaseAdapter = new LoginDataBaseAdapter(this);
        loginDataBaseAdapter = loginDataBaseAdapter.open();

        // Get The Refference Of Buttons
        btnSignIn = (Button) findViewById(R.id.button_login);

        loginDataBaseAdapter.deleteAllEntriesIfErrors();

        User user = loginDataBaseAdapter.getSinlgeEntry();
        if (user != null) {
            userNameTextField.setText(user.getUsername());
            pwTextField.setText(user.getPassword());
            login(null);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (MyApplication.loginUser.getPoints() > 0) {
            gotoNextActivity();
        }
    }

    private void gotoNextActivity() {
        Intent target = new Intent(this, HomeActivity.class);
        startActivity(target);
    }

    public void login(final View view) {

        AsyncTask<String, Void, Integer> task = new LoginTask();
        task.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close The Database
        loginDataBaseAdapter.close();
    }

    class LoginTask extends AsyncTask<String, Void, Integer> {
        ProgressDialog progressDialog;

        long start;

        int ttr = 0;

        @Override
        protected void onPostExecute(Integer integer) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            if (playerNotWellRegistered) {
                MyApplication.loginUser.setPoints(-1);
                gotoNextActivity();
                return;
            }
            if (loginSuccess && ttr == 0) {
                Toast.makeText(LoginActivity.this, "Login war erfolgreich konnte aber die Punkte nicht finden.",
                        Toast.LENGTH_SHORT).show();
            } else if (!loginSuccess && errorMessage != null) {
                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            } else if (!loginSuccess) {
                Toast.makeText(LoginActivity.this, "Login war nicht erfolgreich. Hast du einen Premiumaccount?",
                        Toast.LENGTH_SHORT).show();
            } else {
                MyApplication.loginUser.setPoints(ttr);
                gotoNextActivity();
            }
        }

        @Override
        protected void onPreExecute() {
            start = System.currentTimeMillis();
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Login zu mytischtennis.de, bitte warten...");
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }

        @Override
        protected Integer doInBackground(String... params) {

            String username = ((EditText) findViewById(R.id.username)).getText().toString();
            String pw = ((EditText) findViewById(R.id.password)).getText().toString();
            errorMessage = null;
            try {
                if (loginManager.login(username, pw)) {
                    loginSuccess = true;
                    MyTischtennisParser myTischtennisParser = new MyTischtennisParser();

                    try {
                        ttr = myTischtennisParser.getPoints();
                    } catch (PlayerNotWellRegistered e) {
                        playerNotWellRegistered = true;
                    }
                    String name = myTischtennisParser.getRealName();
                    MyApplication.loginUser = new User(name, username, pw, ttr);
                    loginDataBaseAdapter.deleteEntry(username);

                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                    Boolean saveUser = sharedPref.getBoolean(MySettingsActivity.KEY_PREF_SAVE_USER, true);
                    if (saveUser) {
                        loginDataBaseAdapter.insertEntry(name, username, pw, ttr);
                    }
                }
            } catch (IOException e) {
                errorMessage = NetworkException.translate(e);
                Log.d(Constants.LOG_TAG, "", e);
            }

            return null;
        }
    }
}
