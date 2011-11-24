package org.motechproject.ghana.national.tools;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class StartsWithMatcher extends TypeSafeMatcher<String> {

    private final String string;

    public StartsWithMatcher(String string) {
        if (string == null) {
            throw new IllegalArgumentException("Non-null value required by startsWith()");
        }
        this.string = string;
    }

    @Override
    public boolean matchesSafely(String item) {
        return item.toLowerCase().startsWith(string.toLowerCase());
    }

    public void describeTo(Description description) {
        description.appendText("startsWith(").appendValue(string).appendText(")");
    }

    @Factory
    public static Matcher<String> startsWith(String string) {
        return new StartsWithMatcher(string);
    }
}
