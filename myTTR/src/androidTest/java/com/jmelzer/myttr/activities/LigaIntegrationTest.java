package com.jmelzer.myttr.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.jmelzer.myttr.Bezirk;
import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.Kreis;
import com.jmelzer.myttr.Mannschaftspiel;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.Verband;
import com.jmelzer.myttr.db.LoginDataBaseAdapter;
import com.jmelzer.myttr.logic.SyncManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.DataInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Simulate the workflow of the app
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class LigaIntegrationTest {


    public static final String MANNSCHAFT_TO_CLICK = "TTG St. Augustin IV";
    public static final String PACKAGE_NAME = "com.jmelzer.myttr";
    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<>(
            LoginActivity.class);

//    public LigaIntegrationTest() {
//        super(LoginActivity.class);
//    }

    @Before
    public void setUp() throws Exception {
        SyncManager.testIsRun = true;
        Log.d(Constants.LOG_TAG, "------  starting test " + getClass().getCanonicalName() + " ----- ");

        Context context = getInstrumentation().getTargetContext();
        LoginDataBaseAdapter adapter = new LoginDataBaseAdapter(context);
        adapter.open();
        adapter.deleteAllEntries();

        //prepare the db and prefs to automatic

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPref.edit().putBoolean(MySettingsActivity.KEY_PREF_SAVE_USER, false).commit();
        sharedPref.edit().putInt(MySettingsActivity.KEY_PREF_TIMER, 10).commit();


    }

    @Test
    public void testAll() throws Exception {
        login();

        ligaHome();

        onView(withText("1. Kreisklasse 1")).perform(click());

        ligaMannschaftResultsActivity();
    }

    private void ligaMannschaftResultsActivity() {

        onView(withText("LigaTabelleActivity"));
        onView(withText(MANNSCHAFT_TO_CLICK));
        onView(withText(MANNSCHAFT_TO_CLICK)).perform(click());

//
        onView(withText(MANNSCHAFT_TO_CLICK + " - Ergebnisse"));

//        onData(allOf(is(instanceOf(Mannschaftspiel.class)), hasToString(MANNSCHAFT_TO_CLICK))).check();
        //prevent html
//        onData(allOf(is(instanceOf(Mannschaftspiel.class)), not(hasToString("<"))));
        onView(withId(R.id.liga_mannschaft_detail_row)).check(matches(withText(MANNSCHAFT_TO_CLICK)));
//
//        LigaMannschaftResultsActivity activity = (LigaMannschaftResultsActivity) solo.getCurrentActivity();
//        if (activity.startWithRR()) {
//            solo.scrollToSide(Solo.LEFT);
//        } else {
//            solo.scrollToSide(Solo.RIGHT);
//        }
//        listView = (ListView) solo.getView(R.id.liga_mannschaft_detail_row);
//        listAdapter = listView.getAdapter();
//        assertTrue(listAdapter.getCount() > 0);
//
//        //go back
//        if (activity.startWithRR()) {
//            solo.scrollToSide(Solo.RIGHT);
//        } else {
//            solo.scrollToSide(Solo.LEFT);
//        }


    }

    void login() {
        onView(withId(R.id.username)).perform(typeText("chokdee"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("fuckyou123"), closeSoftKeyboard());

        onView(withId(R.id.button_login)).perform(click());
        Log.d(Constants.LOG_TAG, "after login");

        assertNotNull(MyApplication.getLoginUser());
        assertEquals("chokdee", MyApplication.getLoginUser().getUsername());
    }

    private void ligaHome() throws InterruptedException {
        onView(withId(R.id.imageButton6)).perform(click());

        onView(withText("TTBL"));

        selectSpinnerItem(R.id.spinner_verband, new Verband("Westdeutscher TTV"));
        selectSpinnerItem(R.id.spinner_bezirk, new Bezirk("Bezirk Mittelrhein"));
        selectSpinnerItem(R.id.spinner_kreise, new Kreis("Rhein-Sieg"));
        selectSpinnerItem(R.id.kategorie_spinner, "Herren");

        onView(withText("1. Kreisklasse 1"));
//
//        assertTrue(solo.searchText("Kreisliga"));
    }

    private void selectSpinnerItem(int id, Object obj) {
        onView(withId(id)).perform(click());
        DataInteraction d = onData(allOf(is(instanceOf(obj.getClass())), is(obj)));
        d.perform(click());
    }

}
