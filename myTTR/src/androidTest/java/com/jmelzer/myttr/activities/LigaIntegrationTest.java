package com.jmelzer.myttr.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.jmelzer.myttr.Bezirk;
import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.Kreis;
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
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.jmelzer.myttr.activities.TestHelper.login;
import static com.jmelzer.myttr.activities.TestHelper.setUpIT;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.anything;
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

    @Before
    public void setUp() {
      setUpIT();
    }

    @Test
    public void testAll() throws Exception {
        matchToolbarTitle("myTTR");

        login();

        ligaSelect1KK();

        ligaMannschaftResultsActivity();
    }

    private void ligaMannschaftResultsActivity() {

        onView(withText("LigaTabelleActivity"));
        onView(withText(MANNSCHAFT_TO_CLICK)).perform(click());

        // LigaMannschaftResultsActivity
        onView(withText(MANNSCHAFT_TO_CLICK + " - Ergebnisse"));

        onData(anything())
                .inAdapterView(allOf(withId(R.id.liga_mannschaft_detail_row), isCompletelyDisplayed()))
                .atPosition(0).perform(click());

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
//        Thread.sleep(100000);
        matchToolbarTitle("Spielbericht");

    }



    private void ligaSelect1KK() {
        onView(withId(R.id.imageButton6)).perform(click());

        onView(withText("TTBL"));

        selectSpinnerItem(R.id.spinner_verband, new Verband("Westdeutscher TTV"));
        selectSpinnerItem(R.id.spinner_bezirk, new Bezirk("Bezirk Mittelrhein"));
        selectSpinnerItem(R.id.spinner_kreise, new Kreis("Rhein-Sieg"));
        selectSpinnerItem(R.id.kategorie_spinner, "Herren");

        onView(withText("1. Kreisklasse 1")).perform(click());
//
//        assertTrue(solo.searchText("Kreisliga"));
    }

    private void selectSpinnerItem(int id, Object obj) {
        onView(withId(id)).perform(click());
        DataInteraction d = onData(allOf(is(instanceOf(obj.getClass())), is(obj)));
        d.perform(click());
    }

    private static void matchToolbarTitle(String title) {
        onView(withText(title)).check(matches(isDisplayed()));
    }
}
