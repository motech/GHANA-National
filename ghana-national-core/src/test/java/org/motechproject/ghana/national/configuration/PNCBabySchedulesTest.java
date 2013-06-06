package org.motechproject.ghana.national.configuration;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.quartz.SchedulerException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.motechproject.ghana.national.configuration.ScheduleNames.*;
import static org.motechproject.scheduletracking.api.domain.WindowName.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-core.xml"})
public class PNCBabySchedulesTest extends BaseScheduleTrackingTest{

    @Before
    public void setUp() {
        super.setUp();
    }

    //////////////////////////////////////////// Case: Child is Born ////////////////////////////////////////////////////////
    @Test
    public void verifyPNC1BabyAlertsIfRegisteredAsSoonAsTheChildIsBorn() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("01-Mar-2012", "12:30");
        mockToday(newDateWithTime("01-Mar-2012", "12:30"));

        enrollmentId = scheduleAlertForPNCBaby(dateOfBirth, PNC_CHILD_1.getName());
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(due, newDateWithTime("01-Mar-2012", "18:30").toDate()),
                alert(late, newDateWithTime("02-Mar-2012", "00:30").toDate())
        ));
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("03-Mar-2012", "12:30").toDate())));
    }

    @Test
    public void verifyPNC2BabyAlertsIfRegisteredAsSoonAsTheChildIsBorn() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("01-Mar-2012", "12:30");
        mockToday(newDateWithTime("01-Mar-2012", "12:30"));

        enrollmentId = scheduleAlertForPNCBaby(dateOfBirth, PNC_CHILD_2.getName());
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(earliest, newDateWithTime("06-Mar-2012", "12:29").toDate()),
                alert(late, newDateWithTime("08-Mar-2012", "12:29").toDate())
        ));
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("13-Mar-2012", "12:30").toDate())));
    }

    @Test
    public void verifyPNC3BabyAlertsIfRegisteredAsSoonAsTheChildIsBorn() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("01-Mar-2012", "12:30");
        mockToday(newDateWithTime("01-Mar-2012", "12:30"));

        enrollmentId = scheduleAlertForPNCBaby(dateOfBirth, PNC_CHILD_3.getName());
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(earliest, newDateWithTime("13-Mar-2012", "12:29").toDate()),
                alert(late, newDateWithTime("15-Mar-2012", "12:29").toDate())
        ));
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("22-Mar-2012", "12:30").toDate())));
    }
    //////////////////////////////////////////// Case: Child is registered >= 6 hours from birth ////////////////////////////////////////////////////////
    @Test
    public void verifyPNC1BabyAlertsIfRegisteredOnOrAfter6HoursFromChildBirth() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("03-Jan-2012", "12:30");
        mockToday(newDateWithTime("03-Jan-2012", "19:38:12"));

        enrollmentId = scheduleAlertForPNCBaby(dateOfBirth, PNC_CHILD_1.getName());
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(late, newDateWithTime("04-Jan-2012", "00:30").toDate())
        ));
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("05-Jan-2012", "12:30").toDate())));
    }

    @Test
    public void verifyPNC2BabyAlertsIfRegisteredOnOrAfter6HoursFromChildBirth() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("03-Jan-2012", "12:30");
        mockToday(newDateWithTime("03-Jan-2012", "19:38:12"));

        enrollmentId = scheduleAlertForPNCBaby(dateOfBirth, PNC_CHILD_2.getName());
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(earliest, newDateWithTime("08-Jan-2012", "12:29").toDate()),
                alert(late, newDateWithTime("10-Jan-2012", "12:29").toDate())
        ));
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("15-Jan-2012", "12:30").toDate())));
    }

    @Test
    public void verifyPNC3BabyAlertsIfRegisteredOnOrAfter6HoursFromChildBirth() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("03-Jan-2012", "12:30");
        mockToday(newDateWithTime("03-Jan-2012", "19:38:12"));

        enrollmentId = scheduleAlertForPNCBaby(dateOfBirth, PNC_CHILD_3.getName());
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(earliest, newDateWithTime("15-Jan-2012", "12:29").toDate()),
                alert(late, newDateWithTime("17-Jan-2012", "12:29").toDate())
        ));
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("24-Jan-2012", "12:30").toDate())));
    }
    //////////////////////////////////////////// Case: Child is registered >= 12 hours from birth ////////////////////////////////////////////////////////
    @Test
    public void verifyPNC1BabyAlertsIfRegisteredOnOrAfter12HoursFromChildBirth() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("13-Apr-2012", "4:30");
        mockToday(newDateWithTime("13-Apr-2012", "19:00:34"));

        enrollmentId = scheduleAlertForPNCBaby(dateOfBirth, PNC_CHILD_1.getName());
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), Collections.<TestAlert>emptyList());
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("15-Apr-2012", "04:30").toDate())));
    }

    @Test
    public void verifyPNC2BabyAlertsIfRegisteredOnOrAfter12HoursFromChildBirth() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("13-Apr-2012", "4:30");
        mockToday(newDateWithTime("13-Apr-2012", "19:00:34"));

        enrollmentId = scheduleAlertForPNCBaby(dateOfBirth, PNC_CHILD_2.getName());
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(earliest, newDateWithTime("18-Apr-2012", "4:29").toDate()),
                alert(late, newDateWithTime("20-Apr-2012", "4:29").toDate())
        ));
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("25-Apr-2012", "04:30").toDate())));
    }

    @Test
    public void verifyPNC3BabyAlertsIfRegisteredOnOrAfter12HoursFromChildBirth() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("13-Apr-2012", "4:30");
        mockToday(newDateWithTime("13-Apr-2012", "19:00:34"));

        enrollmentId = scheduleAlertForPNCBaby(dateOfBirth, PNC_CHILD_3.getName());
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(earliest, newDateWithTime("25-Apr-2012", "4:29").toDate()),
                alert(late, newDateWithTime("27-Apr-2012", "4:29").toDate())
        ));
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("04-May-2012", "04:30").toDate())));
    }
    //////////////////////////////////////////// Case: Child is registered >= 48 hours from birth ////////////////////////////////////////////////////////
    @Test
    public void verifyPNC1BabyAlertsIfRegisteredOnOrAfter48HoursFromChildBirth() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("27-May-2012", "17:04");
        mockToday(newDateWithTime("29-May-2012", "17:49:55"));

        enrollmentId = scheduleAlertForPNCBaby(dateOfBirth, PNC_CHILD_1.getName());
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), Collections.<TestAlert>emptyList());
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(null)));
    }

    @Test
    public void verifyPNC2BabyAlertsIfRegisteredOnOrAfter48HoursFromChildBirth() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("27-May-2012", "17:04");
        mockToday(newDateWithTime("29-May-2012", "17:49:55"));

        enrollmentId = scheduleAlertForPNCBaby(dateOfBirth, PNC_CHILD_2.getName());
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(earliest, newDateWithTime("01-Jun-2012", "17:03").toDate()),
                alert(late, newDateWithTime("03-Jun-2012", "17:03").toDate())
        ));
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("08-Jun-2012", "17:04").toDate())));
    }

    @Test
    public void verifyPNC3BabyAlertsIfRegisteredOnOrAfter48HoursFromChildBirth() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("27-May-2012", "17:04");
        mockToday(newDateWithTime("29-May-2012", "17:49:55"));

        enrollmentId = scheduleAlertForPNCBaby(dateOfBirth, PNC_CHILD_3.getName());
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(earliest, newDateWithTime("08-Jun-2012", "17:03").toDate()),
                alert(late, newDateWithTime("10-Jun-2012", "17:03").toDate())
        ));
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("17-Jun-2012", "17:04").toDate())));
    }

    //////////////////////////////////////////// Case: Child is registered >= 5 days from birth ////////////////////////////////////////////////////////
    @Test
    public void verifyPNC1BabyAlertsIfRegisteredOnOrAfter5DaysFromChildBirth() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("15-Mar-2012", "09:45");
        mockToday(newDateWithTime("20-Mar-2012", "10:45:00"));

        enrollmentId = scheduleAlertForPNCBaby(dateOfBirth, PNC_CHILD_1.getName());
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), Collections.<TestAlert>emptyList());
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(null)));
    }

    @Test
    public void verifyPNC2BabyAlertsIfRegisteredOnOrAfter5DaysFromChildBirth() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("15-Mar-2013", "09:45");
        mockToday(newDateWithTime("20-Mar-2013", "10:45:00"));

        enrollmentId = scheduleAlertForPNCBaby(dateOfBirth, PNC_CHILD_2.getName());
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(late, newDateWithTime("22-Mar-2013", "09:44").toDate())
        ));
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("27-Mar-2013", "09:45").toDate())));
    }

    @Test
    public void verifyPNC3BabyAlertsIfRegisteredOnOrAfter5DaysFromChildBirth() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("15-Mar-2013", "09:45");
        mockToday(newDateWithTime("20-Mar-2013", "10:45:12"));

        enrollmentId = scheduleAlertForPNCBaby(dateOfBirth, PNC_CHILD_3.getName());
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                // when seconds are left cause alerts to be missed
                alert(earliest, newDateWithTime("27-Mar-2013", "09:44").toDate()),
                alert(late, newDateWithTime("29-Mar-2013", "09:44").toDate())
        ));
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("05-Apr-2013", "09:45").toDate())));
    }

    //////////////////////////////////////////// Case: Child is registered exactly 7 days of age ////////////////////////////////////////////////////////
    @Test
    public void verifyPNC1BabyAlertsIfRegisteredExactlyAt7DaysOfAge() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("30-Jan-2012", "14:25:12");
        mockToday(newDateWithTime("06-Feb-2012", "14:25:12"));

        enrollmentId = scheduleAlertForPNCBaby(dateOfBirth, PNC_CHILD_1.getName());
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), Collections.<TestAlert>emptyList());
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(null)));
    }

    @Test
    public void verifyPNC2BabyAlertsIfRegisteredExactlyAt7DaysOfAge() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("30-Jan-2013", "14:25");
        mockToday(newDateWithTime("06-Feb-2013", "14:25:12"));

        enrollmentId = scheduleAlertForPNCBaby(dateOfBirth, PNC_CHILD_2.getName());
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), Collections.<TestAlert>emptyList());
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("11-Feb-2013", "14:25").toDate())));
    }

    @Test
    public void verifyPNC3BabyAlertsIfRegisteredExactlyAt7DaysOfAge() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("30-Jan-2012", "14:25:12");
        mockToday(newDateWithTime("06-Feb-2012", "14:25:12"));

        enrollmentId = scheduleAlertForPNCBaby(dateOfBirth, PNC_CHILD_3.getName());
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                // seconds are not considered
                alert(earliest, newDateWithTime("11-Feb-2012", "14:24:00").toDate()),
                alert(late, newDateWithTime("13-Feb-2012", "14:24:00").toDate())
        ));
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("20-Feb-2012", "14:25").toDate())));
    }

    //////////////////////////////////////////// Case: Child is registered over 14 days of age - PN1 is NA ////////////////////////////////////////////////////////

    @Test
    public void verifyPNC2BabyAlertsIfRegisteredOver14DaysOfAge() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("29-Feb-2012", "04:30:00");
        mockToday(newDateWithTime("14-Mar-2012", "15:50:23"));

        enrollmentId = scheduleAlertForPNCBaby(dateOfBirth, PNC_CHILD_2.getName());
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), Collections.<TestAlert>emptyList());
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(null)));
    }

    //////////////////////////////////////////// Case: Child is registered over 21 days of age - PN1, PN2, PN3 is NA ////////////////////////////////////////////////////////

    @Test
    public void verifyPNC3BabyAlertsIfRegisteredOver21DaysOfAge() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("29-Feb-2012", "10:00:00");
                mockToday(newDateWithTime("21-Mar-2012", "21:20:23"));

        enrollmentId = scheduleAlertForPNCBaby(dateOfBirth, PNC_CHILD_3.getName());
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), Collections.<TestAlert>emptyList());
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(null)));
    }

    private String scheduleAlertForPNCBaby(DateTime birthDate, String pncScheduleName) {
        scheduleName = pncScheduleName;
        Time referenceTime = new Time(birthDate.getHourOfDay(), birthDate.getMinuteOfHour());
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest(PATIENT_ID, scheduleName, null, birthDate.toLocalDate(), referenceTime, null, null, null, null);
        return scheduleTrackingService.enroll(enrollmentRequest);
    }
}
