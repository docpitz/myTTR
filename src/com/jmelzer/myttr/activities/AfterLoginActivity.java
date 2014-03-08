/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.SyncManager;

public class AfterLoginActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.afterlogin);

        setTitle(MyApplication.getTitle());
        startService(new Intent(this, SyncManager.class));
//        new SyncManager().startService();

    }

    public void manual(final View view) {
        Intent target = new Intent(this, ManualEntriesActivity.class);
        startActivity(target);
    }

    public void ocr(final View view) {
        Toast.makeText(getApplicationContext(), "Noch nicht fettig.",
                       Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (MyApplication.loginUser.getPoints() < 0) {
            Intent target = new Intent(this, EnterTTRActivity.class);
            startActivity(target);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_settings:
                Intent intent = new Intent(AfterLoginActivity.this, MySettingsActivity.class);
                startActivity(intent);
                break;
        }
        return false;
    }


}
