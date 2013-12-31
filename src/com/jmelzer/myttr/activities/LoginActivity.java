/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */

package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.app.ProgressDialog;
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
import com.jmelzer.myttr.parser.LoginManager;
import com.jmelzer.myttr.parser.TTRPointParser;

public class LoginActivity extends Activity {
    Button btnSignIn;
    LoginDataBaseAdapter loginDataBaseAdapter;
    LoginManager loginManager = new LoginManager();

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

        User user = loginDataBaseAdapter.getSinlgeEntry();
        if (user != null) {
            login(null);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (MyApplication.ttrValue > 0) {
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
            System.out.println("time = " + (System.currentTimeMillis() - start) + "ms");
            if (ttr == 0) {
                Toast.makeText(LoginActivity.this, "Login war nicht erfolgreich bitte nochmals versuchen.",
                               1000);
//                    AlertDialog ad = new AlertDialog.Builder(LoginActivity.this).create();
//                    ad.setCancelable(false); // This blocks the 'BACK' button
//                    ad.setMessage("TTR punkte = " + ttr);
//                    ad.setButton("OK", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//                    ad.show();
            } else {
                MyApplication.ttrValue = ttr;
                gotoNextActivity();
            }
        }

        @Override
        protected void onPreExecute() {
            start = System.currentTimeMillis();
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Login, bitte warten...");
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }

        @Override
        protected Integer doInBackground(String... params) {

            String username = ((EditText) findViewById(R.id.username)).getText().toString();
            String pw = ((EditText) findViewById(R.id.password)).getText().toString();
            if (loginManager.login(username, pw)) {
                loginDataBaseAdapter.deleteEntry(username);
                loginDataBaseAdapter.insertEntry(username, pw);
                TTRPointParser ttrPointParser = new TTRPointParser();
                ttr = ttrPointParser.getPoints();
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
