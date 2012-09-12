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
public class CWCRotavirusVaccineSchedulesTest extends BaseScheduleTrackingTest {

    @Before
    public void setUp() {
        super.setUp();
        scheduleName = ScheduleNames.CWC_ROTAVIRUS.getName();
    }

    @Test
    public void verifyRegisterForCWCAsSoonAsBornOrMuchBefore6WeeksToGetAlertForRotavirus() throws SchedulerException, ParseException
    {
        mockToday(newDate("02-JAN-2012"));
        LocalDate dateOfBirth = newDate("30-DEC-2011");

        enrollmentId = scheduleAlertForRotavirusEnrolledFromStart(dateOfBirth, null);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(due, onDate("03-FEB-2012")),
                alert(late, onDate("10-FEB-2012")),
                alert(late, onDate("17-FEB-2012")),
                alert(late, onDate("24-FEB-2012")))
        );
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("09-Mar-2012", "00:00").toDate())));
    }

    @Test
    public void verifyStartRotavirus2AfterRotavirus1() throws SchedulerException, ParseException {
        mockToday(newDate("16-JAN-2012"));
//        LocalDate dateOfBirth = newDate("13-JAN-2012");

        enrollmentId = scheduleAlertForRotavirusEnrolledFromStart(null, null);
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
    public void shouldRegisterChildForCWCInThe7thWeek() throws ParseException, SchedulerException {
        mockToday(newDate("12-FEB-2012"));
        LocalDate dateOfBirth = newDate("30-DEC-2011");

        enrollmentId = scheduleAlertForRotavirusEnrolledFromStart(dateOfBirth, null);
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

        enrollmentId = scheduleAlertForRotavirusEnrolledFromStart(dateOfBirth, null);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(late, onDate("24-FEB-2012")))
        );
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("09-Mar-2012", "00:00").toDate())));
    }

    @Test
    public void shouldRegisterChildForCWCInThe9thWeek() throws ParseException, SchedulerException {
        mockToday(newDate("25-FEB-2012"));
        LocalDate dateOfBirth = newDate("30-DEC-2011");

        enrollmentId = scheduleAlertForRotavirusEnrolledFromStart(dateOfBirth, null);
        assertTrue(CollectionUtils.isEmpty(captureAlertsForNextMilestone(enrollmentId)));
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("09-Mar-2012", "00:00").toDate())));
    }

    @Test
    public void shouldRegisterChildForCWCInThe10thWeek() throws ParseException, SchedulerException {
        mockToday(newDate("06-MAR-2012"));
        LocalDate dateOfBirth = newDate("30-DEC-2011");

        enrollmentId = scheduleAlertForRotavirusEnrolledFromStart(dateOfBirth, null);
        assertTrue(CollectionUtils.isEmpty(captureAlertsForNextMilestone(enrollmentId)));
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("09-Mar-2012", "00:00").toDate())));
    }

    @Test
    public void verifyRotavirus2TriggersTheSchedule() throws ParseException, SchedulerException {
        mockToday(newDate("01-APR-2012"));
        LocalDate lastRotavirusDate = newDate("29-MAR-2012");

        enrollmentId = scheduleAlertForRotavirusEnrolledWithHistory(lastRotavirusDate, "Rotavirus2");
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(due, onDate("19-APR-2012")),
                alert(late, onDate("26-APR-2012")),
                alert(late, onDate("03-MAY-2012")),
                alert(late, onDate("10-MAY-2012")))
        );
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("24-May-2012", "00:00").toDate())));
    }

    private String scheduleAlertForRotavirusEnrolledFromStart(LocalDate birthDate, String milestoneName) {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest().setExternalId(PATIENT_ID)
                .setScheduleName(scheduleName).setPreferredAlertTime(preferredAlertTime)
                .setReferenceDate(birthDate).setStartingMilestoneName(milestoneName);
        return scheduleTrackingService.enroll(enrollmentRequest);
    }

    private String scheduleAlertForRotavirusEnrolledWithHistory(LocalDate lastRotavirusDate, String milestoneName) {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest().setExternalId(PATIENT_ID)
                .setScheduleName(scheduleName).setPreferredAlertTime(preferredAlertTime)
                .setEnrollmentDate(lastRotavirusDate).setStartingMilestoneName(milestoneName);
        return scheduleTrackingService.enroll(enrollmentRequest);
    }
}
