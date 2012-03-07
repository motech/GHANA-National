package org.motechproject.ghana.national.util;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class AssertionUtility {
    public static void assertContainsTemplateValues(Map<String, String> expectedValues, Map<String, String> actualValues) {
        for (Map.Entry<String, String> entry : expectedValues.entrySet()) {
            assertThat("Failed : expected value : " + entry + " actual value : " + actualValues.get(entry.getKey()),
                    actualValues.get(entry.getKey()), is(equalTo(entry.getValue())));
        }
    }

}
