package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.db.LoginDataBaseAdapter;
import com.robotium.solo.Solo;

/**
 * Simulate the workflow of the app
 */
public class LigaIntegrationTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    private static final int STANDARD_TIMEOUT = 50000;
    public static final String MANNSCHAFT_TO_CLICK = "TTG St. Augustin III";
    private Solo solo;

    public LigaIntegrationTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(false);
        Context context = getInstrumentation().getTargetContext();
        LoginDataBaseAdapter adapter = new LoginDataBaseAdapter(context);
        adapter.open();
        adapter.deleteAllEntries();

        //prepare the db and prefs to automatic

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPref.edit().putBoolean(MySettingsActivity.KEY_PREF_SAVE_USER, false).commit();

//        adapter.insertEntry("Ich bins", "chokdee", "fuckyou123",
//                2000, "TTG St. Augustin", 16);

        solo = new Solo(getInstrumentation(), getActivity());


    }
    private void login() {
        assertEquals("", MyApplication.getLoginUser().getUsername());

        Activity loginActivity = solo.getCurrentActivity();
        final EditText loginTxt = (EditText) loginActivity.findViewById(R.id.username);
        loginActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loginTxt.setText("chokdee");
            }
        });
        final EditText pwTxt = (EditText) loginActivity.findViewById(R.id.password);
        loginActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pwTxt.setText("fuckyou123");
            }
        });


        solo.clickOnButton(0);

        assertTrue(solo.waitForActivity(HomeActivity.class, 40000));

        assertNotNull(MyApplication.getLoginUser());
        assertEquals("chokdee", MyApplication.getLoginUser().getUsername());
    }


    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    @MediumTest
    public void testAll() throws InterruptedException {

        assertEquals(LoginActivity.class, solo.getCurrentActivity().getClass());

        login();

        testLigaHome();

        solo.clickOnText("Kreisliga");
        assertTrue(solo.waitForActivity(LigaTabelleActivity.class, STANDARD_TIMEOUT));
        assertTrue(solo.searchText(MANNSCHAFT_TO_CLICK));
        solo.clickOnText(MANNSCHAFT_TO_CLICK);

        assertTrue(solo.waitForActivity(LigaMannschaftResultsActivity.class, STANDARD_TIMEOUT));
    }

    private void testLigaHome() {
        solo.clickOnButton(solo.getString(R.string.liga));
        assertTrue(solo.waitForActivity(LigaHomeActivity.class, 5000));
        assertEquals(LigaHomeActivity.class, solo.getCurrentActivity().getClass());

        assertTrue(solo.searchText("1. Bundesliga"));

        selectSpinnerItem(R.id.spinner_verband, "Westdeutscher TTV");
        selectSpinnerItem(R.id.spinner_bezirk, "Mittelrhein");
        selectSpinnerItem(R.id.spinner_kreise, "Rhein-Sieg");
        selectSpinnerItem(R.id.kategorie_spinner, "Herren");

        assertTrue(solo.searchText("Kreisliga"));
    }

    private void selectSpinnerItem(int id, String textToSelect) {
        solo.clickOnView(solo.getView(id));
        solo.clickOnText(textToSelect);
        assertTrue(solo.searchText(textToSelect));
    }

}
