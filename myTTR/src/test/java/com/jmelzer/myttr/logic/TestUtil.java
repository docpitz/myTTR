package com.jmelzer.myttr.logic;

import com.jmelzer.myttr.utils.StringUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by J. Melzer on 22.04.2015.
 * Helper methods.
 */
public class TestUtil {

    public static String readFile(String file) throws IOException {
        InputStream in = new FileInputStream("src/androidTest/" + file);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }

        return StringUtils.unescapeHtml3(stringBuilder.toString());
    }
}
