package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.Mannschaftspiel;
import com.jmelzer.myttr.MockResponses;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.db.FavoriteLigaDataBaseAdapter;
import com.jmelzer.myttr.db.LoginDataBaseAdapter;
import com.robotium.solo.Solo;

/**
 * Simulate the workflow of the app
 */
public class LigaIntegrationTest extends BaseActivityInstrumentationTestCase<LoginActivity> {

    private static final int STANDARD_TIMEOUT = 50000;
    public static final String MANNSCHAFT_TO_CLICK = "TTG St. Augustin III";
    private Solo solo;

    public LigaIntegrationTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void prepareMocks() {
        super.prepareMocks();
        MockResponses.forRequestDoAnswer(".*championship=DTTB.14/15", "clicktt/dttb_ligen_plan.htm");
        MockResponses.forRequestDoAnswer(".*championship=WTTV.*14/15", "clicktt/wttv_ligen_plan.htm");
        MockResponses.forRequestDoAnswer(".*meeting=7371117.*", "clicktt/mannschafts_detail_kreisliga.htm");
        MockResponses.forRequestDoAnswer(".*championship=B15.14%2F15", "clicktt/mittelrhein_ligen_plan.htm");
        MockResponses.forRequestDoAnswer(".*displayTyp=vorrunde.*", "clicktt/kreisliga_vorrunde.htm");
        MockResponses.forRequestDoAnswer(".*displayTyp=rueckrunde.*", "clicktt/kreisliga_vorrunde.htm");
        MockResponses.forRequestDoAnswer(".*teamPortrait.teamtable=1831640.*", "clicktt/mannschafts_info.htm");
        MockResponses.forRequestDoAnswer(".*meeting=7371089.*", "clicktt/mannschafts_spiel_7371089.htm");
        MockResponses.forRequestDoAnswer(".*championship=K156.14%2F15", "clicktt/rhein_sieg_ligen_plan.htm");
        MockResponses.forRequestDoAnswer(".*championship=K156.14%2F15&group=225345", "clicktt/kreisliga.htm");
        MockResponses.forRequestDoAnswer("http://www.mytischtennis.de/community/events", "events.htm");
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
//        assertEquals("", MyApplication.getLoginUser().getUsername());

        assertTrue(solo.waitForActivity(LoginActivity.class));
        Activity loginActivity = solo.getCurrentActivity();
        final EditText loginTxt = (EditText) solo.getView(R.id.username);
        loginActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loginTxt.setText("chokdee");
            }
        });
        final EditText pwTxt = (EditText) solo.getView(R.id.password);
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

        ligaHome();

        solo.clickOnText("Kreisliga");
        ligaMannschaftResultsActivity();
