package com.jmelzer.myttr.activities;

import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.MockResponses;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Player;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.db.LoginDataBaseAdapter;
import com.robotium.solo.Solo;

import java.util.ArrayList;

/**
 * Simulate the workflow of the app
 */
public class IntegrationTest extends BaseActivityInstrumentationTestCase<LoginActivity> {

    public IntegrationTest() {
        super(LoginActivity.class);
    }


    @Override
    protected void prepareMocks() {
        super.prepareMocks();
        MockResponses.forRequestDoAnswer(".*showclubinfo.*", "showclubinfo.htm");
        MockResponses.forRequestDoAnswer(".*vereinid=3147.*", "ranking_verein.htm");
        MockResponses.forRequestDoAnswer(".*vorname=Timo&nachname=Boll.*", "search_boll.htm");
        MockResponses.forRequestDoAnswer(".*vorname=Marco&nachname=Vester.*", "search_vester.htm");
        MockResponses.forRequestDoAnswer(".*teamId=1409109", "team_id_1409109.htm");
        MockResponses.forRequestDoAnswer(".*personId=457126", "person_457126.htm");
        MockResponses.forRequestDoAnswer(".*personId=249366", "person_249366.htm");
        MockResponses.forRequestDoAnswer(".*personId=289406", "person_289406.htm");
        MockResponses.forRequestDoAnswer(".*personId=150716", "person_150716.htm");
        MockResponses.forRequestDoAnswer(".*personId=17741", "person_17741.htm");
        MockResponses.forRequestDoAnswer(".*personId=980671", "person_980671.htm");
        MockResponses.forRequestDoAnswer(".*vereinId=153027.*", "vereinid_153027.htm");
        MockResponses.forRequestDoAnswer(".*vereinId=156012.*", "ranking_verein.htm");
        MockResponses.forRequestDoAnswer(".*eventId=302352639", "eventid_302352639.htm");
        MockResponses.forRequestDoAnswer(".*eventId=62679901", "eventid_62679901.htm");
        MockResponses.forRequestDoAnswer(".*eventId=301876029", "eventid_301876029.htm");
        MockResponses.forRequestDoAnswer(".*vereinId=141046.*", "vereinid_141046.htm");
        MockResponses.forRequestDoAnswer(".*teamplayers.*", "teamplayers.htm");
        MockResponses.forRequestDoAnswer(".*/events", "events.htm");
    }

    @MediumTest
    public void testAll() throws InterruptedException {
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            }
        });
        //todo fixme
//        automaticLogin();
//
        login();
        assertTrue(waitForActivity(HomeActivity.class));
        homeButton();
        preferences();
        TTR();
        gotoHome();
        clubList();
        gotoHome();
        ownStatistics();
        gotoHome();
        search();
        gotoHome();
        playerSimulation();
        gotoHome();
        logOut();
    }


    private void gotoHome() {

        //solo.clickOnActionBarItem(R.id.action_home);
        View homeView = solo.getView(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.id.home : R.id.home);
        solo.clickOnView(homeView);
    }

    private void search() {
        solo.clickOnButton(solo.getString(R.string.search));
        assertTrue(waitForActivity(SearchActivity.class));
        solo.enterText(2, "Borussia Düsseldorf");
        solo.clickOnText(solo.getString(R.string.detail_search));
        assertTrue(waitForActivity(SearchResultActivity.class));
        solo.clickInList(0);
        assertTrue(waitForActivity(EventsActivity.class));
        assertTrue(solo.searchText("Boll"));
        solo.clickInList(3);
        assertTrue(waitForActivity(EventDetailActivity.class));
        solo.clickInList(0);
        assertTrue(waitForActivity(EventsActivity.class));

    }

    private void automaticLogin() {


        assertTrue(waitForActivity(HomeActivity.class));
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
        assertTrue(waitForActivity(LoginActivity.class));

    }

    private void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
