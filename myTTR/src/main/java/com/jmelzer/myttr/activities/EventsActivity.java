package com.jmelzer.myttr.activities;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.tasks.Head2HeadAsyncTask;

import java.lang.reflect.Method;

/**
 * Created by J. Melzer on 19.05.2015.
 */
public class EventsActivity extends BaseActivity {
    EventsTabsPagerAdapter eventsTabsPagerAdapter;
    FavoriteManager favoriteManager;

    @Override
    protected boolean checkIfNeccessryDataIsAvaible() {
        return true;


    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liga_mannschaft_detail);

        favoriteManager = new FavoriteManager(this, getApplicationContext());

        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        eventsTabsPagerAdapter =
                new EventsTabsPagerAdapter(
                        getSupportFragmentManager(), this);
        final ViewPager viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(eventsTabsPagerAdapter);

        forceTabs();
        final ActionBar actionBar = getActionBar();
        //  on swiping the viewpager make respective tab selected
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
//                configList(position == 0);
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // hide the given tab
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // probably ignore this event
            }
        };
        actionBar.addTab(actionBar.newTab().setText("Statistiken").setTabListener(tabListener), 0);
        actionBar.addTab(actionBar.newTab().setText("Chart").setTabListener(tabListener), 1);
    }

    // This is where the magic happens!
    public void forceTabs() {
        try {
            final ActionBar actionBar = getActionBar();
            final Method setHasEmbeddedTabsMethod = actionBar.getClass()
                    .getDeclaredMethod("setHasEmbeddedTabs", boolean.class);
            setHasEmbeddedTabsMethod.setAccessible(true);
            setHasEmbeddedTabsMethod.invoke(actionBar, false);
        } catch (final Exception e) {
            // Handle issues as needed: log, warn user, fallback etc
            // This error is safe to ignore, standard tabs will appear.
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.events_actions, menu);
        return true;
    }

    public void favorite(MenuItem item) {
        favoriteManager.favorite(MyApplication.selectedPlayer);
    }


    public void head2head(MenuItem item) {
        new Head2HeadAsyncTask(this, MyApplication.selectedPlayer.getPersonId(), Head2HeadActivity.class).execute();
    }
}
