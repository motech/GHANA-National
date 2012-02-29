package org.motechproject.ghana.national.configuration;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.model.Time;
import org.quartz.SchedulerException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.motechproject.ghana.national.configuration.ScheduleNames.CWC_YELLOW_FEVER_VACCINE;
import static org.motechproject.scheduletracking.api.domain.WindowName.due;
import static org.motechproject.scheduletracking.api.domain.WindowName.late;
import static org.motechproject.util.DateUtil.newDateTime;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-core.xml"})
public class CWCYellowFeverVaccineSchedulesTest extends BaseScheduleTrackingTest {

    @Before
    public void setUp() {
        super.setUp();
        preferredAlertTime = new Time(10, 10);
        externalId = "patient_id" + randomAlphabetic(6);
        scheduleName = CWC_YELLOW_FEVER_VACCINE;
    }

    @Test
    public void verifyScheduleIfEnrolledOnStartOfDateOfBirth() throws SchedulerException {

        LocalDate childBirthDate = newDate("30-Dec-2011");
        mockToday(childBirthDate);

        enrollmentId = enroll(childBirthDate);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(due, onDate("23-Sep-2012")),
                alert(late, onDate("21-Oct-2012")),
                alert(late, onDate("28-Oct-2012")),
                alert(late, onDate("4-Nov-2012")))
        );
    }

        @Test
    public void verifyScheduleIfEnrolledJustBefore9thMonthToGetAlerts() throws SchedulerException {

        LocalDate childBirthDate = newDate("3-Jan-2012");
        mockToday(childBirthDate.plusMonths(9).minusDays(3)); // 30-Sep-2012

        enrollmentId = enroll(childBirthDate);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(late, onDate("24-Oct-2012")),
                alert(late, onDate("31-Oct-2012")),
                alert(late, onDate("7-Nov-2012")))
        );
    }
    
    @Test
    public void verifyScheduleIfEnrolledAtExactly9thMonthToGetAlerts() throws SchedulerException {

        LocalDate childBirthDate = newDate("4-Jan-2012");
        mockToday(childBirthDate.plusMonths(9)); // 4-Oct-2012

        enrollmentId = enroll(childBirthDate);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(late, onDate("25-Oct-2012")),
                alert(late, onDate("1-Nov-2012")),
                alert(late, onDate("8-Nov-2012")))
        );
    }

    @Test
    public void verifyScheduleIfEnrolledAt3WeeksPast9thMonthToGetAlerts() throws SchedulerException {

        LocalDate childBirthDate = newDate("3-Oct-2011");
        mockToday(childBirthDate.plusMonths(9).plusWeeks(2).plusDays(3)); // 20-Jul-2012 00: 00 -which is before delivery time 10: 10

        enrollmentId = enroll(childBirthDate);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(late, onDate("24-Jul-2012")),
                alert(late, onDate("31-Jul-2012")),
                alert(late, onDate("7-Aug-2012")))
        );
    }

    @Test
    public void verifyScheduleIfEnrolledAt4WeeksPast9thMonthToGetAlerts() throws SchedulerException {
        // change Test excel data for this
        LocalDate childBirthDate = newDate("11-Oct-2012");
        mockToday(newDateTime(childBirthDate.plusMonths(9).plusWeeks(4), new Time(10, 9))); // 8-Aug-2013 10:09 before delivery time
        enrollmentId = enroll(childBirthDate);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(late, onDate("08-Aug-2013")),
                alert(late, onDate("15-Aug-2013")))
        );

        deleteAllJobs();

        mockToday(newDateTime(childBirthDate.plusMonths(9).plusWeeks(4), new Time(10, 10))); // 8-Aug-2013 10:10 exact delivery time
        enrollmentId = enroll(childBirthDate);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(late, onDate("15-Aug-2013")))
        );
    }

    @Test
    public void verifyScheduleIfEnrolledAt5WeeksPast9thMonthToGetAlerts() throws SchedulerException {

        LocalDate childBirthDate = newDate("11-Oct-2012");
        mockToday(newDateTime(childBirthDate.plusMonths(9).plusWeeks(5), new Time(10, 9))); // 15-Aug-2013 10:09 before delivery time
        enrollmentId = enroll(childBirthDate);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
            alert(late, onDate("15-Aug-2013")))
        );

        deleteAllJobs();

        mockToday(newDateTime(childBirthDate.plusMonths(9).plusWeeks(5).plusDays(1), new Time(10, 9))); // 16-Aug-2013 10:09 before delivery time
        enrollmentId = enroll(childBirthDate);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), Collections.<TestAlert>emptyList());
    }

    @Test
    public void verifyScheduleIfEnrolledAt6WeeksPast9thMonthToGetAlerts() throws SchedulerException {
        LocalDate childBirthDate = newDate("29-Dec-2011");
        mockToday(newDateTime(childBirthDate.plusMonths(9).plusWeeks(5).plusDays(3), new Time(10, 9))); // 6-Nov-2012 10:09 before delivery time
        enrollmentId = enroll(childBirthDate);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), Collections.<TestAlert>emptyList());
    }
}
