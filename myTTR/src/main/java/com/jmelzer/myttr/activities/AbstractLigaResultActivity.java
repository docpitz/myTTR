package com.jmelzer.myttr.activities;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;

import java.lang.reflect.Method;

/**
 * Base class for liga results.
 * Created by J. Melzer on 21.02.2015.
 */
public abstract class AbstractLigaResultActivity extends BaseActivity {

    private ViewPager viewPager;


    protected void init() {

        final ActionBar actionBar = getActionBar();
        viewPager = (ViewPager) findViewById(R.id.pager);
        LigaTabsPagerAdapter mAdapter = createTabsAdapter();
        viewPager.setAdapter(mAdapter);

        forceTabs();
        /**
         * on swiping the viewpager make respective tab selected
         * */
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
        if (!hasGesamt()) {
            boolean b = startWithRR();
            actionBar.addTab(actionBar.newTab().setText("Vorrunde").setTabListener(tabListener), !b);
            actionBar.addTab(actionBar.newTab().setText("Rückrunde").setTabListener(tabListener), b);
        } else {
            actionBar.addTab(actionBar.newTab().setText("Gesamt").setTabListener(tabListener), true);
        }
    }

    abstract boolean startWithRR();
    boolean hasGesamt() {
        return MyApplication.getSelectedLiga().getUrlGesamt() != null;
    }

    abstract protected LigaTabsPagerAdapter createTabsAdapter();

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


}
