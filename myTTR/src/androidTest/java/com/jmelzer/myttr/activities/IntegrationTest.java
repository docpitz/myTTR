package com.jmelzer.myttr.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;
import android.widget.EditText;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.R;
import com.robotium.solo.Solo;

/**
 * Simulate the workflow of the app
 */
public class IntegrationTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    LoginActivity loginActivity;
    private Solo solo;

    public IntegrationTest() {
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
        sharedPref.edit().putBoolean(MySettingsActivity.KEY_PREF_SAVE_USER, true).commit();

        adapter.insertEntry("testi", "chokdee", "fuckyou123",
                2000, "TTG St. Augustin", 16);

        loginActivity = getActivity();
        solo = new Solo(getInstrumentation(), getActivity());


    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    @MediumTest
    public void testit() throws InterruptedException {
//        testAutomaticLogin();

//        login();
        assertTrue(solo.waitForActivity(HomeActivity.class, 40000));
//        testHomeButton();
        testPreferences();
        testTTR();
        gotoHome();
        testClubList();
        gotoHome();
        testOwnStatistics();
        gotoHome();
        testSearch();
        gotoHome();
        testPlayerSimulation();
        testLogOut();
    }

    private void login() {
        assertEquals("", MyApplication.getLoginUser().getUsername());

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

    private void gotoHome() {

        //solo.clickOnActionBarItem(R.id.action_home);
        View homeView = solo.getView(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.id.home : R.id.home);
        solo.clickOnView(homeView);
    }

    private void testSearch() {
        solo.clickOnButton(solo.getString(R.string.search));
        assertTrue(solo.waitForActivity(SearchActivity.class, 5000));
        solo.enterText(2, "Borussia Düsseldorf");
        solo.clickOnText(solo.getString(R.string.detail_search));
        assertTrue(solo.waitForActivity(SearchResultActivity.class, 50000));
        solo.clickLongInList(0);
        assertTrue(solo.waitForActivity(EventsActivity.class, 5000));
        assertTrue(solo.searchText("Boll"));
        solo.clickLongInList(3);
        assertTrue(solo.waitForActivity(EventDetailActivity.class, 5000));
        solo.clickLongInList(0);
        assertTrue(solo.waitForActivity(EventsActivity.class, 5000));

    }

    private void testAutomaticLogin() {


        assertTrue(solo.waitForActivity(HomeActivity.class, 40000));
        //automatic login must be sucessfull

        solo.sendKey(Solo.MENU);
        solo.clickOnText(solo.getString(R.string.menu_settings));
        solo.clickOnCheckBox(0);
        sleep(1000);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(solo.getCurrentActivity().getApplicationContext());
        Boolean saveUser = sharedPref.getBoolean(MySettingsActivity.KEY_PREF_SAVE_USER, true);
        assertFalse(saveUser);

        //entries in the dn must be deleted
        LoginDataBaseAdapter adapter = new LoginDataBaseAdapter(solo.getCurrentActivity().getApplicationContext());
        assertNull(adapter.getSinlgeEntry());

        solo.sendKey(Solo.MENU);
        solo.clickOnText(solo.getString(R.string.menu_settings));
        solo.clickOnCheckBox(0);

        sleep(1000);
        saveUser = sharedPref.getBoolean(MySettingsActivity.KEY_PREF_SAVE_USER, true);
        assertTrue(saveUser);

        adapter = new LoginDataBaseAdapter(solo.getCurrentActivity().getApplicationContext());
        assertNotNull("db entry must be created", adapter.getSinlgeEntry());

        solo.goBack();

        solo.clickOnActionBarItem(R.id.action_logout);
        assertTrue(solo.waitForActivity(LoginActivity.class, 5000));

    }

    private void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
//            e.printStackTrace();
        }
    }

    private void testLogOut() {
        gotoHome();
        assertTrue(solo.waitForActivity(HomeActivity.class, 5000));
        solo.clickOnActionBarItem(R.id.action_logout);
        assertTrue(solo.waitForActivity(LoginActivity.class, 5000));
    }

    private void testPlayerSimulation() {
        solo.clickOnButton(solo.getString(R.string.player_sim));
        assertTrue(solo.waitForActivity(SelectTeamPlayerActivity.class, 50000));

        assertTrue(solo.searchText("Schmidt"));
        solo.clickLongInList(8);

        assertTrue(solo.waitForActivity(HomeActivity.class, 5000));
        assertTrue("Manni is selected as player", solo.searchText("Manfred"));
        assertNotNull(MyApplication.simPlayer);
        assertTrue(MyApplication.simPlayer.getTtrPoints() > 0);

        solo.clickOnText(solo.getString(R.string.enter_manual));
        assertTrue(solo.waitForActivity(TTRCalculatorActivity.class, 5000));
        //todo write own test here
//        testManualEntry();
        gotoHome();

        solo.clickOnActionBarItem(R.id.action_remove_sim);
        assertTrue(solo.waitForText("wurde beendet"));

        assertFalse("No one is selected ", solo.searchText("Manfred"));


    }

    private void testOwnStatistics() {
        solo.clickOnButton(solo.getString(R.string.statistik));
        assertTrue(solo.waitForActivity(EventsActivity.class, 50000));
        solo.clickLongInList(1);
        assertTrue(solo.waitForActivity(EventDetailActivity.class, 50000));

        gotoHome();
        assertTrue(solo.waitForActivity(HomeActivity.class, 50000));
    }

    private void testClubList() {
        solo.clickOnButton(solo.getString(R.string.clublist));
        assertTrue(solo.waitForActivity(ClubListActivity.class, 5000));
        solo.clickLongInList(0);
        assertTrue(solo.waitForActivity(EventsActivity.class, 5000));
        assertTrue(solo.searchText("Vester"));
        solo.clickLongInList(0);
        assertTrue(solo.waitForActivity(EventDetailActivity.class, 5000));

        assertTrue(solo.searchText("11 : ") || solo.searchText(" : 11"));
        assertFalse(solo.searchText("div"));

        solo.clickLongInList(0);
        assertTrue(solo.waitForActivity(EventsActivity.class, 5000));

        gotoHome();
        assertTrue(solo.waitForActivity(HomeActivity.class, 5000));

    }

    private void testHomeButton() {
        solo.clickOnButton(solo.getString(R.string.clublist));
        assertTrue(solo.waitForActivity(ClubListActivity.class, 50000));
        gotoHome();
        assertTrue(solo.waitForActivity(HomeActivity.class, 50000));

    }

    private void testTTR() throws InterruptedException {

        solo.clickOnText(solo.getString(R.string.enter_manual));
        assertTrue(solo.waitForActivity(TTRCalculatorActivity.class, 5000));

        testEmptyTTRList();
        testManualEntry();
        testAppointments();
    }

    private void testAppointments() {
        MyApplication.manualClub = "TTG St. Augustin";
        assertNull(MyApplication.teamAppointments);
        solo.clickOnView(solo.getView(R.id.next_appointments_search));
        assertTrue(solo.waitForActivity(NextAppointmentsActivity.class, 5000));
        assertNotNull(MyApplication.teamAppointments);

        //select first appointment
        solo.clickLongInList(0);
        assertTrue(solo.waitForActivity(NextAppointmentPlayersActivity.class, 5000));

        solo.clickOnCheckBox(0);
        solo.clickOnCheckBox(1);

        solo.clickOnButton(solo.getString(R.string.select));
        assertTrue(solo.waitForActivity(TTRCalculatorActivity.class, 5000));

        assertEquals("2 selected", 2, MyApplication.getPlayers().size());

        solo.clickOnImageButton(0);
        solo.waitForText(solo.getString(R.string.player_removed_from_list));
        solo.clickOnImageButton(0);
        solo.waitForText(solo.getString(R.string.player_removed_from_list));

        assertEquals("all removed", 0, MyApplication.getPlayers().size());

        solo.clickOnView(solo.getView(R.id.next_appointments_search));
        assertTrue(solo.waitForActivity(NextAppointmentsActivity.class, 5000));
        assertNotNull(MyApplication.teamAppointments);

        //select first appointment
        solo.clickLongInList(0);
        assertTrue(solo.waitForActivity(NextAppointmentPlayersActivity.class, 5000));
        solo.clickOnButton(solo.getString(R.string.loadplayerfromclub));
        solo.clickLongInList(3);
        assertTrue(solo.waitForActivity(TTRCalculatorActivity.class, 5000));

        assertEquals("one selected", 1, MyApplication.getPlayers().size());
        solo.clickOnImageButton(0);
        solo.waitForText(solo.getString(R.string.player_removed_from_list));
        assertEquals("none selected", 0, MyApplication.getPlayers().size());
    }

    private void testEmptyTTRList() {
        solo.clickOnText("Berechnen");
        assertTrue("no entries in the list", solo.waitForText(solo.getString(R.string.select_player_first)));

        assertNull(MyApplication.actualPlayer);
    }

    private void testManualEntry() {
        assertEquals(0, MyApplication.getPlayers().size());

        View homeView = solo.getView(R.id.action_new_player);
        solo.clickOnView(homeView);
//        solo.clickOnText(solo.getString(R.string.btn_new_player));
        assertTrue(solo.waitForActivity(SearchActivity.class, 20000));

        assertEquals("", solo.getEditText(0).getText().toString());
        assertEquals("", solo.getEditText(1).getText().toString());
        assertEquals("", solo.getEditText(2).getText().toString());

        testEmptyName();


        //vor und nachname
        solo.getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                solo.getEditText(0).setText("Timo");
                solo.getEditText(1).setText("Boll");
            }
        });


        solo.clickOnText(solo.getString(R.string.detail_search));

        assertTrue(solo.waitForActivity(SearchResultActivity.class, 20000));

        assertNotNull(solo.getCurrentActivity());

        assertTrue("Timo must be found", solo.searchText("Timo"));
        assertTrue("Bollmann must be found", solo.searchText("Bollmann"));

        //test case, we get NULLs in list
        solo.goBack();

        assertTrue(solo.waitForActivity(SearchActivity.class, 5000));

        solo.goBack();


        //back to calc activity
        assertTrue(solo.waitForActivity(TTRCalculatorActivity.class, 5000));
        assertEquals(0, MyApplication.getPlayers().size());

        solo.clickOnView(solo.getView(R.id.action_new_player));

        assertTrue(solo.waitForActivity(SearchActivity.class, 10000));

        assertTrue(solo.searchText("Vorname"));

        //vor und nachname
        solo.getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                solo.getEditText(0).setText("Timo");
                solo.getEditText(1).setText("Boll");
            }
        });

        solo.clickOnText(solo.getString(R.string.detail_search));

        assertTrue(solo.waitForActivity(SearchResultActivity.class, 20000));

        solo.clickLongInList(3); //real Timo

        assertTrue(solo.waitForActivity(TTRCalculatorActivity.class, 20000));

        assertEquals(1, MyApplication.getPlayers().size());
        Player p = MyApplication.getPlayers().get(0);
        assertEquals("Timo", p.getFirstname());
        assertEquals("Boll", p.getLastname());
        assertEquals("Borussia Düsseldorf", p.getClub());


        //test exact player
        solo.clickOnView(solo.getView(R.id.action_new_player));
        assertTrue(solo.waitForActivity(SearchActivity.class, 10000));

        assertTrue(solo.searchText("Vorname"));

        //vor und nachname
        solo.getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                solo.getEditText(0).setText("Marco");
                solo.getEditText(1).setText("Vester");
            }
        });
        solo.clickOnText(solo.getString(R.string.detail_search));
        assertTrue(solo.waitForActivity(TTRCalculatorActivity.class, 20000));
        assertEquals(2, MyApplication.getPlayers().size());

        p = MyApplication.getPlayers().get(1);
        assertEquals("Marco", p.getFirstname());
        assertEquals("Vester", p.getLastname());
        assertEquals("TTG St. Augustin", p.getClub());

        solo.clickOnText(solo.getString(R.string.calc));
        assertTrue(solo.waitForActivity(ResultActivity.class, 5000));
        assertTrue(solo.searchText("Deine Punkte sind gleich geblieben"));
        solo.goBack();

        solo.clickOnCheckBox(0);
        solo.clickOnCheckBox(1);
        solo.clickOnText(solo.getString(R.string.calc));
        assertTrue(solo.waitForActivity(ResultActivity.class, 5000));
        assertTrue(solo.searchText("32 Punkte dazu gewonnen"));

        solo.goBack();

        solo.clickOnImageButton(0);
        solo.waitForText(solo.getString(R.string.player_removed_from_list));
        assertEquals("1 must be deleted", 1, MyApplication.getPlayers().size());

        solo.clickOnImageButton(0);
        solo.waitForText(solo.getString(R.string.player_removed_from_list));
        assertEquals("all entries (2) must be deleted", 0, MyApplication.getPlayers().size());


