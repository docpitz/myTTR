package com.jmelzer.myttr.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.LoginManager;

/**
 * Base class for all activities setting the header etc.
 */
public abstract class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new UnCaughtException(this));


        if (MyApplication.manualClub != null && !"".equals(MyApplication.manualClub)) {
            setTitle(MyApplication.getTitle() + " - " + MyApplication.manualClub);
        } else {
            setTitle(MyApplication.getTitle());
        }
        setTitle(MyApplication.getTitle() + " - " + getClass().getSimpleName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.settings, menu);
        menu.getItem(2).setVisible(MyApplication.simPlayer != null);
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
            case R.id.action_logout: {
                new LoginManager().logout();
                MyApplication.loginUser = null;
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra(LoginActivity.NOAUTOLOGIN, true);
                startActivity(intent);
                break;
            }
            case R.id.action_remove_sim: {
                MyApplication.simPlayer = null;
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Simulation wurde beendet", Toast.LENGTH_SHORT).show();
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
