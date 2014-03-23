package com.jmelzer.myttr;

/**
 * TODO
 * User: jmelzer
 * Date: 23.03.14
 * Time: 13:33
 */
public class Util {
    public static String abbreviate(final String str, int offset, final int maxWidth) {
        int offset1 = offset;
        if (str == null) {
            return null;
        }
        if (maxWidth < 4) {
            throw new IllegalArgumentException("Minimum abbreviation width is 4");
        }
        if (str.length() <= maxWidth) {
            return str;
        }
        if (offset1 > str.length()) {
            offset1 = str.length();
        }
        if (str.length() - offset1 < maxWidth - 3) {
            offset1 = str.length() - (maxWidth - 3);
        }
        final String abrevMarker = "...";
        if (offset1 <= 4) {
            return str.substring(0, maxWidth - 3) + abrevMarker;
        }
        if (maxWidth < 7) {
            throw new IllegalArgumentException("Minimum abbreviation width with offset is 7");
        }
        if (offset1 + maxWidth - 3 < str.length()) {
            return abrevMarker + abbreviate(str.substring(offset1), 0, maxWidth - 3);
        }
        return abrevMarker + str.substring(str.length() - (maxWidth - 3));
    }
}
