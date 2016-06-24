package com.jmelzer.myttr.util;

/**
 * Created by J. Melzer on 07.03.2015.
 * Util class for some helper methods around urls
 */
public final class UrlUtil {
    public static String safeUrl(String http, String relUrl) {
        if (relUrl == null || relUrl.startsWith("http")) {
            return relUrl;
        }
        return http + relUrl;
    }
    public static String getHttpAndDomain(String url) {
        if (url == null || url.isEmpty())
            return null;

        return url.substring(0, url.indexOf(".de")+3);
    }

    /**
     * converts the string to a google maps format
     * e.g. Spiellokal 2: Sporthalle Süd\nEifelstr., 53859 Niederkassel-Mondorf
     * @param s to parse
     */
    public static String formatAddressToGoogleMaps(String s) {
        String result = "";
        String tmpStr = s;
        if (tmpStr.contains("Spiellokal")){
            tmpStr = tmpStr.substring(tmpStr.indexOf(":")+1);
        }
        tmpStr = tmpStr.trim();
        result = "geo:0,0?q=" + tmpStr.replaceAll("\n", " ").replaceAll(" ", "%20");

        return result;

    }
    public static String formatAddressToGoogleMaps(String plz, String city, String street) {
        return  "geo:0,0?q=" + plz + "%20" + city + "%20" + street;
    }
}
