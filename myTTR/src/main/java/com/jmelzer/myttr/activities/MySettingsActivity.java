/*
 * Copyright (c) Juergen Melzer
 *
 * 2014.
 */

/*
* Author: J. Melzer
* Date: 22.02.14 
*
*/


package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.logic.SyncManager;

public class MySettingsActivity extends Activity {

    public static final String KEY_PREF_SAVE_USER = "SAVE_USER";
    public static final String KEY_PREF_SYNC_TTR = "SYNC_TTR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                                                        new SettingsFragment()).commit();


    }


    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

//            CheckBoxPreference pref = (CheckBoxPreference) findPreference(KEY_PREF_SYNC_TTR);
//            pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//                @Override
//                public boolean onPreferenceChange(Preference preference, Object newValue) {
//                    Log.i(Constants.LOG_TAG, "preferenced changed");
//                    new SyncManager().switchSync((Boolean)newValue);
//                    return true;
//                }
//            });
        }
    }
}
