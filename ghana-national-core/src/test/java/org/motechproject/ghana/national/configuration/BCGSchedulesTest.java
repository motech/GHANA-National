package org.motechproject.ghana.national.configuration;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.quartz.SchedulerException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.motechproject.util.DateUtil.newDate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-core.xml"})
public class BCGSchedulesTest extends BaseScheduleTrackingTest {

    @Before
    public void setUp() {
        super.setUp();
        scheduleName = ScheduleNames.BCG;
    }

    @Test
    public void verifyBCGAlertsIfRegisteredAsSoonAsTheChildIsBorn() throws SchedulerException {
        mockToday(newDate(2000, 2, 24));
        LocalDate dateOfBirth = newDate(2000, 2, 24);

        enrollmentId = scheduleAlertForBCG(dateOfBirth);
        assertAlerts(captureAlertsForNextMilestone(enrollmentId), dates(newDate(2000, 2, 26), newDate(2000, 3, 4)));
    }

    @Test
    public void verifyBCGAlertsIfRegisteredForCWCAfterTheChildTurnsADayOld() throws SchedulerException {
        mockToday(newDate(2000, 2, 25));
        LocalDate dateOfBirth = newDate(2000, 2, 24);

        enrollmentId = scheduleAlertForBCG(dateOfBirth);
        assertAlerts(captureAlertsForNextMilestone(enrollmentId), dates(newDate(2000, 2, 26), newDate(2000, 3, 4)));
    }

    @Test
    public void verifyBCGAlertsIfRegisteredForCWCWhenTheChildIs2DaysOld() throws SchedulerException {
        mockToday(newDate(2000, 2, 26));
        LocalDate dateOfBirth = newDate(2000, 2, 24);

        enrollmentId = scheduleAlertForBCG(dateOfBirth);
        assertAlerts(captureAlertsForNextMilestone(enrollmentId), dates(newDate(2000, 2, 26), newDate(2000, 3, 4)));
    }

    @Test
    public void verifyBCGAlertsIfRegisteredForCWCWhenTheChildIsOverAWeekOld() throws SchedulerException {
        mockToday(newDate(2000, 3, 3));
        LocalDate dateOfBirth = newDate(2000, 2, 24);

        enrollmentId = scheduleAlertForBCG(dateOfBirth);
        assertAlerts(captureAlertsForNextMilestone(enrollmentId), dates(newDate(2000, 3, 4)));
    }

    @Test
    public void verifyBCGAlertsIfRegisteredForCWCOverAMonthOld() throws SchedulerException {
        mockToday(newDate(2000, 4, 2));
        LocalDate dateOfBirth = newDate(2000, 2, 24);

        enrollmentId = scheduleAlertForBCG(dateOfBirth);
        assertAlerts(captureAlertsForNextMilestone(enrollmentId), dates());
    }

    private String scheduleAlertForBCG(LocalDate birthDate) {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest(PATIENT_ID, scheduleName, preferredAlertTime, birthDate, null, null);
        return scheduleTrackingService.enroll(enrollmentRequest);
    }
}