//        solo.
//        solo.clickOnText();

    }

    private void testEmptyName() {
        //fill up only vorname
        solo.getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                solo.getEditText(0).setText("Timo");
            }
        });

        solo.clickOnText(solo.getString(R.string.detail_search));
        assertTrue("first name only no allowed", solo.waitForText(solo.getString(R.string.error_search_required_fields)));

        //fill up only nachname
        solo.getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                solo.getEditText(0).setText("");
                solo.getEditText(1).setText("Meier");
            }
        });
        solo.clickOnText(solo.getString(R.string.detail_search));
        assertTrue("last name only no allowed", solo.waitForText(solo.getString(R.string.error_search_required_fields)));
    }

    private void testPreferences() {

        solo.sendKey(Solo.MENU);
        solo.clickOnText(solo.getString(R.string.menu_settings));
        solo.clickOnText(solo.getString(R.string.enter_clubname));
        solo.clearEditText(0);
        assertTrue(solo.searchText(""));
        solo.enterText(0, "TTG St. Augus");
        solo.clickOnButton("OK");
        assertTrue(solo.searchText("TTG St. Augustin"));
        solo.clickOnText("TTG St. Augustin");
        solo.clickOnButton("OK");

        assertTrue(solo.waitForActivity(HomeActivity.class, 5000));
        assertEquals("TTG St. Augustin", MyApplication.manualClub);


    }
}
