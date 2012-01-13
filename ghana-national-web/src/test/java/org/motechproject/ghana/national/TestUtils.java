package org.motechproject.ghana.national;

import org.hamcrest.Matcher;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;

public class TestUtils {

    public static <T> Matcher<T> isEq(T value) { 
        return is(equalTo(value));
    }
}
