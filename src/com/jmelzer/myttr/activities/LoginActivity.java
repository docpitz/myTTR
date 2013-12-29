package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TimeUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.parser.LoginManager;
import com.jmelzer.myttr.parser.TTRPointParser;

import java.util.concurrent.ExecutionException;

public class LoginActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.main);

        final EditText userNameTextField = (EditText) findViewById(R.id.username);
        userNameTextField.setText("chokdee");

        final EditText pwTextField = (EditText) findViewById(R.id.password);
        pwTextField.setText("bla");

//        button.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                // Perform action on click
//            }
//        });
    }

    public void login(final View view) {

        AsyncTask<String, Void, Integer> task = new AsyncTask<String, Void, Integer>() {
            ProgressDialog progressDialog;
            long start;
            int ttr = 0;
            @Override
            protected void onPostExecute(Integer integer) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                System.out.println("time = " + (System.currentTimeMillis() - start) + "ms" );
                if (ttr == 0) {
                    AlertDialog ad = new AlertDialog.Builder(LoginActivity.this).create();
                    ad.setCancelable(false); // This blocks the 'BACK' button
                    ad.setMessage("TTR punkte = " + ttr);
                    ad.setButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    ad.show();
                } else {
                    MyApplication.ttrValue = ttr;
                    Intent target = new Intent(LoginActivity.this, AfterLoginActivity.class);
                    startActivity(target);
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

                LoginManager loginManager = new LoginManager();
//                loginManager.login("chokdee", "fuckyou");
                String username = ((EditText) findViewById(R.id.username)).getText().toString();
                String pw = ((EditText) findViewById(R.id.password)).getText().toString();
                if (loginManager.login(username, pw)) {
                    TTRPointParser ttrPointParser = new TTRPointParser();
                    ttr = ttrPointParser.getPoints();
                }

                return null;
            }
        };

//        setProgressBarIndeterminateVisibility(true);

        task.execute();
//        Integer ttr = null;
//        try {
//            ttr = task.get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }

//        if (ttr == null) {
//            AlertDialog ad = new AlertDialog.Builder(this).create();
//            ad.setCancelable(false); // This blocks the 'BACK' button
//            ad.setMessage("TTR punkte = " + ttr);
//            ad.setButton("OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//            });
//            ad.show();
//        } else {
//            MyApplication.ttrValue = ttr;
//            Intent target = new Intent(this, AfterLoginActivity.class);
//            startActivity(target);
//        }
    }

}
