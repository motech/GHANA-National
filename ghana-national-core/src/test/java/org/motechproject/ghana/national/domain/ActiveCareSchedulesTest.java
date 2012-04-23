package org.motechproject.ghana.national.domain;

import org.junit.Test;
import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;

import static junit.framework.Assert.assertTrue;

public class ActiveCareSchedulesTest {
    @Test
    public void shouldReturnTrueIfActiveScheduleForIPTiIsPresent() {
        ActiveCareSchedules activeCareSchedules = new ActiveCareSchedules();
        activeCareSchedules.setActiveCareSchedule(ScheduleNames.CWC_IPT_VACCINE, new EnrollmentRecord(null, null, null, null, null, null, null, null, null, null));
        assertTrue(activeCareSchedules.hasActiveIPTiSchedule());
    }
}
