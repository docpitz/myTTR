package com.jmelzer.myttr.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
            LigaMannschaftResultsFragment f =  new LigaMannschaftResultsFragment();
            f.setPos(0);
            return f;
		case 1:
            f =  new LigaMannschaftResultsFragment();
            f.setPos(1);
            return f;
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 2;
	}

}
