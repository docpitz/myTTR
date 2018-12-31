package com.jmelzer.myttr.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.db.LoginDataBaseAdapter;
import com.jmelzer.myttr.logic.SyncManager;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestHelper {

    public static void setUpIT() {
        SyncManager.testIsRun = true;

        Context context = getInstrumentation().getTargetContext();
        LoginDataBaseAdapter adapter = new LoginDataBaseAdapter(context);
        adapter.open();
        adapter.deleteAllEntries();

        //prepare the db and prefs to automatic

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPref.edit().putBoolean(MySettingsActivity.KEY_PREF_SAVE_USER, false).commit();
        sharedPref.edit().putInt(MySettingsActivity.KEY_PREF_TIMER, 10).commit();


    }

    static public void login() {
        onView(withId(R.id.username)).perform(typeText("chokdee"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("fuckyou123"), closeSoftKeyboard());

        onView(withId(R.id.button_login)).perform(click());
        Log.d(Constants.LOG_TAG, "after login");

        assertNotNull(MyApplication.getLoginUser());
        assertEquals("chokdee", MyApplication.getLoginUser().getUsername());
    }

    public static String readFile(String file) throws IOException {
        InputStream in = TestHelper.class.getClassLoader().getResourceAsStream(file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }

        return StringEscapeUtils.unescapeHtml4(stringBuilder.toString());
    }
}
