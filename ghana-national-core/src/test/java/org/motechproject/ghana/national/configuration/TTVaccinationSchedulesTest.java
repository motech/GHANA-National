package org.motechproject.ghana.national.configuration;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.quartz.SchedulerException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;

import static org.motechproject.ghana.national.configuration.ScheduleNames.TT_VACCINATION_VISIT;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-core.xml"})
public class TTVaccinationSchedulesTest extends BaseScheduleTrackingTest {

    @Before
    public void setUp() {
        super.setUp();
        scheduleName = TT_VACCINATION_VISIT;
    }

    @Test
    public void verifyTTVaccinationScheduleFulfillmentHappensAfterTheLastAlert() throws SchedulerException, ParseException {
        LocalDate firstDosageDate = mockToday(newDate("01-FEB-2000"));

        enrollmentId = scheduleAlertForTTVaccination(firstDosageDate);
        fulfillCurrentMilestone(firstDosageDate);

        assertAlerts(captureAlertsForNextMilestone(enrollmentId), dates(newDate("22-FEB-2000"), newDate("29-FEB-2000"), newDate("07-MAR-2000"), newDate("14-MAR-2000")));

        fulfillCurrentMilestone(newDate("22-MAR-2000"));
        assertAlerts(captureAlertsForNextMilestone(enrollmentId), dates(newDate("30-AUG-2000"), newDate("09-SEP-2000"), newDate("16-SEP-2000"), newDate("23-SEP-2000")));

        fulfillCurrentMilestone(newDate("18-OCT-2000"));
        assertAlerts(captureAlertsForNextMilestone(enrollmentId), dates(newDate("12-SEP-2001"), newDate("22-SEP-2001"), newDate("29-SEP-2001"), newDate("06-OCT-2001")));

        fulfillCurrentMilestone(newDate("15-OCT-2001"));
        assertAlerts(captureAlertsForNextMilestone(enrollmentId), dates(newDate("09-SEP-2002"), newDate("19-SEP-2002"), newDate("26-SEP-2002"), newDate("03-OCT-2002")));
    }

    @Test
    @Ignore
    public void verifyTTVaccinationScheduleGivenFirstDosageInThePast() throws SchedulerException, ParseException {
        mockToday(newDate("24-FEB-2000"));
        final LocalDate firstDosageDate = newDate("01-FEB-2000");

        enrollmentId = scheduleAlertForTTVaccination(firstDosageDate);
        fulfillCurrentMilestone(firstDosageDate);

        assertAlerts(captureAlertsForNextMilestone(enrollmentId), dates(newDate("29-FEB-2000"), newDate("07-MAR-2000"), newDate("14-MAR-2000")));
    }

    private String scheduleAlertForTTVaccination(LocalDate firstDosageDate) {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest(PATIENT_ID, scheduleName, preferredAlertTime, firstDosageDate, null, null);
        return scheduleTrackingService.enroll(enrollmentRequest);
    }
}
