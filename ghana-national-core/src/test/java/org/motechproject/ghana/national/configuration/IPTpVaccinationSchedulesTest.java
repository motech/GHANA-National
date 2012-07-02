package org.motechproject.ghana.national.configuration;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.ghana.national.vo.Pregnancy;
import org.motechproject.model.Time;
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
import static org.motechproject.ghana.national.configuration.ScheduleNames.ANC_IPT_VACCINE;
import static org.motechproject.ghana.national.vo.Pregnancy.basedOnDeliveryDate;
import static org.motechproject.scheduletracking.api.domain.WindowName.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-core.xml"})
public class IPTpVaccinationSchedulesTest extends BaseScheduleTrackingTest {

    @Before
    public void setUp() {
        super.setUp();
        preferredAlertTime = new Time(10, 10);
        externalId = "patient_id" + randomAlphabetic(6);
        scheduleName = ANC_IPT_VACCINE.getName();
    }

    @Test
    public void verifyPregnancyIPT1ScheduleForPatientOnStartOf5thWeekOfPregnancy() throws SchedulerException, ParseException {

        Pregnancy pregnancy = basedOnDeliveryDate(newDate("22-SEP-2012"));
        LocalDate dateOfConception = pregnancy.dateOfConception();

        mockToday(dateOfConception.plusWeeks(5).plusDays(4)); // 25-Jan-2012

        enrollmentId = enrollForIPTVaccine(dateOfConception);
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(earliest, onDate("10-MAR-2012")),
                alert(due, onDate("17-MAR-2012")),
                alert(late, onDate("31-MAR-2012")),
                alert(late, onDate("7-APR-2012")),
                alert(late, onDate("14-APR-2012")))
        );
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("28-Apr-2012", "00:00").toDate())));
    }

    @Test
    public void verifyCompleteIPTpScheduleForRegistrationOnMidOf12thWeekOfPregnancy() throws SchedulerException, ParseException {

        Pregnancy pregnancy = basedOnDeliveryDate(newDate("22-SEP-2012"));        
        mockCurrentDate(pregnancy.dateOfConception().plusWeeks(11).plusDays(3));  // 3-Mar-2012

        enrollmentId = enrollForIPTVaccine(pregnancy.dateOfConception());
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(earliest, onDate("10-MAR-2012")),
                alert(due, onDate("17-MAR-2012")),
                alert(late, onDate("31-MAR-2012")),
                alert(late, onDate("07-APR-2012")),
                alert(late, onDate("14-APR-2012"))
        ));
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("28-Apr-2012", "00:00").toDate())));

        fulfilMilestoneOnVisitDate(newDate("15-MAR-2012"));
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(due, onDate("5-APR-2012")),
                alert(late, onDate("12-APR-2012")),
                alert(late, onDate("19-APR-2012")),
                alert(late, onDate("26-APR-2012"))
        ));
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("10-May-2012", "00:00").toDate())));

        fulfilMilestoneOnVisitDate(newDate("07-MAY-2012"));
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(due, onDate("28-MAY-2012")),
                alert(late, onDate("04-JUN-2012")),
                alert(late, onDate("11-JUN-2012")),
                alert(late, onDate("18-JUN-2012"))
        ));
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("02-Jul-2012", "00:00").toDate())));
    }

    @Test
    public void verifyCompleteIPTpScheduleForPatientRegistrationOn13thOfPregnancy() throws SchedulerException, ParseException {

        Pregnancy pregnancy = basedOnDeliveryDate(newDate("22-SEP-2012"));
        LocalDate mar16th2012 = pregnancy.dateOfConception().plusWeeks(12).plusDays(6);
        mockCurrentDate(mar16th2012);

        enrollmentId = enrollForIPTVaccine(pregnancy.dateOfConception());
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(due, onDate("17-MAR-2012")),
                alert(late, onDate("31-MAR-2012")),
                alert(late, onDate("07-APR-2012")),
                alert(late, onDate("14-APR-2012"))
        ));
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("28-Apr-2012", "00:00").toDate())));

        fulfilMilestoneOnVisitDate(newDate("01-APR-2012"));
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(due, onDate("22-APR-2012")),
                alert(late, onDate("29-APR-2012")),
                alert(late, onDate("06-MAY-2012")),
                alert(late, onDate("13-MAY-2012"))
        ));
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("27-May-2012", "00:00").toDate())));

        fulfilMilestoneOnVisitDate(newDate("07-MAY-2012"));
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(due, onDate("28-MAY-2012")),
                alert(late, onDate("04-JUN-2012")),
                alert(late, onDate("11-JUN-2012")),
                alert(late, onDate("18-JUN-2012"))
        ));
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("02-Jul-2012", "00:00").toDate())));
    }

    @Test
    public void verifyPregnancyIPT1ScheduleForPatientRegistrationOn14thWeekOfPregnancy() throws SchedulerException, ParseException {

        Pregnancy pregnancy = basedOnDeliveryDate(newDate("22-SEP-2012"));
        LocalDate mar22nd2012_w14 = pregnancy.dateOfConception().plusWeeks(13).plusDays(5);
        mockCurrentDate(mar22nd2012_w14);

        enrollmentId = enrollForIPTVaccine(pregnancy.dateOfConception());
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(late, onDate("31-MAR-2012")),
                alert(late, onDate("7-APR-2012")),
                alert(late, onDate("14-APR-2012"))
        ));
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("28-Apr-2012", "00:00").toDate())));
    }

    @Test
    public void verifyPregnancyIPT1ScheduleForPatientRegistrationOn15thWeekOfPregnancy() throws SchedulerException, ParseException {

        Pregnancy pregnancy = basedOnDeliveryDate(newDate("22-SEP-2012"));
        LocalDate mar29th2012_w15 = pregnancy.dateOfConception().plusWeeks(14).plusDays(5);
        mockCurrentDate(mar29th2012_w15);

        enrollmentId = enrollForIPTVaccine(pregnancy.dateOfConception());
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(late, onDate("31-MAR-2012")),
                alert(late, onDate("7-APR-2012")),
                alert(late, onDate("14-APR-2012"))
        ));
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("28-Apr-2012", "00:00").toDate())));
    }

    @Test
    public void verifyPregnancyIPT1ScheduleForPatientRegistrationOn16thWeekOfPregnancy() throws SchedulerException, ParseException {

        Pregnancy pregnancy = basedOnDeliveryDate(newDate("22-SEP-2012"));
        LocalDate apr5th2012_w16 = pregnancy.dateOfConception().plusWeeks(15).plusDays(5);
        mockCurrentDate(apr5th2012_w16);

        enrollmentId = enrollForIPTVaccine(pregnancy.dateOfConception());
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(late, onDate("7-APR-2012")),
                alert(late, onDate("14-APR-2012"))
        ));
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("28-Apr-2012", "00:00").toDate())));
    }

    @Test
    public void verifyPregnancyIPT1ScheduleForPatientRegistrationOn18thWeekOfPregnancy() throws SchedulerException, ParseException {

        Pregnancy pregnancy = basedOnDeliveryDate(newDate("22-SEP-2012"));
        LocalDate apr12th2012_w17 = pregnancy.dateOfConception().plusWeeks(17).plusDays(1);
        mockCurrentDate(apr12th2012_w17);

        enrollmentId = enrollForIPTVaccine(pregnancy.dateOfConception());
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), Collections.<TestAlert>emptyList());
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("28-Apr-2012", "00:00").toDate())));
    }

    @Test
    public void verifyPregnancyIPT2ScheduleForPatientRegistrationWithVisitOn15Mar() throws SchedulerException, ParseException {

        Pregnancy pregnancy = basedOnDeliveryDate(newDate("22-SEP-2012"));
        mockCurrentDate(pregnancy.dateOfConception().plusWeeks(11));

        enrollmentId = enrollForIPTVaccine(pregnancy.dateOfConception());

        fulfilMilestoneOnVisitDate(newDate("15-MAR-2012"));
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(due, onDate("5-APR-2012")),
                alert(late, onDate("12-APR-2012")),
                alert(late, onDate("19-APR-2012")),
                alert(late, onDate("26-APR-2012"))
        ));
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("10-May-2012", "00:00").toDate())));
    }

    @Test
    public void verifyPregnancyIPT3ScheduleForPatientRegistrationWithIPT1On1AprAndIPT2On7May() throws SchedulerException, ParseException {

        Pregnancy pregnancy = basedOnDeliveryDate(newDate("22-SEP-2012"));
        mockCurrentDate(pregnancy.dateOfConception().plusWeeks(11));

        enrollmentId = enrollForIPTVaccine(pregnancy.dateOfConception());

        fulfilMilestoneOnVisitDate(newDate("01-APR-2012"));
        fulfilMilestoneOnVisitDate(newDate("07-MAY-2012"));
        assertTestAlerts(captureAlertsForNextMilestone(enrollmentId), asList(
                alert(due, onDate("28-MAY-2012")),
                alert(late, onDate("04-JUN-2012")),
                alert(late, onDate("11-JUN-2012")),
                alert(late, onDate("18-JUN-2012"))
        ));
        assertThat(getDefaultmentDate(enrollmentId), is(equalTo(newDateWithTime("02-Jul-2012", "00:00").toDate())));
    }

    private void fulfilMilestoneOnVisitDate(LocalDate visitDate) {
        mockCurrentDate(visitDate);
        fulfillCurrentMilestone(visitDate);
    }

    private String enrollForIPTVaccine(LocalDate referenceDate) {
        return enroll(referenceDate);
    }
}
