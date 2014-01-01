/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;

public class AfterLoginActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.afterlogin);

        setTitle(MyApplication.getTitle());


    }

    public void manual(final View view) {
        Intent target = new Intent(this, ManualEntriesActivity.class);
        startActivity(target);
    }
    public void  ocr(final View view) {
        Toast.makeText(getApplicationContext(), "Noch nicht fettig.",
                       Toast.LENGTH_LONG).show();
    }
}
