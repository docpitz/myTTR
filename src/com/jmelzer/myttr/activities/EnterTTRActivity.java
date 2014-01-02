/*
 * Copyright (c) Juergen Melzer
 *
 * 2014.
 */


package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;

public class EnterTTRActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enterttr);
        setTitle(MyApplication.getTitle());
    }

    public void ok(final View view) {

        EditText txtField = (EditText) findViewById(R.id.txt_ttrvalue);
        try {
            MyApplication.ttrValue = Integer.valueOf(txtField.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Das ist keine g\u00FCltige Zahl.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent target = new Intent(this, AfterLoginActivity.class);
        startActivity(target);
    }
}
