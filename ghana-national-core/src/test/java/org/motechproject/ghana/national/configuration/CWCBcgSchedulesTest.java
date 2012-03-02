package org.motechproject.ghana.national.configuration;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.quartz.SchedulerException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-core.xml"})
public class CWCBcgSchedulesTest extends BaseScheduleTrackingTest {

    @Before
    public void setUp() {
        super.setUp();
        scheduleName = ScheduleNames.CWC_BCG;
    }

    @Test
    public void verifyBCGAlertsIfRegisteredAsSoonAsTheChildIsBorn() throws SchedulerException, ParseException {
        mockToday(newDate("24-FEB-2000"));
        LocalDate dateOfBirth = newDate("24-FEB-2000");

        enrollmentId = scheduleAlertForBCG(dateOfBirth);
        assertAlerts(captureAlertsForNextMilestone(enrollmentId), dates(newDate("26-FEB-2000"), newDate("4-MAR-2000")));
    }

    @Test
    public void verifyBCGAlertsIfRegisteredForCWCAfterTheChildTurnsADayOld() throws SchedulerException, ParseException {
        mockToday(newDate("25-FEB-2000"));
        LocalDate dateOfBirth = newDate("24-FEB-2000");

        enrollmentId = scheduleAlertForBCG(dateOfBirth);
        assertAlerts(captureAlertsForNextMilestone(enrollmentId), dates(newDate("26-FEB-2000"), newDate("04-MAR-2000")));
    }

    @Test
    public void verifyBCGAlertsIfRegisteredForCWCWhenTheChildIs2DaysOld() throws SchedulerException, ParseException {
        mockToday(newDate("26-FEB-2000"));
        LocalDate dateOfBirth = newDate("24-FEB-2000");

        enrollmentId = scheduleAlertForBCG(dateOfBirth);
        assertAlerts(captureAlertsForNextMilestone(enrollmentId), dates(newDate("26-FEB-2000"), newDate("04-MAR-2000")));
    }

    @Test
    public void verifyBCGAlertsIfRegisteredForCWCWhenTheChildIsOverAWeekOld() throws SchedulerException, ParseException {
        mockToday(newDate("03-MAR-2000"));
        LocalDate dateOfBirth = newDate("24-FEB-2000");

        enrollmentId = scheduleAlertForBCG(dateOfBirth);
        assertAlerts(captureAlertsForNextMilestone(enrollmentId), dates(newDate("04-MAR-2000")));
    }

    @Test
    public void verifyBCGAlertsIfRegisteredForCWCOverAMonthOld() throws SchedulerException, ParseException {
        mockToday(newDate("02-APR-2000"));
        LocalDate dateOfBirth = newDate("24-FEB-2000");

        enrollmentId = scheduleAlertForBCG(dateOfBirth);
        assertAlerts(captureAlertsForNextMilestone(enrollmentId), dates());
    }

    private String scheduleAlertForBCG(LocalDate birthDate) {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest(PATIENT_ID, scheduleName, preferredAlertTime, birthDate, null, null, null, null);
        return scheduleTrackingService.enroll(enrollmentRequest);
    }
}
