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

import java.text.ParseException;
import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.motechproject.scheduletracking.api.domain.WindowName.due;
import static org.motechproject.scheduletracking.api.domain.WindowName.late;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-core.xml"})
public class CWCOPV1SchedulesTest extends BaseScheduleTrackingTest {

    @Before
    public void setUp() {
        super.setUp();
        scheduleName = ScheduleNames.CWC_OPV_OTHERS;
    }
    
    @Test
    public void shouldVerifyOPV1SchedulesOnRegistrationAtChildBirth() throws SchedulerException {
         LocalDate dateOfBirth = newDate("5-FEB-2012");
        LocalDate dateOfRegistration = newDate("5-FEB-2012");
        mockToday(dateOfRegistration);

        enrollmentId = scheduleAlertForOPV(dateOfBirth);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(WindowName.due, onDate("11-MAR-2012")),
                alert(WindowName.late, onDate("18-MAR-2012")),
                alert(WindowName.late, onDate("25-MAR-2012")),
                alert(WindowName.late, onDate("1-APR-2012"))
        ));

    }

    @Test
    public void shouldVerifyOPV1SchedulesOnRegistrationAtFiveWeeksAge() throws SchedulerException {
         LocalDate dateOfBirth = newDate("11-FEB-2012");
        LocalDate dateOfRegistration = newDate("17-MAR-2012");
        mockToday(dateOfRegistration);

        enrollmentId = scheduleAlertForOPV(dateOfBirth);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(WindowName.due, onDate("17-MAR-2012")),
                alert(WindowName.late, onDate("24-MAR-2012")),
                alert(WindowName.late, onDate("31-MAR-2012")),
                alert(WindowName.late, onDate("7-APR-2012"))
        ));

    }

    @Test
    public void shouldVerifyOPV1SchedulesOnRegistrationAtSixWeeksAge() throws SchedulerException {
         LocalDate dateOfBirth = newDate("13-FEB-2012");
        LocalDate dateOfRegistration = newDate("26-MAR-2012");
        mockToday(dateOfRegistration);

        enrollmentId = scheduleAlertForOPV(dateOfBirth);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(WindowName.late, onDate("26-MAR-2012")),
                alert(WindowName.late, onDate("2-APR-2012")),
                alert(WindowName.late, onDate("9-APR-2012"))
        ));

    }

    @Test
    public void shouldVerifyOPV1SchedulesOnRegistrationAtSevenWeeksAge() throws SchedulerException {
         LocalDate dateOfBirth = newDate("23-FEB-2012");
        LocalDate dateOfRegistration = newDate("11-APR-2012");
        mockToday(dateOfRegistration);

        enrollmentId = scheduleAlertForOPV(dateOfBirth);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(WindowName.late, onDate("12-APR-2012")),
                alert(WindowName.late, onDate("19-APR-2012"))
        ));

    }

    @Test
    public void shouldVerifyOPV1SchedulesOnRegistrationUnderTenWeeksAgeWithAnAlert() throws SchedulerException {
         LocalDate dateOfBirth = newDate("9-MAR-2012");
        LocalDate dateOfRegistration = newDate("4-MAY-2012");
        mockToday(dateOfRegistration);

        enrollmentId = scheduleAlertForOPV(dateOfBirth);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(WindowName.late, onDate("4-MAY-2012"))
        ));

    }

    @Test
    public void shouldVerifyOPV1SchedulesOnRegistrationOverTenWeeksAge() throws SchedulerException {
         LocalDate dateOfBirth = newDate("28-APR-2012");
        LocalDate dateOfRegistration = newDate("7-JUL-2012");
        mockToday(dateOfRegistration);

        enrollmentId = scheduleAlertForOPV(dateOfBirth);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), new ArrayList<TestAlert>());

    }

    @Test
    public void shouldVerifyOPV2SchedulesOnFulfillmentOfOPV1() throws SchedulerException {
         LocalDate dateOfBirth = newDate("30-AUG-2012");
        LocalDate dateOfRegistration = newDate("13-SEP-2012");
        mockToday(dateOfRegistration);

        enrollmentId = scheduleAlertForOPV(dateOfBirth);
        fulfillCurrentMilestone(newDate("26-SEP-2012"));
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(WindowName.due, onDate("17-OCT-2012")),
                alert(WindowName.late, onDate("24-OCT-2012")),
                alert(WindowName.late, onDate("31-OCT-2012")),
                alert(WindowName.late, onDate("7-NOV-2012"))
        ));

    }

    @Test
    public void shouldVerifyOPV3SchedulesOnFulfillmentOfOPV2AndOPV1() throws SchedulerException {
         LocalDate dateOfBirth = newDate("4-APR-2012");
        LocalDate dateOfRegistration = newDate("25-APR-2012");
        mockToday(dateOfRegistration);

        enrollmentId = scheduleAlertForOPV(dateOfBirth);
        fulfillCurrentMilestone(newDate("1-MAY-2012"));
        fulfillCurrentMilestone(newDate("23-JUN-2012"));

        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(WindowName.due, onDate("14-JUL-2012")),
                alert(WindowName.late, onDate("21-JUL-2012")),
                alert(WindowName.late, onDate("28-JUL-2012")),
                alert(WindowName.late, onDate("4-AUG-2012"))
        ));

    }

    @Test
    public void verifyOPV1TriggersTheSchedule() throws ParseException, SchedulerException {
        mockToday(newDate("01-APR-2012"));
        LocalDate lastOPVDate = newDate("29-MAR-2012");

        enrollmentId = scheduleAlertForOPVEnrolledWithHistory(lastOPVDate, "OPV2");
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(due, onDate("19-APR-2012")),
                alert(late, onDate("26-APR-2012")),
                alert(late, onDate("03-MAY-2012")),
                alert(late, onDate("10-MAY-2012")))
        );
    }


    private String scheduleAlertForOPV(LocalDate referenceDate) {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest(PATIENT_ID, scheduleName, preferredAlertTime, referenceDate, null, referenceDate, null, null, null);
        return scheduleTrackingService.enroll(enrollmentRequest);
    }

    private String scheduleAlertForOPVEnrolledWithHistory(LocalDate lastPentaDate, String milestoneName) {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest(PATIENT_ID, scheduleName, preferredAlertTime, null, null, lastPentaDate, null, milestoneName, null);
        return scheduleTrackingService.enroll(enrollmentRequest);
    }
}