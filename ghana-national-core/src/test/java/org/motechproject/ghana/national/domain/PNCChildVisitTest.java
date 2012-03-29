package org.motechproject.ghana.national.domain;

import org.junit.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.motechproject.ghana.national.domain.PNCChildVisit.*;

public class PNCChildVisitTest {

    @Test
    public void shouldVerifySchedulesForPNCChildVisit() {
        assertThat(schedules(), is(asList("PNC-CHILD-1", "PNC-CHILD-2", "PNC-CHILD-3")));
    }

}
