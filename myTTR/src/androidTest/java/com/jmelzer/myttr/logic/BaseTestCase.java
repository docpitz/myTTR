package com.jmelzer.myttr.logic;

import com.jmelzer.myttr.MockHttpClient;
import com.jmelzer.myttr.MockResponses;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 */
public class BaseTestCase extends TestCase {
    //switch wether we read html from file system or calling mytt.de
    boolean offline = true;
    protected MockHttpClient mockHttpClient;

    protected void prepareMocks() {
        if (offline) {
            mockHttpClient = new MockHttpClient();
            Client.setHttpClient(mockHttpClient);

            MockResponses.reset();
            MockResponses.forRequestDoAnswer(".*login.*", "loginform.html"); //fake
            MockResponses.forRequestDoAnswer(".*community/index.*", "index.htm");
        }
    }

    protected void login() throws IOException {

        LoginManager loginManager = new LoginManager();
        //prepare cookies
        prepareMocks();
        prepareCookiesForLogin();
        Assert.assertTrue(loginManager.login("chokdee", "fuckyou123"));
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
