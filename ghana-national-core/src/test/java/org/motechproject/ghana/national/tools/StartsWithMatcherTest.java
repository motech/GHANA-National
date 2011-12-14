package org.motechproject.ghana.national.tools;

import org.hamcrest.Description;
import org.hamcrest.StringDescription;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class StartsWithMatcherTest {

    @Test
    public void shouldMatchTextOnStartingCharacters() {
        assertTrue(StartsWithMatcher.ignoreCaseStartsWith("cou").matches("country"));
        assertFalse(StartsWithMatcher.ignoreCaseStartsWith("cOut").matches("counTRY"));
    }

    @Test
    public void shouldMatchSafely() {
        assertTrue(new StartsWithMatcher("Hello").matchesSafely("hello"));
        assertFalse(new StartsWithMatcher("Hello").matchesSafely(null));
    }

    @Test
    public void shouldDescribeTheMatcher() {
        final Description stringDescription = new StringDescription();
        new StartsWithMatcher("hello").describeTo(stringDescription);
        assertEquals("startsWith(\"hello\")", stringDescription.toString());
    }
}