//        //we have 3 actions here after that
        mannschaftsInfo();

        solo.goBack();
        mannschaftsBilanzen();

        solo.goBack();
        spielbericht();

        solo.goBack();
        solo.goBack();
        favorite();
    }

    private void favorite() throws InterruptedException {
        //lets prepare the database to have the same setup everytime
        FavoriteLigaDataBaseAdapter adapter = new FavoriteLigaDataBaseAdapter(MyApplication.getAppContext());
        adapter.open();
        adapter.deleteAllEntries();

        assertTrue(solo.waitForActivity(LigaTabelleActivity.class, STANDARD_TIMEOUT));
        solo.clickOnActionBarItem(R.id.action_info);
        assertTrue(solo.waitForText(solo.getString(R.string.favorite_added)));

        //one entry in the db
        assertEquals(1, adapter.getAllEntries().size());


        solo.clickOnActionBarItem(R.id.action_info);
        assertTrue("we cannot add the same liga twice", solo.waitForText(solo.getString(R.string.favorite_exists)));
        //one entry in the db
        assertEquals(1, adapter.getAllEntries().size());

        solo.goBack();
        assertTrue(solo.waitForActivity(LigaHomeActivity.class, STANDARD_TIMEOUT));
        solo.clickOnActionBarItem(1);
        //ok try to do remove it,
        assertTrue(solo.waitForActivity(EditFavoritesActivity.class, STANDARD_TIMEOUT));
        Thread.sleep(1000);
        assertTrue(solo.searchText("Kreisliga"));
        solo.clickOnImage(0);

        Log.d(Constants.LOG_TAG, "sleeping cause event is asynchron");
        Thread.sleep(1000);

        //one entry in the db
        adapter = new FavoriteLigaDataBaseAdapter(MyApplication.getAppContext());
        adapter.open();
        assertEquals(0, adapter.getAllEntries().size());
    }

    private void spielbericht() {
        solo.clickInList(0); //first game
        assertTrue(solo.waitForActivity(LigaSpielberichtActivity.class, STANDARD_TIMEOUT));
        assertTrue("Mannschaftsname must be shown", solo.searchText(MANNSCHAFT_TO_CLICK));
        assertTrue("Jens must be shown", solo.searchText("Pohl"));
        assertTrue("3:0 must be shown?", solo.searchText("3:0"));

    }

    private void mannschaftsBilanzen() {
        solo.clickOnActionBarItem(R.id.action_bilanz);
        assertTrue(solo.waitForActivity(LigaMannschaftBilanzActivity.class, STANDARD_TIMEOUT));
        assertTrue("Thorsten must be shown", solo.searchText("Kopp"));
        assertTrue("Jens must be shown", solo.searchText("Pohl"));
        solo.clickOnText("Pohl");
        assertTrue("Label must be show", solo.searchText("Einsätze"));
    }

    private void mannschaftsInfo() {
        solo.clickOnActionBarItem(R.id.action_info);
        assertTrue(solo.waitForActivity(LigaMannschaftInfoActivity.class, STANDARD_TIMEOUT));
        assertTrue("Mannschaftsfueher must be shown", solo.searchText("Panknin"));
        assertTrue("Spiellokal must be shown", solo.searchText("Menden"));
    }

    private void ligaMannschaftResultsActivity() {
        assertTrue(solo.waitForActivity(LigaTabelleActivity.class, STANDARD_TIMEOUT));
        assertTrue(solo.searchText(MANNSCHAFT_TO_CLICK));
        solo.clickOnText(MANNSCHAFT_TO_CLICK);

        assertTrue(solo.waitForActivity(LigaMannschaftResultsActivity.class, STANDARD_TIMEOUT));
        ListView listView = (ListView) solo.getView(R.id.liga_mannschaft_detail_row);
        ListAdapter listAdapter = listView.getAdapter();
        assertTrue(listAdapter.getCount() > 0);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            Mannschaftspiel spiel = (Mannschaftspiel) listAdapter.getItem(i);

            assertTrue("Only " + MANNSCHAFT_TO_CLICK + " shall be in the list",
                    spiel.getGastMannschaft().getName().equals(MANNSCHAFT_TO_CLICK) ||
                            spiel.getHeimMannschaft().getName().equals(MANNSCHAFT_TO_CLICK));
        }

        LigaMannschaftResultsActivity activity = (LigaMannschaftResultsActivity) solo.getCurrentActivity();
        if (activity.startWithRR()) {
            solo.scrollToSide(Solo.LEFT);
        } else {
            solo.scrollToSide(Solo.RIGHT);
        }
        listView = (ListView) solo.getView(R.id.liga_mannschaft_detail_row);
        listAdapter = listView.getAdapter();
        assertTrue(listAdapter.getCount() > 0);

        //go back
        if (activity.startWithRR()) {
            solo.scrollToSide(Solo.RIGHT);
        } else {
            solo.scrollToSide(Solo.LEFT);
        }


    }

    private void ligaHome() throws InterruptedException {
        solo.clickOnButton(solo.getString(R.string.liga));
        assertTrue(solo.waitForActivity(LigaHomeActivity.class, 50000));
        assertEquals(LigaHomeActivity.class, solo.getCurrentActivity().getClass());
        Thread.sleep(1000);
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
