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

import java.text.ParseException;

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

    @Test
    public void verifyPNC1BabyAlertsIfRegisteredAsSoonAsTheChildIsBorn() throws SchedulerException, ParseException {
        mockToday(newDateWithTime("24-FEB-2000", "10:05"));
        DateTime dateOfBirth = newDateWithTime("24-FEB-2000", "10:00");

        enrollmentId = scheduleAlertForPNCBaby(dateOfBirth, 10, 0, PNC_MOTHER_1);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(due, newDateWithTime("24-FEB-2000", "16:00").toDate()),
                alert(late, newDateWithTime("24-FEB-2000", "22:00").toDate()),
                alert(max, newDateWithTime("26-FEB-2000", "10:00").toDate())
        ));
    }

    @Test
    public void verifyPNC2BabyAlertsIfRegisteredAsSoonAsTheChildIsBorn() throws SchedulerException, ParseException {
        mockToday(newDateWithTime("01-MAR-2000", "10:05"));
        DateTime dateOfBirth = newDateWithTime("01-MAR-2000", "10:00");

        enrollmentId = scheduleAlertForPNCBaby(dateOfBirth, 10, 0, PNC_MOTHER_2);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(earliest, newDateWithTime("06-MAR-2000", "10:00").toDate()),
                alert(due, newDateWithTime("08-MAR-2000", "10:00").toDate()),
                alert(max, newDateWithTime("13-MAR-2000", "10:00").toDate())
        ));
    }

    @Test
    public void verifyPNC3BabyAlertsIfRegisteredAsSoonAsTheChildIsBorn() throws SchedulerException, ParseException {
        mockToday(newDateWithTime("01-MAR-2000", "10:05"));
        DateTime dateOfBirth = newDateWithTime("01-MAR-2000", "10:00");

        enrollmentId = scheduleAlertForPNCBaby(dateOfBirth, 10, 0, PNC_MOTHER_3);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(earliest, newDateWithTime("13-MAR-2000", "10:00").toDate()),
                alert(due, newDateWithTime("15-MAR-2000", "10:00").toDate()),
                alert(max, newDateWithTime("22-MAR-2000", "10:00").toDate())
        ));
    }

    private String scheduleAlertForPNCBaby(DateTime birthDate, Integer hour, Integer minute, String pncScheduleName) {
        scheduleName = pncScheduleName;
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest(PATIENT_ID, scheduleName, null, birthDate.toLocalDate(), new Time(hour, minute), null, null, null);
        return scheduleTrackingService.enroll(enrollmentRequest);
    }
}