//            e.printStackTrace();
        }
    }

    private void logOut() {
        gotoHome();
        assertTrue(waitForActivity(HomeActivity.class));
        solo.clickOnActionBarItem(R.id.action_logout);
        assertTrue(waitForActivity(LoginActivity.class));
    }

    private void playerSimulation() {
        solo.clickOnButton(solo.getString(R.string.player_sim));
        assertTrue(waitForActivity(SelectTeamPlayerActivity.class));

        assertTrue(solo.searchText("Schmidt"));
        solo.clickInList(7);

        assertTrue(waitForActivity(HomeActivity.class));
        assertTrue("Manni is selected as player", solo.searchText("Manfred"));
        assertNotNull(MyApplication.simPlayer);
        assertTrue(MyApplication.simPlayer.getTtrPoints() > 0);

        solo.clickOnText(solo.getString(R.string.enter_manual));
        assertTrue(waitForActivity(TTRCalculatorActivity.class));
        //todo write own test here
//        manualEntry();
        gotoHome();

        solo.clickOnButton(solo.getString(R.string.player_sim));
        assertTrue(waitForActivity(SelectTeamPlayerActivity.class));
        solo.clickOnButton(solo.getString(R.string.loadplayerfromclub));
        assertTrue(waitForActivity(SearchResultActivity.class));

        assertTrue("Marco must be found", solo.searchText("Vester"));

        solo.clickInList(0);
        assertTrue(waitForActivity(HomeActivity.class));
        assertTrue("Marco is selected as player", solo.searchText("Marco"));

        solo.clickOnActionBarItem(R.id.action_remove_sim);
        assertTrue(solo.waitForText("wurde beendet"));

        assertFalse("No one is selected ", solo.searchText("Manfred"));


    }

    private void ownStatistics() {
        solo.clickOnButton(solo.getString(R.string.statistik));
        assertTrue(waitForActivity(EventsActivity.class));
        solo.clickInList(1);
        assertTrue(waitForActivity(EventDetailActivity.class));

        gotoHome();
        assertTrue(waitForActivity(HomeActivity.class));
    }

    private void clubList() {
        solo.clickOnButton(solo.getString(R.string.clublist));
        assertTrue(waitForActivity(ClubListActivity.class));
        solo.clickInList(0);
        assertTrue(waitForActivity(EventsActivity.class));
        assertTrue(solo.searchText("Statistiken für den Spieler"));

        solo.scrollToSide(Solo.RIGHT);
        assertTrue("Chart must be shown", solo.searchText("Chart für den Spieler"));
        solo.scrollToSide(Solo.LEFT);
        assertTrue(solo.searchText("Statistiken für den Spieler"));

        solo.clickInList(0);
        assertTrue(waitForActivity(EventDetailActivity.class));

        assertTrue(solo.searchText("11 : ") || solo.searchText(" : 11"));
        assertFalse(solo.searchText("div"));

        solo.clickInList(0);
        assertTrue(waitForActivity(EventsActivity.class));

        gotoHome();
        assertTrue(waitForActivity(HomeActivity.class));

    }

    private void homeButton() {
        assertActivity(HomeActivity.class);
        ArrayList<View> views = solo.getViews();
        for (View view : views) {
            Log.d(Constants.LOG_TAG, view.toString());
        }
        solo.clickOnButton(solo.getString(R.string.clublist));
        assertActivity(ClubListActivity.class);
        gotoHome();
        assertActivity(HomeActivity.class);

    }

    private void TTR() throws InterruptedException {

        solo.clickOnText(solo.getString(R.string.enter_manual));
        assertTrue(waitForActivity(TTRCalculatorActivity.class));

        emptyTTRList();
        manualEntry();
        appointments();
    }

    private void appointments() {
        MyApplication.manualClub = "TTG St. Augustin";
        assertNull(MyApplication.teamAppointments);
        solo.clickOnView(solo.getView(R.id.next_appointments_search));
        assertTrue(waitForActivity(NextAppointmentsActivity.class));
        assertNotNull(MyApplication.teamAppointments);

        //select first appointment
        solo.clickInList(0);
        assertTrue(waitForActivity(NextAppointmentPlayersActivity.class));

        solo.clickOnCheckBox(0);
        solo.clickOnCheckBox(1);

        solo.clickOnButton(solo.getString(R.string.select));
        assertTrue(waitForActivity(TTRCalculatorActivity.class));

        assertEquals("2 selected", 2, MyApplication.getPlayers().size());

        solo.clickOnImageButton(0);
        solo.waitForText(solo.getString(R.string.player_removed_from_list));
        solo.clickOnImageButton(0);
        solo.waitForText(solo.getString(R.string.player_removed_from_list));

        assertEquals("all removed", 0, MyApplication.getPlayers().size());

        solo.clickOnView(solo.getView(R.id.next_appointments_search));
        assertTrue(waitForActivity(NextAppointmentsActivity.class));
        assertNotNull(MyApplication.teamAppointments);

        //select first appointment
        solo.clickInList(0);
        assertTrue(waitForActivity(NextAppointmentPlayersActivity.class));
        solo.clickOnButton(solo.getString(R.string.loadplayerfromclub));
        solo.clickInList(3);
        assertTrue(waitForActivity(TTRCalculatorActivity.class));

        assertEquals("one selected", 1, MyApplication.getPlayers().size());
        solo.clickOnImageButton(0);
        solo.waitForText(solo.getString(R.string.player_removed_from_list));
        assertEquals("none selected", 0, MyApplication.getPlayers().size());
    }

    private void emptyTTRList() {
        solo.clickOnText("Berechnen");
        assertTrue("no entries in the list", solo.waitForText(solo.getString(R.string.select_player_first)));

        assertNull(MyApplication.actualPlayer);
    }

    private void manualEntry() {
        assertEquals(0, MyApplication.getPlayers().size());

        View homeView = solo.getView(R.id.action_new_player);
        solo.clickOnView(homeView);
//        solo.clickOnText(solo.getString(R.string.btn_new_player));
        assertTrue(waitForActivity(SearchActivity.class));

        assertEquals("", solo.getEditText(0).getText().toString());
        assertEquals("", solo.getEditText(1).getText().toString());
        assertEquals("", solo.getEditText(2).getText().toString());

        emptyName();


        //vor und nachname
        solo.getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                solo.getEditText(0).setText("Timo");
                solo.getEditText(1).setText("Boll");
            }
        });


        solo.clickOnText(solo.getString(R.string.detail_search));

        assertTrue(waitForActivity(SearchResultActivity.class));

        assertNotNull(solo.getCurrentActivity());

        assertTrue("Timo must be found", solo.searchText("Timo"));
        assertTrue("Bollmann must be found", solo.searchText("Bollmann"));

        //test case, we get NULLs in list
        solo.goBack();

        assertTrue(waitForActivity(SearchActivity.class));

        solo.goBack();


        //back to calc activity
        assertTrue(waitForActivity(TTRCalculatorActivity.class));
        assertEquals(0, MyApplication.getPlayers().size());

        solo.clickOnView(solo.getView(R.id.action_new_player));

        assertTrue(waitForActivity(SearchActivity.class));

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

        assertTrue(waitForActivity(SearchResultActivity.class));

        solo.clickInList(3); //real Timo

        assertTrue(waitForActivity(TTRCalculatorActivity.class));

        assertEquals(1, MyApplication.getPlayers().size());
        Player p = MyApplication.getPlayers().get(0);
        assertEquals("Timo", p.getFirstname());
        assertEquals("Boll", p.getLastname());
        assertEquals("Borussia Düsseldorf", p.getClub());


        //test exact player
        solo.clickOnView(solo.getView(R.id.action_new_player));
        assertTrue(waitForActivity(SearchActivity.class));

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
        assertTrue(waitForActivity(TTRCalculatorActivity.class));
        assertEquals(2, MyApplication.getPlayers().size());

        p = MyApplication.getPlayers().get(1);
        assertEquals("Marco", p.getFirstname());
        assertEquals("Vester", p.getLastname());
        assertEquals("TTG St. Augustin", p.getClub());

        solo.clickOnText(solo.getString(R.string.calc));
        assertTrue(waitForActivity(ResultActivity.class));
        assertTrue(solo.searchText("Deine Punkte sind gleich geblieben"));
        solo.goBack();

        solo.clickOnCheckBox(0);
        solo.clickOnCheckBox(1);
        solo.clickOnText(solo.getString(R.string.calc));
        assertTrue(waitForActivity(ResultActivity.class));
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

    private void emptyName() {
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

    private void preferences() {
//        solo.sendKey(Solo.MENU);
//        solo.sendKey(KeyEvent.KEYCODE_MENU);
//        sleep(1000); //emulator is very slow
        solo.clickOnMenuItem(solo.getString(R.string.menu_settings));
        sleep(2000); //emulator is very slow
        solo.clickOnText(solo.getString(R.string.enter_clubname));
        solo.clearEditText(0);
        assertTrue(solo.searchText(""));
        solo.enterText(0, "TTG St. Augus");
        solo.clickOnButton("OK");
        assertTrue(solo.searchText("TTG St. Augustin"));
        solo.clickOnText("TTG St. Augustin");
        solo.clickOnButton("OK");

        assertTrue(waitForActivity(HomeActivity.class));
        assertEquals("TTG St. Augustin", MyApplication.manualClub);


    }
}
