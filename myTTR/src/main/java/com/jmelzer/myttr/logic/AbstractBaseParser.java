package com.jmelzer.myttr.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by J. Melzer on 19.02.2015.
 */
public class AbstractBaseParser {

    List<String[]> parseTable(String page, String tableTag, int coloumnCount) {
        return parseTable(page, tableTag, coloumnCount, false);
    }

    List<String[]> parseTable(String page, String tableTag, int coloumnCount, boolean withHeader) {
        ParseResult table = readBetween(page, 0, tableTag, "</table>");
        List<String[]> rows = new ArrayList<>();
        if (table == null) {
            return rows;
        }

        int c = 0;
        int idx = 0;
        while (true) {
            ParseResult resultrow = readBetweenOpenTag(table.result, idx, "<tr", "</tr>");

            if (isEmpty(resultrow)) {
                break;
            }
            if (!withHeader && c == 0) {
                idx = resultrow.end;
                c++;
                continue;//skip first row

            }
            idx = resultrow.end - 1;

            String[] columns = tableRowAsArray(resultrow.result, coloumnCount, (c == 0));
            if (allEmpty(columns) && c == 0) { //second try
                columns = tableRowAsArray(resultrow.result, coloumnCount, false);
            }
            rows.add(columns);
            c++;

        }
        return rows;
    }

    private boolean allEmpty(String[] columns) {
        for (String column : columns) {
            if (column != null && !column.isEmpty())
                return false;
        }
        return true;
    }

    static class ParseResult {
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

    ParseResult readBetween(ParseResult result, int start, String tagStart, String tagEnd) {
        if (result == null)
            return null;
        return readBetween(result.result, start, tagStart, tagEnd);
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
        if (line == null || line.isEmpty()) {
            return new String[]{"", ""};
        }
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
     * @param isHeader
     * @return array, will be filled with emoty string if validsize isn't reached
     */
    String[] tableRowAsArray(String tr, int validsize, boolean isHeader) {
        String[] arr = new String[validsize];
        int startIdx = 0;
        for (int i = 0; i < validsize; i++) {
            ParseResult td = readBetweenOpenTag(tr, startIdx, isHeader ? "<th" : "<td",
                    isHeader ? "</th>" : "</td>");
            if (td != null) {
                startIdx = td.end;
                arr[i] = td.result.trim();
            } else {
                arr[i] = "";
            }
        }
        return arr;
    }

    String cleanHtml(ParseResult result) {
        if (result == null) {
            return "";
        }
        String ret = result.result;
        return cleanHtml(ret);
    }

    String cleanHtml(String ret) {
        ret = ret.replaceAll("<b>", " ");
        ret = ret.replaceAll("</b>", " ");
        ret = replaceMultipleSpaces(ret);
        ret = ret.replaceAll("<br />", "\n");
        ret = ret.replaceAll("\n ", "\n");
        ret = ret.replaceAll(" \n", "\n");
        ret = ret.replaceAll("&euro;", "€");
        ret = removeLastNewLine(ret);
        return ret.trim();
    }

    String removeLastNewLine(String result) {
        if (result.lastIndexOf('\n') == result.length() - 1) {
            if (result.length() > 1)
                return result.substring(0, result.length() - 1);
            else
                return "";
        }
        return result;
    }

    protected String replaceMultipleSpaces(String s) {
        s = s.replaceAll("\\s{2,}", " ");
        return s;
    }

    protected String listFromLiElement(ParseResult parseResult) {
        if (parseResult == null)
            return "";
        String html = parseResult.result;
        List<String> list = new ArrayList<>();
        ParseResult result = readBetween(html, 0, "<li>", "</li>");
        while (result != null) {
            list.add(removeLabel(cleanHtml(result.result)));

            result = readBetween(html, result.end, "<li>", "</li>");
        }
        String m = "";
        for (String s : list) {
            m += s + "\n";
        }
        return m;
    }

    private String removeLabel(String s) {
        ParseResult result = readBetween(s, 0, "<label style=\"width:300px;\">", "</label>");
        if (result == null) return s;
        else return result.result + s.substring(result.end);
    }
}
