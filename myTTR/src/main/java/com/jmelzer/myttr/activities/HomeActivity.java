/*
 * Copyright (c) Juergen Melzer
 *
 * 2013.
 */


package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Toast;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.LoginExpiredException;
import com.jmelzer.myttr.logic.LoginManager;
import com.jmelzer.myttr.logic.MyTischtennisParser;
import com.jmelzer.myttr.logic.NetworkException;
import com.jmelzer.myttr.logic.NiceGuysException;
import com.jmelzer.myttr.logic.NoDataException;
import com.jmelzer.myttr.logic.ValidationException;
import com.jmelzer.myttr.model.Favorite;

import java.util.List;

import static com.jmelzer.myttr.MyApplication.actualTTR;

public class HomeActivity extends BaseActivity {

    public static final String BEARBEITEN = "Bearbeiten...";
    FavoriteManager favoriteManager;

    @Override
    protected boolean checkIfNeccessryDataIsAvailable() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (toLoginIfNeccassry()) {
            return;
        }

        setContentView(R.layout.home);

        favoriteManager = new FavoriteManager(this, getApplicationContext());

//        if (!isMyServiceRunning(SyncManager.class)) {
//            Log.d(Constants.LOG_TAG, "SyncManager will be started");
//            startService(new Intent(this, SyncManager.class));
//        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void manual(final View view) {
        Intent target = new Intent(this, TTRCalculatorActivity.class);
        startActivity(target);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (MyApplication.getPoints() < 0) {
            Intent target = new Intent(this, EnterTTRActivity.class);
            startActivity(target);
        }
    }

    public void clublist(View view) {
        AsyncTask<String, Void, Integer> task = new ClubListAsyncTask(this, actualTTR);
        task.execute();
    }

    public void statistik(View view) {
        MyApplication.selectedPlayerName = null;
        AsyncTask<String, Void, Integer> task = new EventsAsyncTask(this, EventsActivity.class);
        task.execute();
    }

    public void liga(View view) {
        if (MyApplication.selectedVerband == null) {
            AsyncTask<String, Void, Integer> task = new LigenAsyncTask(this, LigaHomeActivity.class);
            task.execute();
        } else {
            Intent target = new Intent(this, LigaHomeActivity.class);
            startActivity(target);
        }
    }

    public void player_sim(View view) {
        AsyncTask<String, Void, Integer> task = new OwnClubListAsyncTask(this,
                SelectTeamPlayerActivity.class);
        task.execute();
    }

    public void search(View view) {
        Intent target = new Intent(this, SearchActivity.class);
        target.putExtra(SearchActivity.BACK_TO, EventsActivity.class);
        startActivity(target);
    }

    public void ligaranking(View view) {
        AsyncTask<String, Void, Integer> task = new LigaRanglisteAsyncTask(this);
        task.execute();
    }

    public void info(View view) {
        AboutDialog about = new AboutDialog(this);
        about.show();
    }

    public void tournament(View view) {
        Intent target = new Intent(this, TournamentsActivity.class);
        startActivity(target);
    }

    public void cups(View view) {
        Intent target = new Intent(this, CupsActivity.class);
        startActivity(target);
    }

    public void team(View view) {
        Intent target = new Intent(this, TeamHomeActivity.class);
        startActivity(target);
    }



    class OwnClubListAsyncTask extends BaseAsyncTask {


        OwnClubListAsyncTask(Activity parent, Class targetClz) {
            super(parent, targetClz);
        }

        @Override
        protected void callParser() throws NoDataException, NetworkException, LoginExpiredException, ValidationException, NiceGuysException {
            MyApplication.myTeamPlayers = new MyTischtennisParser().readPlayersFromTeam(null);
        }

        @Override
        protected boolean dataLoaded() {
            return MyApplication.myTeamPlayers != null;
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        modifyMenu(menu);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.home_actions, menu);
        modifyMenu(menu);
        return true;
    }

    private void modifyMenu(Menu menu) {
        menu.getItem(1).setVisible(MyApplication.simPlayer != null);

        SubMenu subm = menu.getItem(0).getSubMenu(); // get my MenuItem with placeholder submenu
        subm.clear(); // delete place holder
        List<Favorite> list = favoriteManager.getFavorites();
        int id = 100;
//        subm.add(0, 99, Menu.NONE, "Mein Verein");
        for (Favorite favorite : list) {
            subm.add(0, id++, Menu.NONE, favorite.typeForMenu() + ": " + favorite.getName());
        }
        if (list.size() > 0) {
            subm.add(0, id, Menu.NONE, BEARBEITEN);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
        switch (item.getItemId()) {
            case R.id.menu_settings: {
                Intent intent = new Intent(this, MySettingsActivity.class);
                startActivity(intent);
                break;
            }
            case 99: {
                favoriteManager.startOwnVerein();
                break;
            }
//            case R.id.action_home: {
//                Intent intent = new Intent(this, HomeActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                break;
//            }
            case R.id.action_logout: {
                new LoginManager().logout();
                MyApplication.createEmptyUser();
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
                about.show();
                break;
            }
            case R.id.menu_impressum: {
                ImpressumDialog dialog = new ImpressumDialog(this);
                dialog.setTitle("Impressum");
                dialog.show();
                break;
            }
//            case R.id.menu_version: {
//                VersionCheckDialog dialog = new VersionCheckDialog(this);
//                dialog.setTitle("Versionsprüfung");
//                dialog.show();
//                break;
//            }
        }

         */
        if (item.getItemId() > 99)
            favoriteManager.startFavorite(item.getItemId() - 100);
        if (item.getTitle().equals(BEARBEITEN)) {
            favoriteEdit();
        }
        return false;
    }

    private void favoriteEdit() {
        Intent target = new Intent(this, EditFavoritesActivity.class);
        startActivity(target);
    }
}
