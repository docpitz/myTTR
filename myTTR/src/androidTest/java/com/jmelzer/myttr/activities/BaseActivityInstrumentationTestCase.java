package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.EditText;

import com.jmelzer.myttr.Constants;
import com.jmelzer.myttr.MockHttpClient;
import com.jmelzer.myttr.MockResponses;
import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.R;
import com.jmelzer.myttr.db.LoginDataBaseAdapter;
import com.jmelzer.myttr.logic.Client;
import com.jmelzer.myttr.logic.LoginManager;
import com.jmelzer.myttr.logic.SyncManager;
import com.robotium.solo.Solo;

import org.apache.http.impl.cookie.BasicClientCookie;

/**
 * Created by J. Melzer on 01.05.2015.
 * Base class handle
 */
public abstract class BaseActivityInstrumentationTestCase<T extends Activity> extends ActivityInstrumentationTestCase2<T> {

    //switch wether we read html from file system or calling mytt.de
    boolean offline = true;

    protected static final int STANDARD_TIMEOUT = 20000;


    public BaseActivityInstrumentationTestCase(Class<T> activityClass) {
        super(activityClass);
    }

    protected MockHttpClient mockHttpClient;
    protected Solo solo;

    public boolean waitForActivity(Class<? extends Activity> activityClass) {
        long start = System.currentTimeMillis();
        boolean b = solo.waitForActivity(activityClass, STANDARD_TIMEOUT);
        System.out.println("waited for activity " + activityClass.getCanonicalName() + " for " + (System.currentTimeMillis() - start) + "ms - result=" + b);
        Log.d(Constants.LOG_TAG, "waited for activity " + activityClass.getCanonicalName() + " for " + (System.currentTimeMillis() - start) + "ms - result=" + b);
        return b;
    }

    protected void assertActivity(Class<? extends Activity> activityClass) {
        boolean b = waitForActivity(activityClass);
        if (!b) {
            fail("current activity " + solo.getCurrentActivity().getLocalClassName() + " isn't as expected: " +
                    activityClass.getName() + "\n" + st());
        }
    }

    String st() {
        String s = "-----------------\n";
        for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
            s += ste + "\n";
        }
        return s + "-----------------";
    }

    protected void login() throws InterruptedException {

        assertTrue(solo.waitForActivity(LoginActivity.class));
        LoginActivity loginActivity = (LoginActivity) solo.getCurrentActivity();
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
//        loginActivity.login(null);
        Thread.sleep(2000);
        //net statement is very buggy, sometimes it doesn't work
        solo.clickOnView(solo.getView(R.id.button_login));
        System.out.println("after login");
        Log.d(Constants.LOG_TAG, "after login");
//        Thread.sleep(200000);//let the activity do something here

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
    protected void tearDown() throws Exception {
        super.tearDown();
        solo.finishOpenedActivities();
        Log.d(Constants.LOG_TAG, "------  end test " + getClass().getCanonicalName() + " ----- ");
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        SyncManager.testIsRun = true;
        Log.d(Constants.LOG_TAG, "------  starting test " + getClass().getCanonicalName() + " ----- ");
        prepareMocks();

        setActivityInitialTouchMode(true);
        Context context = getInstrumentation().getTargetContext();
        LoginDataBaseAdapter adapter = new LoginDataBaseAdapter(context);
        adapter.open();
        adapter.deleteAllEntries();

        //prepare the db and prefs to automatic

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPref.edit().putBoolean(MySettingsActivity.KEY_PREF_SAVE_USER, false).commit();
        sharedPref.edit().putInt(MySettingsActivity.KEY_PREF_TIMER, 10).commit();

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
