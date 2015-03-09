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
        if (url == null)
            return null;
        return url.substring(0, url.indexOf(".de")+3);
    }
}
