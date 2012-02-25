package org.motechproject.ghana.national.configuration;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.util.DateUtil;
import org.quartz.SchedulerException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.motechproject.ghana.national.configuration.ScheduleNames.TTVaccine;
import static org.motechproject.util.DateUtil.newDate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-core.xml"})
public class TTVaccinationSchedulesTest extends BaseScheduleTrackingTest {

    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void verifyTTVaccinationScheduleFulfillmentHappensAfterTheLastAlert() throws SchedulerException {
        LocalDate firstDosageDate = mockToday(DateUtil.newDate(2000, 2, 1));

        enrollmentId = scheduleAlertForTTVaccination(firstDosageDate);
        fulfillCurrentMilestone(firstDosageDate, TTVaccine);

        assertAlerts(captureAlertsForNextMilestone(enrollmentId), dates(newDate(2000, 2, 22), newDate(2000, 2, 29), newDate(2000, 3, 7), newDate(2000, 3, 14)));

        fulfillCurrentMilestone(newDate(2000, 3, 22), TTVaccine);
        assertAlerts(captureAlertsForNextMilestone(enrollmentId), dates(newDate(2000, 8, 30), newDate(2000, 9, 9), newDate(2000, 9, 16), newDate(2000, 9, 23)));

        fulfillCurrentMilestone(newDate(2000, 10, 18), TTVaccine);
        assertAlerts(captureAlertsForNextMilestone(enrollmentId), dates(newDate(2001, 9, 12), newDate(2001, 9, 22), newDate(2001, 9, 29), newDate(2001, 10, 6)));

        fulfillCurrentMilestone(newDate(2001, 10, 15), TTVaccine);
        assertAlerts(captureAlertsForNextMilestone(enrollmentId), dates(newDate(2002, 9, 9), newDate(2002, 9, 19), newDate(2002, 9, 26), newDate(2002, 10, 3)));
    }

    @Test
    @Ignore
    public void verifyTTVaccinationScheduleGivenFirstDosageInThePast() throws SchedulerException {
        mockToday(DateUtil.newDate(2000, 2, 24));
        final LocalDate firstDosageDate = newDate(2000, 2, 1);

        enrollmentId = scheduleAlertForTTVaccination(firstDosageDate);
        fulfillCurrentMilestone(firstDosageDate, TTVaccine);

        assertAlerts(captureAlertsForNextMilestone(enrollmentId), dates(newDate(2000, 2, 29), newDate(2000, 3, 7), newDate(2000, 3, 14)));
    }

    private String scheduleAlertForTTVaccination(LocalDate firstDosageDate) {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest(PATIENT_ID, TTVaccine, preferredAlertTime, firstDosageDate);
        return scheduleTrackingService.enroll(enrollmentRequest);
    }
}
