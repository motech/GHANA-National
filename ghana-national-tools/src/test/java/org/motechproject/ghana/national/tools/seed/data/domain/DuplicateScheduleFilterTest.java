package org.motechproject.ghana.national.tools.seed.data.domain;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.motechproject.ghana.national.tools.seed.data.factory.TestUpcomingSchedule.newUpcomingSchedule;

public class DuplicateScheduleFilterTest {
    @Test
    public void shouldFilterDuplicatesAndRetainOnlyOneRecord(){
        List<UpcomingSchedule> upcomingSchedules = Arrays.asList(newUpcomingSchedule("1000", "2012-12-17 00:00:00.0", "TT2").build(),
                newUpcomingSchedule("1000", "2012-12-17 00:00:00.0", "TT2").build());
        final List<UpcomingSchedule> filteredSchedule = new DuplicateScheduleFilter().filteringLogic(upcomingSchedules);
        assertThat(filteredSchedule, is(equalTo(Arrays.asList(newUpcomingSchedule("1000", "2012-12-17 00:00:00.0", "TT2").build()))));
    }
}
