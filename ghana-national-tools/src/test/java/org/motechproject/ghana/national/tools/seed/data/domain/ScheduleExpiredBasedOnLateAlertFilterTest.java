package org.motechproject.ghana.national.tools.seed.data.domain;

import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.testing.utils.BaseUnitTest;
import org.motechproject.util.DateUtil;

import java.util.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.motechproject.ghana.national.tools.seed.data.factory.TestUpcomingSchedule.newUpcomingSchedule;

public class ScheduleExpiredBasedOnLateAlertFilterTest extends BaseUnitTest {

    @Test
    public void shouldFilterOutSchedulesWhichHaveAlreadyElapsed(){
        final UpcomingSchedule schedule = newUpcomingSchedule("1000", "2012-1-20 00:00:00.0", "TT3").lateDateTime("2012-2-1 00:00:00.0").build();

        mockCurrentDate(DateUtil.newDateTime(2012, 2, 21, new Time(10, 10)));
        List<UpcomingSchedule> upcomingSchedules = Arrays.asList(schedule);
        assertThat(new ScheduleExpiredBasedOnLateAlertFilter().filteringLogic(upcomingSchedules), is(equalTo(upcomingSchedules)));

        mockCurrentDate(DateUtil.newDateTime(2012, 2, 22, new Time(10, 10)));
        upcomingSchedules = Arrays.asList(schedule);
        assertThat(new ScheduleExpiredBasedOnLateAlertFilter().filteringLogic(upcomingSchedules), is(equalTo(Collections.<UpcomingSchedule>emptyList())));

    }
}
