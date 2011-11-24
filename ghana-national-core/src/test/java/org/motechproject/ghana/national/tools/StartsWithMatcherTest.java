package org.motechproject.ghana.national.tools;

import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class StartsWithMatcherTest {
    
    @Test
    public void shouldMatchTextOnStartingCharacters() {
        assertTrue(StartsWithMatcher.ignoreCaseStartsWith("cou").matches("country"));
        assertFalse(StartsWithMatcher.ignoreCaseStartsWith("cOut").matches("counTRY"));
    }
}
