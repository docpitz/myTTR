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
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.User;
import com.jmelzer.myttr.logic.LoginManager;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.PlayerNotWellRegistered;

import java.net.SocketTimeoutException;

public class LoginActivity extends Activity {
    Button btnSignIn;
    LoginDataBaseAdapter loginDataBaseAdapter;
    LoginManager loginManager = new LoginManager();
    boolean loginSuccess;
    private boolean playerNotWellRegistered = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.login);

        final EditText userNameTextField = (EditText) findViewById(R.id.username);
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
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            return null;
        }
    }

    private void gotoNextActivity() {
        Intent target = new Intent(this, AfterLoginActivity.class);
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
}
