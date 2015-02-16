/*
 * Copyright (c) Juergen Melzer
 *
 * 2014.
 */


package com.jmelzer.myttr.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;

public class EnterAKActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_ak);

        EditText txtField = (EditText) findViewById(R.id.enter_ak);
        txtField.setText("" + MyApplication.getLoginUser().getAk());
    }

    public void ok(final View view) {

        EditText txtField = (EditText) findViewById(R.id.enter_ak);
        int ak;
        try {
            ak = Integer.valueOf(txtField.getText().toString());
            MyApplication.getLoginUser().setAk(ak);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Das ist keine g\u00FCltige Zahl.", Toast.LENGTH_SHORT).show();
            return;
        }
        LoginDataBaseAdapter adapter = new LoginDataBaseAdapter(MyApplication.getAppContext());
        adapter.storeAk(ak);
        Toast.makeText(this,
                "Deine Änderungskonstante wurde auf " + ak + " ge\u00E4ndert",
                Toast.LENGTH_SHORT).show();
        Intent target = new Intent(this, HomeActivity.class);
        startActivity(target);
    }
}
