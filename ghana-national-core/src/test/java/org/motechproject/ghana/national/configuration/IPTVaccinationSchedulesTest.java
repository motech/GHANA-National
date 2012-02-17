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
import org.quartz.SimpleTrigger;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.motechproject.ghana.national.vo.Pregnancy.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/testApplicationContext-core.xml"})
public class IPTVaccinationSchedulesTest extends BaseScheduleTrackingTest {

    private Time preferredAlertTime;
    String externalId;

    @Before
    public void setUp() {
        super.setUp();
        preferredAlertTime = new Time(10, 10);
        externalId = "patient_id" + randomAlphabetic(6);
    }

    @Test
    public void verifyPregnancyIPT1ScheduleForPatientInStartDayOf12thWeekOfPregnancy() throws SchedulerException {

        Pregnancy pregnancy = basedOnDeliveryDate(DateUtil.newDate(2012, 9, 22));
        final LocalDate programStartDate12WeekStartDay = pregnancy.dateOfConception().plusWeeks(11);

        mockCurrentDate(programStartDate12WeekStartDay);

        EnrollmentRequest enrollmentRequest = new EnrollmentRequest(externalId, CareScheduleNames.ANC_IPT_VACCINE_START_WEEK_12, preferredAlertTime, programStartDate12WeekStartDay);
        String enrollmentId = scheduleTrackingService.enroll(enrollmentRequest);

        ArrayList<Date> expectedAlerts = new ArrayList<Date>() {{
            add(onDate(programStartDate12WeekStartDay, 0, preferredAlertTime));
            add(onDate(programStartDate12WeekStartDay, 1, preferredAlertTime));
            add(onDate(programStartDate12WeekStartDay, 2, preferredAlertTime));
            add(onDate(programStartDate12WeekStartDay, 3, preferredAlertTime));
        }};

        assertEquals(new LocalDate(2012, 3, 10), programStartDate12WeekStartDay.plusWeeks(1));
        List<SimpleTrigger> alerts = captureAlertsForNextMilestone(enrollmentId);
        assertAlerts(alerts, expectedAlerts);
    }

    @Test
    public void verifyPregnancyIPT1ScheduleForPatientInMidOf12thOrExactStartOf13thWeekOfPregnancy() throws SchedulerException {

        // 12 week start day is 3 Mar - 2012
        Pregnancy pregnancy = basedOnDeliveryDate(DateUtil.newDate(2012, 9, 22));
        LocalDate todayAsMidOf12Week_6Mar = pregnancy.dateOfConception().plusWeeks(11).plusDays(3);
        LocalDate startDateAs13WeekStartDay = pregnancy.dateOfConception().plusWeeks(12);

        mockCurrentDate(todayAsMidOf12Week_6Mar);

        String enrollmentId = scheduleTrackingService.enroll(
                new EnrollmentRequest(externalId, CareScheduleNames.ANC_IPT_VACCINE_START_WEEK_13, preferredAlertTime, startDateAs13WeekStartDay));

        final LocalDate march10th2012 = new LocalDate(2012, 3, 10);
        assertEquals(march10th2012, startDateAs13WeekStartDay);

        assertAlerts(captureAlertsForNextMilestone(enrollmentId), new ArrayList<Date>() {{
            add(onDate(march10th2012, 0, preferredAlertTime));
            add(onDate(march10th2012, 0, preferredAlertTime));
            add(onDate(march10th2012, 1, preferredAlertTime));
            add(onDate(march10th2012, 2, preferredAlertTime));
        }});

        final LocalDate visitDateOnMarch15th = march10th2012.plusDays(5);
        mockCurrentDate(visitDateOnMarch15th);
        scheduleTrackingService.fulfillCurrentMilestone(externalId, CareScheduleNames.ANC_IPT_VACCINE_START_WEEK_13);
        final LocalDate apr12th2012 = visitDateOnMarch15th.plusWeeks(4);

        assertAlerts(captureAlertsForNextMilestone(enrollmentId), new ArrayList<Date>() {{
            add(onDate(apr12th2012, -1, preferredAlertTime));
            add(onDate(apr12th2012, 0, preferredAlertTime));
        }});

    }

}
