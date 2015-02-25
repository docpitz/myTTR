package com.jmelzer.myttr;

import org.apache.http.client.methods.HttpUriRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J. Melzer on 24.02.2015.
 */
public class MockResponses {

    private static final List<String[]> responseMapping = new ArrayList<String[]>();
    private static final String BASE = "assets/";

    public static String forRequest(final HttpUriRequest request) {
        final String requestString = request.getURI().toString();
        for (final String[] mapping : responseMapping) {
            if (requestString.matches(mapping[0])) {
                return BASE + mapping[1];
            }
        }
        throw new IllegalArgumentException(
                "No mocked reply configured for request: " + requestString);
    }

    public static void forRequestDoAnswer(final String regex,
                                          final String fileToReturn) {
        responseMapping.add(new String[]{regex, fileToReturn});
    }

    public static void reset() {
        responseMapping.clear();
    }
}
