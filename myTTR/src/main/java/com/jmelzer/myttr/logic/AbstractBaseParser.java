package com.jmelzer.myttr.logic;

import java.util.Locale;

/**
 * Created by J. Melzer on 19.02.2015.
 */
public class AbstractBaseParser {
    class ParseResult {
        String result;

        int end;

        ParseResult(String result, int end) {
            trim(result);
            this.end = end;
        }

        private void trim(String result) {
            if (result != null) {
                //non-breaking space
                this.result = result.trim().replace("\u00A0", " ");
            }
        }

        public boolean isEmpty() {
            return result == null || result.isEmpty();
        }
    }

    boolean isEmpty(ParseResult result) {
        return result == null || result.isEmpty();
    }

    /**
     * read string between 2 tag first tag can be opened e.g. <td but we seach after the closing >
     * The start tag shall not be closed with >
     *
     * @param page     to search threw
     * @param start    index to start
     * @param tagStart to search for, can be null
     * @param tagEnd   to search for, can be null
     */
    ParseResult readBetweenOpenTag(String page, int start, String tagStart, String tagEnd) {
        return readBetweenOpenTag(page, start, tagStart, tagEnd, false);
    }

    ParseResult readBetweenOpenTag(String page, int start, String tagStart, String tagEnd, boolean ignoreCase) {
        ParseResult result = readBetween(page, start, tagStart, tagEnd, ignoreCase);
        if (result != null) {
            ParseResult result2 = readBetween(result.result, 0, ">", null, ignoreCase);
            result2.end = result.end;
            return result2;
        }
        return null;
    }

    ParseResult readBetween(String page, int start, String tagStart, String tagEnd, boolean ignoreCase) {
        String toSearch = "";
        if (ignoreCase) {
            toSearch = page.toLowerCase(Locale.GERMAN);
        } else {
            toSearch = page;
        }
        int s = start;
        int l = 0;
        if (tagStart != null) {
            s = toSearch.indexOf(tagStart, start);
            if (s == -1) {
                return null;
            }
            l = tagStart.length();
        }
        int idxEndTag = 0;
        int end = 0;

        if (tagEnd != null) {
            idxEndTag = toSearch.indexOf(tagEnd, s + l);
            end = idxEndTag + tagEnd.length();
        } else {
            end = idxEndTag = page.length();
        }
        if (idxEndTag == -1) {
            return new ParseResult(null, end);
        } else {
            return new ParseResult(page.substring(s + l, idxEndTag), end);
        }
    }

    ParseResult readBetween(String page, int start, String tagStart, String tagEnd) {
        return readBetween(page, start, tagStart, tagEnd, false);
    }

    protected String safeResult(ParseResult parseResult) {
        if (parseResult != null) {
            return parseResult.result;
        }
        return null;
    }

    /**
     * read the <a tag an return the url and the description inside the a tag
     *
     * @param line to be parsed
     * @return first url, second value inside the tag
     */
    protected String[] readHrefAndATag(String line) {
        ParseResult result2 = readBetween(line, 0, "href=\"", "\"");
        String url = safeResult(result2);
        result2 = readBetweenOpenTag(line, 0, "<a", "</a>");
        return new String[]{url, safeResult(result2)};
    }

    /**
     * read all td elements from a tr and store them into a array
     *
     * @param tr        tp be parsed
     * @param validsize valid size of of the columns
     * @return array, will be filled with emoty string if validsize isn't reached
     */
    String[] tableRowAsArray(String tr, int validsize) {
        String[] arr = new String[validsize];
        int startIdx = 0;
        for (int i = 0; i < validsize; i++) {
            ParseResult td = readBetweenOpenTag(tr, startIdx, "<td", "</td>");
            if (td != null) {
                startIdx = td.end;
                arr[i] = td.result.trim();
            } else {
                arr[i] = "";
            }
        }
        return arr;
    }
}
