package com.jmelzer.myttr.logic;

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
                this.result = result.trim().replace("\u00A0", "");
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
     *
     * @param page     to search threw
     * @param start    index to start
     * @param tagStart
     * @param tagEnd
     */
    ParseResult readBetweenOpenTag(String page, int start, String tagStart, String tagEnd) {
        ParseResult result = readBetween(page, start, tagStart, tagEnd);
        if (result != null) {
            ParseResult result2 = readBetween(result.result, 0, ">", null);
            result2.end = result.end;
            return result2;
        }
        return null;
    }

    ParseResult readBetween(String page, int start, String tagStart, String tagEnd) {
        int s = start;
        int l = 0;
        if (tagStart != null) {
            s = page.indexOf(tagStart, start);
            if (s == -1) {
                return null;
            }
            l = tagStart.length();
        }
        int idxEndTag = 0;
        int end = 0;

        if (tagEnd != null) {
            idxEndTag = page.indexOf(tagEnd, s + l);
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
    protected String safeResult(ParseResult parseResult) {
        if (parseResult != null) {
            return parseResult.result;
        }
        return null;
    }

    protected String[] readHrefAndATag(String line) {
        ParseResult result2 = readBetween(line, 0, "href=\"", "\">");
        String url = safeResult(result2);
        result2 = readBetweenOpenTag(line, 0, "<a", "</a>");
        return new String[]{url, safeResult(result2)};
    }
}
