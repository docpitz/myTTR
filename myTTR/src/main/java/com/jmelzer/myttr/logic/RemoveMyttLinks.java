package com.jmelzer.myttr.logic;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * inspired by the awesome nomytt plugin
 */
public class RemoveMyttLinks extends AbstractBaseParser {


    String replaceIt(String page) {
        Pattern pattern = Pattern.compile("https://www\\.mytischtennis\\.de/clicktt/([^/]+)/.+?/gruppe/([^/]+)/tabelle/gesamt");
        ParseResult resultMeta = readBetween(page, 0, "name=\"nuLigaStatsUrl\"", "/>");
        ParseResult resultFirst = readBetween(resultMeta, 0, "?", "=");
        ParseResult resultSecond = readBetween(resultMeta, 0, "+", "\"");
        if (isEmpty(resultSecond))
            return page;
        String param1 = "";
        String param2 = "";
        try {
            param1 = URLEncoder.encode(resultFirst.result, "UTF-8");
            param2 = URLEncoder.encode(resultSecond.result, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Matcher matcher = pattern.matcher(page);

        StringBuffer sb = new StringBuffer(page.length());

        while (matcher.find()) {
            String f1 = URLEncoder.encode(matcher.group(1));
            matcher.appendReplacement(sb, "https://dttb.click-tt.de/cgi-bin/WebObjects/nuLigaTTDE.woa/wa/groupPage?" +
                    param1 +
                    "=" + f1 + "%20" + param2 + "&group=$2");
        }

        matcher.appendTail(sb);
        page = sb.toString().replaceAll(" target=\"_blank\" ", " ");
        return page;
    }
}
