package org.motechproject.ghana.national.configuration;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.quartz.SchedulerException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.util.Collections;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.motechproject.ghana.national.configuration.ScheduleNames.CWC_IPT_VACCINE;
import static org.motechproject.scheduletracking.api.domain.WindowName.due;
import static org.motechproject.scheduletracking.api.domain.WindowName.late;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-core.xml"})
public class IPTiVaccinationSchedulesTest extends BaseScheduleTrackingTest {

    @Before
    public void setUp() {
        super.setUp();
        preferredAlertTime = new Time(10, 10);
        externalId = "patient_id" + randomAlphabetic(6);
        scheduleName = CWC_IPT_VACCINE.getName();
    }

    @Test
    public void verifyChildCareIPT1ScheduleForChildOnStartOf9thWeekOfBirth() throws SchedulerException, ParseException {

        LocalDate childBirthDate = newDate("25-Jan-2012");
        mockToday(childBirthDate.plusWeeks(9));

        enrollmentId = enrollForIPTVaccine(childBirthDate);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(due, onDate("28-Mar-2012")),
                alert(late, onDate("4-Apr-2012")),
                alert(late, onDate("11-Apr-2012")),
                alert(late, onDate("18-Apr-2012")))
        );
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("02-May-2012", "00:00").toDate())));
    }

    @Test
    public void verifyChildCareIPT1ScheduleForChildOnStartOf11thWeekOfBirth() throws SchedulerException, ParseException {

        LocalDate childBirthDate = newDate("26-Jan-2012");
        mockToday(childBirthDate.plusWeeks(10));

        enrollmentId = enrollForIPTVaccine(childBirthDate);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(late, onDate("5-Apr-2012")),
                alert(late, onDate("12-Apr-2012")),
                alert(late, onDate("19-Apr-2012")))
        );
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("03-May-2012", "00:00").toDate())));
    }

    @Test
    public void verifyChildCareIPT1ScheduleForChildOnStartOf12thWeekOfBirth() throws SchedulerException, ParseException {

        LocalDate childBirthDate = newDate("20-Jan-2012");
        mockToday(childBirthDate.plusWeeks(11).plusDays(3));  //"9-Apr-2012" 11*7+3

        enrollmentId = enrollForIPTVaccine(childBirthDate);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(late, onDate("13-Apr-2012")))
        );
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("27-Apr-2012", "00:00").toDate())));
    }

    @Test
    public void verifyChildCareIPT1ScheduleForChildOnStartOf13thWeekOfBirth() throws SchedulerException, ParseException {

        LocalDate childBirthDate = newDate("2-Jan-2012");
        mockToday(childBirthDate.plusWeeks(12).plusDays(5));  // 31 Mar 12

        enrollmentId = enrollForIPTVaccine(childBirthDate);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), Collections.<TestAlert>emptyList());
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("09-Apr-2012", "00:00").toDate())));
    }

    @Test
    public void verifyChildCareIPT2ScheduleForChildOnStartOf13thWeekOfBirth() throws SchedulerException {

        LocalDate childBirthDate = newDate("2-Jan-2012");
        mockToday(childBirthDate.plusWeeks(12).plusDays(5));  // 31 Mar 12

        enrollmentId = enrollForIPTVaccine(childBirthDate);
        fulfilMilestoneOnVisitDate(newDate("13-Mar-2012"));
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(due, onDate("3-Apr-2012")),
                alert(late, onDate("10-Apr-2012")),
                alert(late, onDate("17-Apr-2012")),
                alert(late, onDate("24-Apr-2012")))
        );
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("08-May-2012", "00:00").toDate())));
    }

    @Test
    public void verifyChildCareIPT3ScheduleForChildOnStartOf13thWeekOfBirth() throws SchedulerException {

        LocalDate childBirthDate = newDate("3-Dec-2011");
        mockToday(childBirthDate.plusWeeks(12).plusDays(5));  // 31 Mar 12

        enrollmentId = enrollForIPTVaccine(childBirthDate);
        fulfilMilestoneOnVisitDate(newDate("15-Feb-2012"));

        fulfilMilestoneOnVisitDate(newDate("17-Mar-2012"));
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(due, onDate("7-Apr-2012")),
                alert(late, onDate("14-Apr-2012")),
                alert(late, onDate("21-Apr-2012")),
                alert(late, onDate("28-Apr-2012")))
        );
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("12-May-2012", "00:00").toDate())));
    }

    @Test
    public void verifyIPTi2TriggersTheSchedule() throws SchedulerException {

        mockToday(newDate("19-JAN-2012"));
        LocalDate referenceDate = newDate("15-JAN-2012");

        enrollmentId = scheduleAlertForIptiEnrolledWithHistory(referenceDate, "IPTi2");
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(due, onDate("05-FEB-2012")),
                alert(late, onDate("12-FEB-2012")),
                alert(late, onDate("19-FEB-2012")),
                alert(late, onDate("26-FEB-2012")))
        );
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("11-Mar-2012", "00:00").toDate())));
    }

    private String scheduleAlertForIptiEnrolledWithHistory(LocalDate lastIPTiDate, String milestoneName) {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest().setExternalId(PATIENT_ID)
                .setScheduleName(scheduleName).setPreferredAlertTime(preferredAlertTime)
                .setReferenceDate(lastIPTiDate).setStartingMilestoneName(milestoneName);
        return scheduleTrackingService.enroll(enrollmentRequest);
    }

    private void fulfilMilestoneOnVisitDate(LocalDate visitDate) {
        mockCurrentDate(visitDate);
        fulfillCurrentMilestone(visitDate);
    }

    private String enrollForIPTVaccine(LocalDate referenceDate) {
        return enroll(referenceDate);
    }
}
