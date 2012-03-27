package org.motechproject.ghana.national.repository;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ghana.national.configuration.BaseScheduleTrackingTest;
import org.motechproject.ghana.national.configuration.ScheduleNames;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.motechproject.ghana.national.vo.Pregnancy.basedOnDeliveryDate;
import static org.motechproject.util.DateUtil.newDateTime;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-core.xml"})
public class AllSchedulesIT extends BaseScheduleTrackingTest {
    @Autowired
    private AllSchedules allSchedules;

    @Test
    public void shouldGetUpcomingAlertsForPatientInTheNext7Days() {
        final String externalId = "patientId";

        final LocalDate today = newDate("25-Jan-2012");
        mockCurrentDate(today);
        final LocalDate dateOfConception = basedOnDeliveryDate(newDate("22-SEP-2012")).dateOfConception();

        EnrollmentRequest enrollmentRequest = new EnrollmentRequest(externalId, ScheduleNames.ANC_IPT_VACCINE, new Time(10, 10), dateOfConception, null, dateOfConception, null, null, null);
        scheduleTrackingService.enroll(enrollmentRequest);

        mockCurrentDate(newDate("17-MAR-2012"));
        assertEquals(0, allSchedules.upcomingCareForCurrentWeek(externalId).size());

        mockCurrentDate(newDateWithTime("18-MAR-2012", "00:00:00"));
        List<EnrollmentRecord> enrollments = allSchedules.upcomingCareForCurrentWeek(externalId);
        assertEquals(1, enrollments.size());
        assertThat(enrollments.get(0).getStartOfDueWindow(), is(newDateTime(2012, 3, 24, 0, 0, 0)));

        mockCurrentDate(newDateWithTime("24-MAR-2012", "23:59:59.999"));
        assertEquals(1, allSchedules.upcomingCareForCurrentWeek(externalId).size());

        mockCurrentDate(newDateWithTime("25-MAR-2012", "00:00:00"));
        assertEquals(0, allSchedules.upcomingCareForCurrentWeek(externalId).size());
    }
}
