package org.motechproject.ghana.national.tools.seed.data.domain;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.motechproject.ghana.national.tools.seed.data.factory.TestUpcomingSchedule.newUpcomingSchedule;

public class VoidedScheduleFilterTest {
    @Test
    public void shouldFilterOutVoidedRecords(){
        final UpcomingSchedule nonVoidedSchedule = newUpcomingSchedule("1000", "2012-12-17 00:00:00.0", "TT3").build();
        final UpcomingSchedule voidedSchedule = newUpcomingSchedule("1000", "2012-12-17 00:00:00.0", "TT2").markVoided().build();
        List<UpcomingSchedule> upcomingSchedules = Arrays.asList(nonVoidedSchedule, voidedSchedule);

        assertThat(new VoidedScheduleFilter().filteringLogic(upcomingSchedules), is(equalTo(Arrays.asList(nonVoidedSchedule))));

    }
}
