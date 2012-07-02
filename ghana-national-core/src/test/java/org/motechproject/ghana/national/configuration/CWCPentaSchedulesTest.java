package org.motechproject.ghana.national.configuration;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.quartz.SchedulerException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.motechproject.scheduletracking.api.domain.WindowName.due;
import static org.motechproject.scheduletracking.api.domain.WindowName.late;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-core.xml"})
public class CWCPentaSchedulesTest extends BaseScheduleTrackingTest {

    @Before
    public void setUp() {
        super.setUp();
        scheduleName = ScheduleNames.CWC_PENTA.getName();
    }

    @Test
    public void verifyRegisterForCWCAsSoonAsBornOrMuchBefore6WeeksToGetAlertForPenta() throws SchedulerException, ParseException {
        mockToday(newDate("02-JAN-2012"));
        LocalDate dateOfBirth = newDate("30-DEC-2011");

        enrollmentId = scheduleAlertForPentaEnrolledFromStart(dateOfBirth, null);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(due, onDate("03-FEB-2012")),
                alert(late, onDate("10-FEB-2012")),
                alert(late, onDate("17-FEB-2012")),
                alert(late, onDate("24-FEB-2012")))
        );
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("09-Mar-2012", "00:00").toDate())));
    }

    @Test
    public void verifyStartPenta2AfterPenta1() throws SchedulerException, ParseException {
        mockToday(newDate("16-JAN-2012"));
//        LocalDate dateOfBirth = newDate("13-JAN-2012");

        enrollmentId = scheduleAlertForPentaEnrolledFromStart(null, null);
        fulfillCurrentMilestone(newDate("25-FEB-2012"));
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(due, onDate("17-MAR-2012")),
                alert(late, onDate("24-MAR-2012")),
                alert(late, onDate("31-MAR-2012")),
                alert(late, onDate("7-APR-2012")))
        );
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("21-Apr-2012", "00:00").toDate())));
    }

    @Test
    public void verifyStartPenta3AfterPenta2() throws SchedulerException, ParseException {
        mockToday(newDate("10-JAN-2012"));
        LocalDate dateOfBirth = newDate("04-JAN-2012");

        enrollmentId = scheduleAlertForPentaEnrolledFromStart(dateOfBirth, null);
        fulfillCurrentMilestone(newDate("17-FEB-2012"));
        fulfillCurrentMilestone(newDate("20-MAR-2012"));

        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(due, onDate("10-APR-2012")),
                alert(late, onDate("17-APR-2012")),
                alert(late, onDate("24-APR-2012")),
                alert(late, onDate("01-MAY-2012")))
        );
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("15-May-2012", "00:00").toDate())));
    }

    @Test
    public void shouldRegisterChildForCWCInThe7thWeek() throws ParseException, SchedulerException {
        mockToday(newDate("12-FEB-2012"));
        LocalDate dateOfBirth = newDate("30-DEC-2011");

        enrollmentId = scheduleAlertForPentaEnrolledFromStart(dateOfBirth, null);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(late, onDate("17-FEB-2012")),
                alert(late, onDate("24-FEB-2012")))
        );
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("09-Mar-2012", "00:00").toDate())));
    }

    @Test
    public void shouldRegisterChildForCWCInThe8thWeek() throws ParseException, SchedulerException {
        mockToday(newDate("18-FEB-2012"));
        LocalDate dateOfBirth = newDate("30-DEC-2011");

        enrollmentId = scheduleAlertForPentaEnrolledFromStart(dateOfBirth, null);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(late, onDate("24-FEB-2012")))
        );
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("09-Mar-2012", "00:00").toDate())));
    }

    @Test
    public void shouldRegisterChildForCWCInThe9thWeek() throws ParseException, SchedulerException {
        mockToday(newDate("25-FEB-2012"));
        LocalDate dateOfBirth = newDate("30-DEC-2011");

        enrollmentId = scheduleAlertForPentaEnrolledFromStart(dateOfBirth, null);
        assertTrue(CollectionUtils.isEmpty(captureAlertsForNextMilestone(enrollmentId)));
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("09-Mar-2012", "00:00").toDate())));
    }

    @Test
    public void shouldRegisterChildForCWCInThe10thWeek() throws ParseException, SchedulerException {
        mockToday(newDate("06-MAR-2012"));
        LocalDate dateOfBirth = newDate("30-DEC-2011");

        enrollmentId = scheduleAlertForPentaEnrolledFromStart(dateOfBirth, null);
        assertTrue(CollectionUtils.isEmpty(captureAlertsForNextMilestone(enrollmentId)));
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("09-Mar-2012", "00:00").toDate())));
    }

    @Test
    public void verifyPenta2TriggersTheSchedule() throws ParseException, SchedulerException {
        mockToday(newDate("01-APR-2012"));
        LocalDate lastPentaDate = newDate("29-MAR-2012");

        enrollmentId = scheduleAlertForPentaEnrolledWithHistory(lastPentaDate, "Penta2");
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(due, onDate("19-APR-2012")),
                alert(late, onDate("26-APR-2012")),
                alert(late, onDate("03-MAY-2012")),
                alert(late, onDate("10-MAY-2012")))
        );
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("24-May-2012", "00:00").toDate())));
    }

    private String scheduleAlertForPentaEnrolledFromStart(LocalDate birthDate, String milestoneName) {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest(PATIENT_ID, scheduleName, preferredAlertTime, birthDate, null, null, null, milestoneName, null);
        return scheduleTrackingService.enroll(enrollmentRequest);
    }

    private String scheduleAlertForPentaEnrolledWithHistory(LocalDate lastPentaDate, String milestoneName) {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest(PATIENT_ID, scheduleName, preferredAlertTime, null, null, lastPentaDate, null, milestoneName, null);
        return scheduleTrackingService.enroll(enrollmentRequest);
    }
}
