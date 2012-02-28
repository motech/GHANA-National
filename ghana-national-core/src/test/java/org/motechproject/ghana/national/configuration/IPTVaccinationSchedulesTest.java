package org.motechproject.ghana.national.configuration;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ghana.national.vo.Pregnancy;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.util.DateUtil;
import org.quartz.SchedulerException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.motechproject.ghana.national.configuration.ScheduleNames.ANC_IPT_VACCINE;
import static org.motechproject.ghana.national.vo.Pregnancy.basedOnDeliveryDate;
import static org.motechproject.scheduletracking.api.domain.WindowName.*;
import static org.motechproject.util.DateUtil.newDate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-core.xml"})
public class IPTVaccinationSchedulesTest extends BaseScheduleTrackingTest {

    @Before
    public void setUp() {
        super.setUp();
        preferredAlertTime = new Time(10, 10);
        externalId = "patient_id" + randomAlphabetic(6);
        scheduleName = ANC_IPT_VACCINE;
    }

    @Test
    public void verifyPregnancyIPT1ScheduleForPatientOnStartOf12thWeekOfPregnancy() throws SchedulerException {

        Pregnancy pregnancy = basedOnDeliveryDate(newDate(2012, 9, 22));
        LocalDate dateOfConception = pregnancy.dateOfConception();

        mockToday(dateOfConception.plusWeeks(11));

        enrollmentId = enrollForIPTVaccine(dateOfConception);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(earliest, onDate(new LocalDate(2012, 3, 3))),
                alert(due, onDate(2012, 3, 10)),
                alert(late, onDate(2012, 3, 24)),
                alert(late, onDate(2012, 3, 31)),
                alert(late, onDate(2012, 4, 7)))
        );
    }

    @Test
    public void verifyCompleteIPTpScheduleForRegistrationOnMidOf12thWeekOfPregnancy() throws SchedulerException {

        Pregnancy pregnancy = basedOnDeliveryDate(newDate(2012, 9, 22));
        // 12 week start day is 3 Mar - 2012 and current date is 6-Mar
        mockCurrentDate(pregnancy.dateOfConception().plusWeeks(11).plusDays(3));

        enrollmentId = enrollForIPTVaccine(pregnancy.dateOfConception());
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(due, onDate(2012, 3, 10)),
                alert(late, onDate(2012, 3, 24)),
                alert(late, onDate(2012, 3, 31)),
                alert(late, onDate(2012, 4, 7))
        ));

        fulfilMilestoneOnVisitDate(new LocalDate(2012, 3, 15));
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(earliest, onDate(new LocalDate(2012, 4, 5))),
                alert(due, onDate(2012, 4, 5)),
                alert(late, onDate(2012, 4, 12)),
                alert(late, onDate(2012, 4, 19)),
                alert(late, onDate(2012, 4, 26))
        ));

        fulfilMilestoneOnVisitDate(new LocalDate(2012, 5, 7));
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(earliest, onDate(new LocalDate(2012, 5, 28))),
                alert(due, onDate(2012, 5, 28)),
                alert(late, onDate(2012, 6, 4)),
                alert(late, onDate(2012, 6, 11)),
                alert(late, onDate(2012, 6, 18))
        ));
    }

    @Test
    public void verifyCompleteIPTpScheduleForPatientRegistrationOn13thOfPregnancy() throws SchedulerException {

        Pregnancy pregnancy = basedOnDeliveryDate(newDate(2012, 9, 22));
        LocalDate mar16th2012 = pregnancy.dateOfConception().plusWeeks(12).plusDays(6);
        mockCurrentDate(mar16th2012);

        enrollmentId = enrollForIPTVaccine(pregnancy.dateOfConception());
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(late, onDate(2012, 3, 24)),
                alert(late, onDate(2012, 3, 31)),
                alert(late, onDate(2012, 4, 7))
        ));

        fulfilMilestoneOnVisitDate(new LocalDate(2012, 4, 1));
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(earliest, onDate(2012, 4, 22)),
                alert(due, onDate(2012, 4, 22)),
                alert(late, onDate(2012, 4, 29)),
                alert(late, onDate(2012, 5, 6)),
                alert(late, onDate(2012, 5, 13))
        ));

        fulfilMilestoneOnVisitDate(new LocalDate(2012, 5, 7));
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(earliest, onDate(2012, 5, 28)),
                alert(due, onDate(2012, 5, 28)),
                alert(late, onDate(2012, 6, 4)),
                alert(late, onDate(2012, 6, 11)),
                alert(late, onDate(2012, 6, 18))
        ));
    }

    @Test
    public void verifyPregnancyIPT1ScheduleForPatientRegistrationOn14thWeekOfPregnancy() throws SchedulerException {

        Pregnancy pregnancy = basedOnDeliveryDate(newDate(2012, 9, 22));
        LocalDate mar22nd2012_w14 = pregnancy.dateOfConception().plusWeeks(13).plusDays(5);
        mockCurrentDate(mar22nd2012_w14);

        enrollmentId = enrollForIPTVaccine(pregnancy.dateOfConception());
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(late, onDate(2012, 3, 24)),
                alert(late, onDate(2012, 3, 31)),
                alert(late, onDate(2012, 4, 7))
        ));
    }

    @Test
    public void verifyPregnancyIPT2ScheduleForPatientRegistrationWithVisitOn15Mar() throws SchedulerException {

        Pregnancy pregnancy = basedOnDeliveryDate(newDate(2012, 9, 22));
        mockCurrentDate(pregnancy.dateOfConception().plusWeeks(11));

        enrollmentId = enrollForIPTVaccine(pregnancy.dateOfConception());

        fulfilMilestoneOnVisitDate(new LocalDate(2012, 3, 15));
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(earliest, onDate(2012, 4, 5)),
                alert(due, onDate(2012, 4, 5)),
                alert(late, onDate(2012, 4, 12)),
                alert(late, onDate(2012, 4, 19)),
                alert(late, onDate(2012, 4, 26))
        ));
    }

    @Test
    public void verifyPregnancyIPT3ScheduleForPatientRegistrationWithIPT1On1AprAndIPT2On7May() throws SchedulerException {

        Pregnancy pregnancy = basedOnDeliveryDate(newDate(2012, 9, 22));
        mockCurrentDate(pregnancy.dateOfConception().plusWeeks(11));

        enrollmentId = enrollForIPTVaccine(pregnancy.dateOfConception());

        fulfilMilestoneOnVisitDate(new LocalDate(2012, 4, 1));
        fulfilMilestoneOnVisitDate(new LocalDate(2012, 5, 7));
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(earliest, onDate(2012, 5, 28)),
                alert(due, onDate(2012, 5, 28)),
                alert(late, onDate(2012, 6, 4)),
                alert(late, onDate(2012, 6, 11)),
                alert(late, onDate(2012, 6, 18))
        ));
    }

    private void fulfilMilestoneOnVisitDate(LocalDate visitDate) {
        mockCurrentDate(visitDate);
        fulfillCurrentMilestone(visitDate);
    }

    private String enrollForIPTVaccine(LocalDate referenceDate) {
        EnrollmentRequest enrollmentRequest = new EnrollmentRequest(externalId, scheduleName, preferredAlertTime, referenceDate, DateUtil.today(), null);
        return scheduleTrackingService.enroll(enrollmentRequest);
    }
}
