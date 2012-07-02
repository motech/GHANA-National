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
import java.util.Collections;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.motechproject.scheduletracking.api.domain.WindowName.late;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-core.xml"})
public class CWCBcgSchedulesTest extends BaseScheduleTrackingTest {

    @Before
    public void setUp() {
        super.setUp();
        scheduleName = ScheduleNames.CWC_BCG.getName();
    }

    @Test
    public void verifyBCGAlertsIfRegisteredAsSoonAsTheChildIsBorn() throws SchedulerException, ParseException {
        mockToday(newDate("28-FEB-2012"));
        LocalDate dateOfBirth = newDate("28-FEB-2012");

        enrollmentId = scheduleAlertForBCG(dateOfBirth);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId),
                asList(alert(late, onDate("1-MAR-2012")),
                       alert(late, onDate("8-MAR-2012"))));
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("28-Feb-2013", "00:00").toDate())));
    }

    @Test
    public void verifyBCGAlertsIfRegisteredForCWCAfterTheChildTurnsADayOld() throws SchedulerException, ParseException {
        mockToday(newDate("21-FEB-2012"));
        LocalDate dateOfBirth = newDate("20-FEB-2012");

        enrollmentId = scheduleAlertForBCG(dateOfBirth);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId),
                asList(alert(late, onDate("22-FEB-2012")),
                       alert(late, onDate("29-FEB-2012"))));
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("20-Feb-2013", "00:00").toDate())));
    }

    @Test
    public void verifyBCGAlertsIfRegisteredForCWCWhenTheChildIs2DaysOld() throws SchedulerException, ParseException {
        mockToday(newDateWithTime("22-FEB-2012", "10:11"));
        LocalDate dateOfBirth = newDate("20-FEB-2012");

        enrollmentId = scheduleAlertForBCG(dateOfBirth);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId),
                asList(alert(late, onDate("29-FEB-2012"))));

        mockToday(newDateWithTime("22-FEB-2012", "10:09"));
        dateOfBirth = newDate("20-FEB-2012");

        enrollmentId = scheduleAlertForBCG(dateOfBirth);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId),
                asList(alert(late, onDate("22-FEB-2012")), 
                        alert(late, onDate("29-FEB-2012"))));

        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("20-Feb-2013", "00:00").toDate())));
    }

    @Test
    public void verifyBCGAlertsIfRegisteredForCWCWhenTheChildIsOverAWeekOld() throws SchedulerException, ParseException {
        mockToday(newDate("12-FEB-2012"));
        LocalDate dateOfBirth = newDate("1-FEB-2012");

        enrollmentId = scheduleAlertForBCG(dateOfBirth);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), Collections.<TestAlert>emptyList());
        
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("01-Feb-2013", "00:00").toDate())));
    }

    @Test
    public void verifyBCGAlertsIfRegisteredForCWCOverAMonthOld() throws SchedulerException, ParseException {
        mockToday(newDate("02-APR-2012"));
        LocalDate dateOfBirth = newDate("24-FEB-2012");

        enrollmentId = scheduleAlertForBCG(dateOfBirth);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), Collections.<TestAlert>emptyList());
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("24-Feb-2013", "00:00").toDate())));
    }

    private String scheduleAlertForBCG(LocalDate birthDate) {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest(PATIENT_ID, scheduleName, preferredAlertTime, birthDate, null, null, null, null, null);
        return scheduleTrackingService.enroll(enrollmentRequest);
    }
}
