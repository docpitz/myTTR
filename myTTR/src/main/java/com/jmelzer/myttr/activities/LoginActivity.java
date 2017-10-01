/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */

package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.User;
import com.jmelzer.myttr.db.LoginDataBaseAdapter;

public class LoginActivity extends Activity {
    public static String NOAUTOLOGIN;

    LoginDataBaseAdapter loginDataBaseAdapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_PROGRESS);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.login);

        final EditText userNameTextField = (EditText) findViewById(R.id.username);
//        userNameTextField.setText("chokdee");

        final EditText pwTextField = (EditText) findViewById(R.id.password);
//pwTextField.setText("fuckyou123");
        loginDataBaseAdapter = new LoginDataBaseAdapter(this);
        loginDataBaseAdapter = loginDataBaseAdapter.open();

        loginDataBaseAdapter.deleteAllEntriesIfErrors();

        User user = loginDataBaseAdapter.getSinlgeEntry();
        if (user != null) {
            userNameTextField.setText(user.getUsername());
            pwTextField.setText(user.getPassword());
            Intent i = getIntent();

            if (i == null || i.getExtras() == null || !i.getExtras().getBoolean(NOAUTOLOGIN, false)) {
                login(null);
            }
        }
        // Close The Database
        loginDataBaseAdapter.close();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (MyApplication.getPoints() > 0) {
            gotoNextActivity();
        }
    }

    public void gotoNextActivity() {
        Intent target = new Intent(this, HomeActivity.class);
        startActivity(target);
    }

    public void login(final View view) {

        String username = ((EditText) findViewById(R.id.username)).getText().toString();
        String pw = ((EditText) findViewById(R.id.password)).getText().toString();

        LoginTask task = new LoginTask(this, username, pw);
        task.execute();

    }

    public void liga(View view) {
        MyApplication.selectedVerband = null;
        AsyncTask<String, Void, Integer> task = new LigenAsyncTask(this, LigaHomeActivity.class);
        task.execute();
    }

    @Override
    public void onBackPressed() {
        //close the app, see myttr-27
        super.onBackPressed();
    }

    public void versioncheck(View view) {
        VersionCheckDialog dialog = new VersionCheckDialog(this);
        dialog.setTitle("Versionsprüfung");
        dialog.show();
    }
}
