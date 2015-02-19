package com.jmelzer.myttr.logic;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 */
public class BaseTestCase extends TestCase {

    protected void login() throws IOException {

        LoginManager loginManager = new LoginManager();
        Assert.assertTrue(loginManager.login("chokdee", "fuckyou123"));
    }

    protected String readFile( String file ) throws IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(file);
        BufferedReader reader = new BufferedReader( new InputStreamReader(in));
        String         line = null;
        StringBuilder  stringBuilder = new StringBuilder();
        String         ls = System.getProperty("line.separator");

        while( ( line = reader.readLine() ) != null ) {
            stringBuilder.append( line );
            stringBuilder.append( ls );
        }

        return StringEscapeUtils.unescapeHtml4(stringBuilder.toString());
    }
}
