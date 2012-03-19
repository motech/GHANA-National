package org.motechproject.ghana.national.configuration;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.quartz.SchedulerException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;

import static java.util.Arrays.asList;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-core.xml"})
public class CWCOpv0SchedulesTest extends BaseScheduleTrackingTest {

    @Before
    public void setUp() {
        super.setUp();
        scheduleName = ScheduleNames.CWC_OPV_0;
    }

    @Test
    public void shouldVerifyOPV0scheduleOnBirthOfChild() throws SchedulerException {
        LocalDate dateOfBirth = newDate("1-APR-2012");
        mockToday(dateOfBirth);

        enrollmentId = scheduleAlertForOPV(dateOfBirth);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(WindowName.late, onDate("03-APR-2012")),
                alert(WindowName.late, onDate("10-APR-2012"))
        ));
    }

    @Test
    public void shouldVerifyOPV0scheduleOnRegistrationAtAgeUptoTenDays() throws SchedulerException {
        LocalDate dateOfBirth = newDate("2-JAN-2012");
        LocalDate dateOfRegistration = newDate("12-JAN-2012");
        mockToday(dateOfRegistration);

        enrollmentId = scheduleAlertForOPV(dateOfBirth);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), new ArrayList<TestAlert>());
    }

    @Test
    public void shouldVerifyOPV0scheduleOnRegistrationAtAgeOneWeek() throws SchedulerException {
        LocalDate dateOfBirth = newDate("2-JAN-2012");
        LocalDate dateOfRegistration = newDate("7-JAN-2012");
        mockToday(dateOfRegistration);

        enrollmentId = scheduleAlertForOPV(dateOfBirth);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(WindowName.late, onDate("11-JAN-2012"))
        ));
    }

    private String scheduleAlertForOPV(LocalDate referenceDate) {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest(PATIENT_ID, scheduleName, preferredAlertTime, referenceDate, null, referenceDate, null, null);
        return scheduleTrackingService.enroll(enrollmentRequest);
    }
}
