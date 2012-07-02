package org.motechproject.ghana.national.configuration;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.quartz.SchedulerException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-core.xml"})
public class CWCOpv0SchedulesTest extends BaseScheduleTrackingTest {

    @Before
    public void setUp() {
        super.setUp();
        scheduleName = ScheduleNames.CWC_OPV_0.getName();
    }

    @Test
    public void shouldVerifyOPV0scheduleOnBirthOfChild() throws SchedulerException {
        LocalDate dateOfBirth = newDate("1-APR-2012");
        mockToday(dateOfBirth);

        enrollmentId = scheduleAlertForOPV(dateOfBirth);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(WindowName.late, onDate("03-APR-2012")),
                alert(WindowName.late, onDate("10-APR-2012"))
        ));
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("22-Apr-2012", "00:00").toDate())));
    }

    @Test
    public void shouldVerifyOPV0scheduleOnRegistrationAtAgeUptoTenDays() throws SchedulerException {
        LocalDate dateOfBirth = newDate("2-JAN-2012");
        LocalDate dateOfRegistration = newDate("12-JAN-2012");
        mockToday(dateOfRegistration);

        enrollmentId = scheduleAlertForOPV(dateOfBirth);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), new ArrayList<TestAlert>());
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("23-Jan-2012", "00:00").toDate())));
    }

    @Test
    public void shouldVerifyOPV0scheduleOnRegistrationAtAgeOneWeek() throws SchedulerException {
        LocalDate dateOfBirth = newDate("2-JAN-2012");
        LocalDate dateOfRegistration = newDate("7-JAN-2012");
        mockToday(dateOfRegistration);

        enrollmentId = scheduleAlertForOPV(dateOfBirth);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(WindowName.late, onDate("11-JAN-2012"))
        ));
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("23-Jan-2012", "00:00").toDate())));
    }

    private String scheduleAlertForOPV(LocalDate referenceDate) {
        return enroll(referenceDate);
    }
}
