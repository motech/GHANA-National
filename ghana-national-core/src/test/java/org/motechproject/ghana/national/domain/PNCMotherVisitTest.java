package org.motechproject.ghana.national.domain;

import org.junit.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.motechproject.ghana.national.domain.PNCMotherVisit.schedules;

public class PNCMotherVisitTest {

    @Test
    public void shouldVerifySchedulesForPNCMotherVisit() {
        assertThat(schedules(), is(asList("PNC-MOTHER-1", "PNC-MOTHER-2", "PNC-MOTHER-3")));
    }
}
