package com.jmelzer.myttr.activities;

import android.app.Activity;
import android.app.KeyguardManager;
import android.test.ActivityInstrumentationTestCase2;
import android.view.WindowManager;

import com.jmelzer.myttr.MockHttpClient;
import com.jmelzer.myttr.MockResponses;
import com.jmelzer.myttr.logic.Client;
import com.jmelzer.myttr.logic.LoginManager;

import org.apache.http.impl.cookie.BasicClientCookie;

/**
 * Created by J. Melzer on 01.05.2015.
 * Base class handle
 */
public abstract class BaseActivityInstrumentationTestCase<T extends Activity> extends ActivityInstrumentationTestCase2<T> {

    //switch wether we read html from file system or calling mytt.de
    boolean offline = true;


    public BaseActivityInstrumentationTestCase(Class<T> activityClass) {
        super(activityClass);
    }

    protected MockHttpClient mockHttpClient;

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
