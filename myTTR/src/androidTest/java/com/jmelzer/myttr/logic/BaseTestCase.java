package com.jmelzer.myttr.logic;

import com.jmelzer.myttr.MockHttpClient;
import com.jmelzer.myttr.MockResponses;
import com.jmelzer.myttr.activities.LoginActivity;

import org.junit.Assert;
import junit.framework.TestCase;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.junit.Rule;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class BaseTestCase {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    //switch wether we read html from file system or calling mytt.de
    boolean offline = false;
    protected MockHttpClient mockHttpClient;

    protected void prepareMocks() {
        if (offline) {
            mockHttpClient = new MockHttpClient();
            Client.setHttpClient(mockHttpClient);

            MockResponses.reset();
            MockResponses.forRequestDoAnswer(".*login.*", "loginform.htm"); //fake
            MockResponses.forRequestDoAnswer(".*community/index.*", "index.htm");
        }
    }

    protected void login() throws Exception {

        LoginManager loginManager = new LoginManager();
        //prepare cookies
        prepareMocks();
        prepareCookiesForLogin();
        Assert.assertNotNull(loginManager.login("chokdee", "fuckyou123"));
    }

    private void prepareCookiesForLogin() {
        if (offline) {
            mockHttpClient.getCookieStore().addCookie(new BasicClientCookie(LoginManager.LOGGEDINAS, ""));
        }
    }

    protected String readFile(String file) throws IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(file);
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
