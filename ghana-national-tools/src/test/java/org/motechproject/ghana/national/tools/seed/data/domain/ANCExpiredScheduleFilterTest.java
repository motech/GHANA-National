package org.motechproject.ghana.national.tools.seed.data.domain;

import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.motechproject.ghana.national.tools.seed.data.factory.TestUpcomingSchedule.newUpcomingSchedule;

public class ANCExpiredScheduleFilterTest extends BaseUnitTest{
    @Test
    public void shouldFilterSchedulesWhosMaxAlertDateIsAlreadyExpired(){
        final UpcomingSchedule schedule = newUpcomingSchedule("1000", "2012-1-1 00:00:00.0", "ANC").build();

        mockCurrentDate(DateUtil.newDateTime(2012, 1, 17, new Time(0, 0)));
        List<UpcomingSchedule> upcomingSchedules = Arrays.asList(schedule);
        assertThat(new ANCExpiredScheduleFilter().filteringLogic(upcomingSchedules), is(equalTo(Collections.<UpcomingSchedule>emptyList())));

        mockCurrentDate(DateUtil.newDateTime(2012, 1, 16, new Time(0, 0)));
        upcomingSchedules = Arrays.asList(schedule);
        assertThat(new ANCExpiredScheduleFilter().filteringLogic(upcomingSchedules), is(equalTo(upcomingSchedules)));
    }
}
