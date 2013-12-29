/* 
* Copyright (C) allesklar.com AG
* All rights reserved.
*
* Author: juergi
* Date: 27.12.13 
*
*/


package com.jmelzer.myttr.parser;

import com.jmelzer.myttr.MyApplication;
import com.jmelzer.myttr.Verein;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashMap;

public class VereinParser {


    HashMap<String, Verein> vereinHashMap = new HashMap<String, Verein>();

    public Verein getVerein(String name) {
        int r = MyApplication.getAppContext().getResources().getIdentifier("raw/vereine", "raw", "com.jmelzer.myttr");

        if (vereinHashMap.isEmpty()) {
            readFile(r);
        }
        //todo better parser e.g. matcher
        return vereinHashMap.get(name);
    }

    private void readFile(int r) {
        try {
            InputStream is = MyApplication.getAppContext().getResources().openRawResource(r);
            LineNumberReader reader = new LineNumberReader(new InputStreamReader(is));

            String line=reader.readLine();
            while (line != null ) {
//                System.out.println("line = " + line);
                Verein v = parseLine(line);
                vereinHashMap.put(v.getName(), v);
                line=reader.readLine();
//                if (true) break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Verein parseLine(String line) {
//        'SV%20Bohlingen','21017,STTB','SV Bohlingen'
        String rest = line.substring(1);
        String webName = rest.substring(0, rest.indexOf("'"));
        rest = rest.substring(webName.length() + 3);
        String id = rest.substring(0, rest.indexOf(","));
        rest = rest.substring(id.length()+1);
        String verband = rest.substring(0, rest.indexOf("'"));
        rest = rest.substring(verband.length()+3);
        String name = rest.substring(0, rest.indexOf("'"));
        return new Verein(name, id, verband, webName);
    }

    public HashMap<String, Verein> getVereinHashMap() {
        return vereinHashMap;
    }
}
