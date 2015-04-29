package com.jmelzer.myttr.logic;

import com.jmelzer.myttr.utils.StringUtils;

import java.io.BufferedReader;
import java.io.File;
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
        File currentDir = new File(".");
//        String fn = absolute ? file : "src/androidTest/" + file;
        String fn = currentDir.getAbsolutePath() + "/src/androidTest/" + file;
        System.out.println("open file " + fn);
        if (!new File(fn).exists()) throw new IOException("File "+ fn + " doesn't exists");

        InputStream in = new FileInputStream(fn);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
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
