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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.User;

public class LoginActivity extends Activity {
    Button btnSignIn;

    LoginDataBaseAdapter loginDataBaseAdapter;

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
        // Close The Database
        loginDataBaseAdapter.close();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (MyApplication.loginUser != null && MyApplication.loginUser.getPoints() > 0) {
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

        AsyncTask<String, Void, Integer> task = new LoginTask(this, username, pw);
        task.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
