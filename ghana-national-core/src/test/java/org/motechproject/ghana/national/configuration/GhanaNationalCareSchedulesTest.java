package org.motechproject.ghana.national.configuration;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.util.DateUtil;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-core.xml"})
public class
        GhanaNationalCareSchedulesTest extends BaseCareSchedulesTest{
    private Time preferredAlertTime;

    @Before
    public void setUp() {
        super.setUp();
        preferredAlertTime = new Time(10, 10);
    }

    @Test
    public void verifyPregnancySchedule() {
        LocalDate today = DateUtil.newDate(2000, 1, 1);
        mockCurrentDate(today);
        LocalDate conceptionDate = today.minusWeeks(1);

        EnrollmentRequest enrollmentRequest = new EnrollmentRequest("123", CareScheduleNames.DELIVERY, preferredAlertTime, conceptionDate);
        scheduleTrackingService.enroll(enrollmentRequest);


        List<RepeatingSchedulableJob> schedulableJobs = captureSchedules(2);
        assertIfAScheduleWasCreatedOn(schedulableJobs, onDate(conceptionDate, 39));
        assertIfAScheduleWasCreatedOn(schedulableJobs, onDate(conceptionDate, 40));
    }

    private LocalDate onDate(LocalDate conceptionDate, int numberOfWeeks) {
        return conceptionDate.plusWeeks(numberOfWeeks);
    }
}
