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
import static org.motechproject.ghana.national.configuration.ScheduleNames.*;
import static org.motechproject.scheduletracking.api.domain.WindowName.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-core.xml"})
public class PNCMotherSchedulesTest extends BaseScheduleTrackingTest {

    @Before
    public void setUp() {
        super.setUp();
    }

    //////////////////////////////////////////// Case: PNC alerts are triggered as soon as the Mother's delivery form is submitted ////////////////////////////////////////////////////////
    @Test
    public void verifyPNC1MotherAlersAsSoonAsTheMotherDeliveryFormIsSubmitted() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("01-Mar-2012", "12:30");
        mockToday(newDateWithTime("01-Mar-2012", "12:30"));

        enrollmentId = scheduleAlertForPNCMother(dateOfBirth, PNC_MOTHER_1);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(due, newDateWithTime("01-Mar-2012", "18:30").toDate()),
                alert(late, newDateWithTime("02-Mar-2012", "00:30").toDate())
        ));
    }

    @Test
    public void verifyPNC2MotherAlertsIfRegisteredAsSoonAsTheChildIsBorn() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("01-Mar-2012", "12:30");
        mockToday(newDateWithTime("01-Mar-2012", "12:30"));

        enrollmentId = scheduleAlertForPNCMother(dateOfBirth, PNC_MOTHER_2);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(earliest, newDateWithTime("06-Mar-2012", "12:30").toDate()),
                alert(late, newDateWithTime("08-Mar-2012", "12:30").toDate())
        ));
    }

    @Test
    public void verifyPNC3MotherAlertsIfRegisteredAsSoonAsTheChildIsBorn() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("01-Mar-2012", "12:30");
        mockToday(newDateWithTime("01-Mar-2012", "12:30"));

        enrollmentId = scheduleAlertForPNCMother(dateOfBirth, PNC_MOTHER_3);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(earliest, newDateWithTime("13-Mar-2012", "12:30").toDate()),
                alert(late, newDateWithTime("15-Mar-2012", "12:30").toDate())
        ));
    }
    //////////////////////////////////////////// Case: Mother's Delivery is registered >= 6 hours from child birth ////////////////////////////////////////////////////////
    @Test
    public void verifyPNC1MotherAlertsIfRegisteredOnOrAfter6HoursFromChildBirth() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("03-Jan-2012", "12:30");
        mockToday(newDateWithTime("03-Jan-2012", "19:38:12"));

        enrollmentId = scheduleAlertForPNCMother(dateOfBirth, PNC_MOTHER_1);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(late, newDateWithTime("04-Jan-2012", "00:30").toDate())
        ));
    }

    @Test
    public void verifyPNC2MotherAlertsIfRegisteredOnOrAfter6HoursFromChildBirth() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("03-Jan-2012", "12:30");
        mockToday(newDateWithTime("03-Jan-2012", "19:38:12"));

        enrollmentId = scheduleAlertForPNCMother(dateOfBirth, PNC_MOTHER_2);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(earliest, newDateWithTime("08-Jan-2012", "12:30").toDate()),
                alert(late, newDateWithTime("10-Jan-2012", "12:30").toDate())
        ));
    }

    @Test
    public void verifyPNC3MotherAlertsIfRegisteredOnOrAfter6HoursFromChildBirth() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("03-Jan-2012", "12:30");
        mockToday(newDateWithTime("03-Jan-2012", "19:38:12"));

        enrollmentId = scheduleAlertForPNCMother(dateOfBirth, PNC_MOTHER_3);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(earliest, newDateWithTime("15-Jan-2012", "12:30").toDate()),
                alert(late, newDateWithTime("17-Jan-2012", "12:30").toDate())
        ));
    }
    //////////////////////////////////////////// Case: Mother's Delivery is registered >= 12 hours from child birth ////////////////////////////////////////////////////////
    @Test
    public void verifyPNC1MotherAlertsIfRegisteredOnOrAfter12HoursFromChildBirth() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("13-Apr-2012", "4:30");
        mockToday(newDateWithTime("13-Apr-2012", "19:00:34"));

        enrollmentId = scheduleAlertForPNCMother(dateOfBirth, PNC_MOTHER_1);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), Collections.<TestAlert>emptyList());
    }

    @Test
    public void verifyPNC2MotherAlertsIfRegisteredOnOrAfter12HoursFromChildBirth() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("13-Apr-2012", "4:30");
        mockToday(newDateWithTime("13-Apr-2012", "19:00:34"));

        enrollmentId = scheduleAlertForPNCMother(dateOfBirth, PNC_MOTHER_2);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(earliest, newDateWithTime("18-Apr-2012", "4:30").toDate()),
                alert(late, newDateWithTime("20-Apr-2012", "4:30").toDate())
        ));
    }

    @Test
    public void verifyPNC3MotherAlertsIfRegisteredOnOrAfter12HoursFromChildBirth() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("13-Apr-2012", "4:30");
        mockToday(newDateWithTime("13-Apr-2012", "19:00:34"));

        enrollmentId = scheduleAlertForPNCMother(dateOfBirth, PNC_MOTHER_3);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(earliest, newDateWithTime("25-Apr-2012", "4:30").toDate()),
                alert(late, newDateWithTime("27-Apr-2012", "4:30").toDate())
        ));
    }
    //////////////////////////////////////////// Case: Mother's Delivery is registered >= 48 hours from child birth ////////////////////////////////////////////////////////
    @Test
    public void verifyPNC1MotherAlertsIfRegisteredOnOrAfter48HoursFromChildBirth() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("27-May-2012", "17:04");
        mockToday(newDateWithTime("29-May-2012", "17:49:55"));

        enrollmentId = scheduleAlertForPNCMother(dateOfBirth, PNC_MOTHER_1);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), Collections.<TestAlert>emptyList());
    }

    @Test
    public void verifyPNC2MotherAlertsIfRegisteredOnOrAfter48HoursFromChildBirth() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("27-May-2012", "17:04");
        mockToday(newDateWithTime("29-May-2012", "17:49:55"));

        enrollmentId = scheduleAlertForPNCMother(dateOfBirth, PNC_MOTHER_2);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(earliest, newDateWithTime("01-Jun-2012", "17:04").toDate()),
                alert(late, newDateWithTime("03-Jun-2012", "17:04").toDate())
        ));
    }

    @Test
    public void verifyPNC3MotherAlertsIfRegisteredOnOrAfter48HoursFromChildBirth() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("27-May-2012", "17:04");
        mockToday(newDateWithTime("29-May-2012", "17:49:55"));

        enrollmentId = scheduleAlertForPNCMother(dateOfBirth, PNC_MOTHER_3);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(earliest, newDateWithTime("08-Jun-2012", "17:04").toDate()),
                alert(late, newDateWithTime("10-Jun-2012", "17:04").toDate())
        ));
    }

    //////////////////////////////////////////// Case: Mother's Delivery is registered >= 5 days from birth ////////////////////////////////////////////////////////
    @Test
    public void verifyPNC1MotherAlertsIfRegisteredOnOrAfter5DaysFromChildBirth() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("15-Mar-2012", "09:45");
        mockToday(newDateWithTime("20-Mar-2012", "10:45:00"));

        enrollmentId = scheduleAlertForPNCMother(dateOfBirth, PNC_MOTHER_1);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), Collections.<TestAlert>emptyList());
    }

    @Test
    public void verifyPNC2MotherAlertsIfRegisteredOnOrAfter5DaysFromChildBirth() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("15-Mar-2012", "09:45");
        mockToday(newDateWithTime("20-Mar-2012", "10:45:00"));

        enrollmentId = scheduleAlertForPNCMother(dateOfBirth, PNC_MOTHER_2);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(late, newDateWithTime("22-Mar-2012", "09:45").toDate())
        ));
    }

    @Test
    public void verifyPNC3MotherAlertsIfRegisteredOnOrAfter5DaysFromChildBirth() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("15-Mar-2012", "09:45");
        mockToday(newDateWithTime("20-Mar-2012", "10:45:12"));

        enrollmentId = scheduleAlertForPNCMother(dateOfBirth, PNC_MOTHER_3);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(earliest, newDateWithTime("27-Mar-2012", "09:45").toDate()),
                alert(late, newDateWithTime("29-Mar-2012", "09:45").toDate())
        ));
    }

    //////////////////////////////////////////// Case: PNC Alerts when Mother's Delivery is registered exactly 7 days after time of delivery ////////////////////////////////////////////////////////
    @Test
    public void verifyPNC1MotherAlertsWhenMotherDeliveryIsRegisteredExactly7DaysAfterTimeOfDelivery() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("30-Jan-2012", "14:25:12");
        mockToday(newDateWithTime("06-Feb-2012", "14:25:12"));

        enrollmentId = scheduleAlertForPNCMother(dateOfBirth, PNC_MOTHER_1);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), Collections.<TestAlert>emptyList());
    }

    @Test
    public void verifyPNC2BabyAlertsIfRegisteredExactlyAt7DaysOfAge() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("30-Jan-2012", "14:25:12");
        mockToday(newDateWithTime("06-Feb-2012", "14:25:12"));

        enrollmentId = scheduleAlertForPNCMother(dateOfBirth, PNC_CHILD_2);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), Collections.<TestAlert>emptyList());
    }

    @Test
    public void verifyPNC3BabyAlertsIfRegisteredExactlyAt7DaysOfAge() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("30-Jan-2012", "14:25:12");
        mockToday(newDateWithTime("06-Feb-2012", "14:25:12"));

        enrollmentId = scheduleAlertForPNCMother(dateOfBirth, PNC_CHILD_3);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                // seconds are not considered
                alert(earliest, newDateWithTime("11-Feb-2012", "14:25").toDate()),
                alert(late, newDateWithTime("13-Feb-2012", "14:25").toDate())
        ));
    }
    //////////////////////////////////////////// Case: Mother's Delivery is registered over 14 days of age of child- PNC1 is NA ////////////////////////////////////////////////////////

    @Test
    public void verifyPNC2MotherAlertsIfRegisteredOver14DaysOfAge() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("29-Feb-2012", "04:30:00");
        mockToday(newDateWithTime("14-Mar-2012", "15:50:23"));

        enrollmentId = scheduleAlertForPNCMother(dateOfBirth, PNC_MOTHER_2);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), Collections.<TestAlert>emptyList());
    }

    @Test
    public void verifyPNC3BabyAlertsIfRegisteredOver14DaysOfAge() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("29-Feb-2012", "04:30:00");
        mockToday(newDateWithTime("14-Mar-2012", "15:50:23"));

        enrollmentId = scheduleAlertForPNCMother(dateOfBirth, PNC_MOTHER_3);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), Collections.<TestAlert>emptyList());
    }

    //////////////////////////////////////////// Case: Mother's Delivery is registered over 21 days of age - PNC1, PNC2 is NA ////////////////////////////////////////////////////////

    @Test
    public void verifyPNC3MotherAlertsIfRegisteredOver21DaysOfAge() throws SchedulerException {
        DateTime dateOfBirth = newDateWithTime("29-Feb-2012", "10:00:00");
        mockToday(newDateWithTime("21-Mar-2012", "21:20:23"));

        enrollmentId = scheduleAlertForPNCMother(dateOfBirth, PNC_MOTHER_3);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), Collections.<TestAlert>emptyList());
    }

    private String scheduleAlertForPNCMother(DateTime birthDate, String pncScheduleName) {
        scheduleName = pncScheduleName;
        Time referenceTime = new Time(birthDate.getHourOfDay(), birthDate.getMinuteOfHour());
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest(PATIENT_ID, scheduleName, null, birthDate.toLocalDate(), referenceTime, null, null, null, null);
        return scheduleTrackingService.enroll(enrollmentRequest);
    }
}
