package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.jmelzer.myttr.MockHttpClient;
import com.jmelzer.myttr.MockResponses;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.db.LoginDataBaseAdapter;
import com.jmelzer.myttr.logic.Client;
import com.jmelzer.myttr.logic.LoginManager;
import com.robotium.solo.Solo;

import org.apache.http.impl.cookie.BasicClientCookie;

/**
 * Created by J. Melzer on 01.05.2015.
 * Base class handle
 */
public abstract class BaseActivityInstrumentationTestCase<T extends Activity> extends ActivityInstrumentationTestCase2<T> {

    //switch wether we read html from file system or calling mytt.de
    boolean offline = true;

    protected static final int STANDARD_TIMEOUT = 50000;


    public BaseActivityInstrumentationTestCase(Class<T> activityClass) {
        super(activityClass);
    }

    protected MockHttpClient mockHttpClient;
    protected Solo solo;

    public boolean waitForActivity(Class<? extends Activity> activityClass) {
        return solo.waitForActivity(activityClass, STANDARD_TIMEOUT);
    }

    protected void assertActivity(Class<? extends Activity> activityClass) {
        boolean b = waitForActivity(HomeActivity.class);
        if (!b) {
            fail("activity isn't as expcted: " + solo.getCurrentActivity().getLocalClassName());
        }
    }

    protected void login() {

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
//        solo.enterText(loginTxt, "chokdee");
//        solo.enterText(pwTxt, "fuckyou123");
//        solo.clickOnButton("Login");
        solo.clickOnView(solo.getView(R.id.button_login));

        assertActivity(HomeActivity.class);

        assertNotNull(MyApplication.getLoginUser());
        assertEquals("chokdee", MyApplication.getLoginUser().getUsername());
    }

    protected void prepareMocks() {
        if (offline) {
            mockHttpClient = new MockHttpClient();
            Client.setHttpClient(mockHttpClient);
            mockHttpClient.getCookieStore().addCookie(new BasicClientCookie(LoginManager.LOGGEDINAS, ""));

            MockResponses.reset();
            MockResponses.forRequestDoAnswer(".*login.*", "loginform.htm"); //fake
            MockResponses.forRequestDoAnswer(".*community/index.*", "index.htm");
        }
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        prepareMocks();

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

//        KeyguardManager keyGuardManager = (KeyguardManager) getActivity().getSystemService(Activity.KEYGUARD_SERVICE);
//        keyGuardManager.newKeyguardLock("activity_classname").disableKeyguard();
//        getInstrumentation().runOnMainSync(new Runnable() {
//            @Override
//            public void run() {
//                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
//            }
//        });

    }
}
