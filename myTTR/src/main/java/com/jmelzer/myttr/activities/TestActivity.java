package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jmelzer.myttr.R;

public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity);
    }

    public void login(View view) {
        Intent target = new Intent(this, HomeActivity.class);
        startActivity(target);
    }
}
