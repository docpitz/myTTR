package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;

/**
 * Base class for all activities setting the header etc.
 */
public abstract class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
//
//        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.header);

        setTitle(MyApplication.getTitle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings: {
                Intent intent = new Intent(this, MySettingsActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.action_home: {
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.menu_about: {
                AboutDialog about = new AboutDialog(this);
                about.setTitle("Über myTTR");
                about.show();
                break;
            }
            case R.id.menu_impressum: {
                ImpressumDialog dialog = new ImpressumDialog(this);
                dialog.setTitle("Impressum");
                dialog.show();
                break;
            }
        }
        return false;
    }
}
